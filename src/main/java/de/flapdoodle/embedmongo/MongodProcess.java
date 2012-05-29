/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
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
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.flapdoodle.embedmongo.config.MongodConfig;
import de.flapdoodle.embedmongo.config.MongodProcessOutputConfig;
import de.flapdoodle.embedmongo.distribution.Distribution;
import de.flapdoodle.embedmongo.io.LogWatchStreamProcessor;
import de.flapdoodle.embedmongo.io.Processors;
import de.flapdoodle.embedmongo.io.StreamToLineProcessor;
import de.flapdoodle.embedmongo.runtime.Mongod;
import de.flapdoodle.embedmongo.runtime.Network;
import de.flapdoodle.embedmongo.runtime.ProcessControl;

public class MongodProcess {

	static final Logger _logger = Logger.getLogger(MongodProcess.class.getName());

	private final MongodConfig _config;
	private final MongodProcessOutputConfig _outputConfig;
	private final MongodExecutable _mongodExecutable;
	private ProcessControl _process;
	private int _mongodProcessId;
	//	private ConsoleOutput _consoleOutput;

	private File _dbDir;

	boolean _stopped = false;

	private Distribution _distribution;

	public MongodProcess(Distribution distribution, MongodConfig config, MongodProcessOutputConfig outputConfig,
			MongodExecutable mongodExecutable) throws IOException {
		_config = config;
		_outputConfig = outputConfig;
		_mongodExecutable = mongodExecutable;
		_distribution = distribution;

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
			_process = ProcessControl.fromCommandLine(
					Mongod.enhanceCommandLinePlattformSpecific(distribution,
							Mongod.getCommandLine(_config, _mongodExecutable.getFile(), dbDir)), true);

			Runtime.getRuntime().addShutdownHook(new JobKiller());

			LogWatchStreamProcessor logWatch = new LogWatchStreamProcessor("waiting for connections on port", "failed",
					StreamToLineProcessor.wrap(outputConfig.getMongodOutput()));
			Processors.connect(_process.getReader(), logWatch);
			Processors.connect(_process.getError(), StreamToLineProcessor.wrap(outputConfig.getMongodError()));
			logWatch.waitForResult(20000);

			//			LogWatch logWatch = LogWatch.watch(_process.getReader(), "waiting for connections on port", "failed", 20000);
			if (logWatch.isInitWithSuccess()) {
				_mongodProcessId = Mongod.getMongodProcessId(logWatch.getOutput(), -1);
				//				ConsoleOutput consoleOutput = new ConsoleOutput(_process.getReader());
			} else {
				throw new IOException("Could not start mongod process");
			}

		} catch (IOException iox) {
			stop();
			throw iox;
		}
	}

	public synchronized void stop() {
		if (!_stopped) {

			_stopped = true;

			_logger.warning("try to stop mongod");
			if (!sendStopToMongoInstance()) {
				_logger.warning("could not stop mongod with db command, try next");
				if (!sendKillToMongodProcess()) {
					_logger.warning("could not stop mongod, try next");
					if (!tryKillToMongodProcess()) {
						_logger.warning("could not stop mongod the second time, try one last thing");
					}
				}
			}

			_process.stop();

			if ((_dbDir != null) && (!Files.forceDelete(_dbDir)))
				_logger.warning("Could not delete temp db dir: " + _dbDir);

			//			if (_mongodExecutable.getFile() != null) {
			//				if (!Files.forceDelete(_mongodExecutable.getFile())) {
			//					_stopped = true;
			//					_logger.warning("Could not delete mongod executable NOW: " + _mongodExecutable.getFile());
			//				}
			//			}
		}
	}

	private boolean sendStopToMongoInstance() {
		try {
			return Mongod.sendShutdown(Network.getLocalHost(), _config.getPort());
		} catch (UnknownHostException e) {
			_logger.log(Level.SEVERE, "sendStop", e);
		}
		return false;
	}

	private boolean sendKillToMongodProcess() {
		if (_mongodProcessId != -1) {
			return ProcessControl.killProcess(_distribution.getPlatform(), StreamToLineProcessor.wrap(_outputConfig.getCommandsOutput()),
					_mongodProcessId);
		}
		return false;
	}

	private boolean tryKillToMongodProcess() {
		if (_mongodProcessId != -1) {
			return ProcessControl.tryKillProcess(_distribution.getPlatform(), StreamToLineProcessor.wrap(_outputConfig.getCommandsOutput()),
					_mongodProcessId);
		}
		return false;
	}

	class JobKiller extends Thread {

		@Override
		public void run() {
			MongodProcess.this.stop();
		}
	}
}
