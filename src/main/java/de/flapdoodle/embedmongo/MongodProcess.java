/**
 * Copyright (C) 2011
 * Michael Mosmann <michael@mosmann.de>
 * Martin Jöhren <m.joehren@googlemail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.embedmongo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.Bytes;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.QueryBuilder;
import com.mongodb.WriteConcern;

import de.flapdoodle.embedmongo.config.MongodConfig;
import de.flapdoodle.embedmongo.distribution.Distribution;
import de.flapdoodle.embedmongo.distribution.Platform;
import de.flapdoodle.embedmongo.io.ConsoleOutput;
import de.flapdoodle.embedmongo.io.LogWatch;
import de.flapdoodle.embedmongo.runtime.NUMA;
import de.flapdoodle.embedmongo.runtime.ProcessControl;

public class MongodProcess {

	static final Logger _logger = Logger.getLogger(MongodProcess.class.getName());

	private final MongodConfig _config;
	private final MongodExecutable _mongodExecutable;
	private ProcessControl _process;
	private ConsoleOutput _consoleOutput;

	private File _dbDir;

	boolean _processKilled = false;
	boolean _stopped = false;

	private Distribution _distribution;

	public MongodProcess(Distribution distribution, MongodConfig config, MongodExecutable mongodExecutable)
			throws IOException {
		_config = config;
		_mongodExecutable = mongodExecutable;
		_distribution=distribution;

		try {
			File dbDir;
			if (config.getDatabaseDir() != null) {
				dbDir = Files.createOrCheckDir(config.getDatabaseDir());
			} else {
				dbDir = Files.createTempDir("embedmongo-db");
				_dbDir = dbDir;
			}
			//			ProcessBuilder processBuilder = new ProcessBuilder(enhanceCommandLinePlattformSpecific(distribution,
			//					getCommandLine(_config, _mongodExecutable.getFile(), dbDir)));
			//			processBuilder.redirectErrorStream();
			//			_process = new ProcessControl(processBuilder.start());
			_process = ProcessControl.fromCommandLine(enhanceCommandLinePlattformSpecific(distribution,
					getCommandLine(_config, _mongodExecutable.getFile(), dbDir)));

			Runtime.getRuntime().addShutdownHook(new JobKiller());

			if (LogWatch.waitForStart(_process.getReader(), "waiting for connections on port", "failed", 20000)) {
				_consoleOutput = new ConsoleOutput(_process.getReader());
				_consoleOutput.setDaemon(true);
				_consoleOutput.start();
			} else {
				throw new IOException("Could not start mongod process");
			}

		} catch (IOException iox) {
			stop();
			throw iox;
		}
	}

	private static List<String> getCommandLine(MongodConfig config, File mongodExecutable, File dbDir) {
		List<String> ret = new ArrayList<String>();
		ret.addAll(Arrays.asList(mongodExecutable.getAbsolutePath(), "-v", "--port", "" + config.getPort(), "--dbpath", ""
				+ dbDir.getAbsolutePath(), "--noprealloc", "--nohttpinterface", "--smallfiles"));
		if (config.isIpv6()) {
			ret.add("--ipv6");
		}
		return ret;
	}

	private List<String> enhanceCommandLinePlattformSpecific(Distribution distribution, List<String> commands) {
		if (NUMA.isNUMA(distribution.getPlatform())) {
			switch (distribution.getPlatform()) {
				case Linux:
					List<String> ret = new ArrayList<String>();
					ret.add("numactl");
					ret.add("--interleave=all");
					ret.addAll(commands);
					return ret;
				default:
					_logger.warning("NUMA Plattform detected, but not supported.");
			}
		}
		return commands;
	}

	public synchronized void stop() {
		if (!_stopped) {

			sendStopToMongoInstance();

			if (_process != null) {
				_process.stop();
			}
			waitForProcessGotKilled();

			if ((_dbDir != null) && (!Files.forceDelete(_dbDir)))
				_logger.warning("Could not delete temp db dir: " + _dbDir);

			if (_mongodExecutable.getFile() != null) {
				if (!Files.forceDelete(_mongodExecutable.getFile())) {
					_stopped = true;
					_logger.warning("Could not delete mongod executable NOW: " + _mongodExecutable.getFile());
				}
			}
		}
	}

	private void sendStopToMongoInstance() {
		if (_distribution.getPlatform()==Platform.Windows) {
			_logger.warning("\n" +
					"------------------------------------------------\n" +
					"On windows stopping mongod process could take too much time.\n" +
					"We will send shutdown to db for speedup.\n" +
					"This will cause some logging of exceptions which we can not suppress.\n" +
					"------------------------------------------------");
			try {
				Mongo mongo = new Mongo("localhost", _config.getPort());
				DB db = mongo.getDB("admin");
	//			db.doEval("db.shutdownServer();");
				db.command(new BasicDBObject("shutdown", 1).append("force", true));
			} catch (UnknownHostException e) {
				_logger.log(Level.SEVERE, "sendStop", e);
			} catch (MongoException e) {
	//			_logger.log(Level.SEVERE, "sendStop", e);
			}
		}
	}

	/**
	 * It may happen in tests, that the process is currently using some files in
	 * the temp directory, e.g. journal files (journal/j._0) and got killed at
	 * that time, so it takes a bit longer to kill the process. So we just wait
	 * for a second (in 10 ms steps) that the process got really killed.
	 */
	private void waitForProcessGotKilled() {
		final Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			public void run() {
				try {
					_process.waitFor();
				} catch (InterruptedException e) {
					_logger.severe(e.getMessage());
				} finally {
					_processKilled = true;
					timer.cancel();
				}
			}
		}, 0, 10);
		// wait for max. 1 second that process got killed

		int countDown = 100;
		while (!_processKilled && (countDown-- > 0))
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				_logger.severe(e.getMessage());
			}
		if (!_processKilled) {
			timer.cancel();
			throw new IllegalStateException("Couldn't kill mongod process!");
		}
	}

	class JobKiller extends Thread {

		@Override
		public void run() {
			MongodProcess.this.stop();
		}
	}
}
