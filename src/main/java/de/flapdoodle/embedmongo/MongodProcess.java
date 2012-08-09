/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
 *
 * with contributions from
 * 	konstantin-ba@github,Archimedes Trajano (trajano@github)
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

import de.flapdoodle.embedmongo.config.MongodConfig;
import de.flapdoodle.embedmongo.config.RuntimeConfig;
import de.flapdoodle.embedmongo.runtime.Mongod;
import de.flapdoodle.process.config.IRuntimeConfig;
import de.flapdoodle.process.config.io.ProcessOutput;
import de.flapdoodle.process.distribution.Distribution;
import de.flapdoodle.process.io.LogWatchStreamProcessor;
import de.flapdoodle.process.io.Processors;
import de.flapdoodle.process.io.StreamToLineProcessor;
import de.flapdoodle.process.io.file.Files;
import de.flapdoodle.process.runtime.Network;
import de.flapdoodle.process.runtime.ProcessControl;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class MongodProcess {

	private static Logger logger = Logger.getLogger(MongodProcess.class.getName());
	public static final int TIMEOUT = 20000;

	private final MongodConfig config;
	private final IRuntimeConfig runtimeConfig;
	private final MongodExecutable mongodExecutable;
	private ProcessControl process;
	private int mongodProcessId;

	private File dbDir;

	private boolean stopped = false;

	private Distribution distribution;

	public MongodProcess(Distribution distribution, MongodConfig config, IRuntimeConfig runtimeConfig,
			MongodExecutable mongodExecutable) throws IOException {
		this.config = config;
		this.runtimeConfig = runtimeConfig;
		this.mongodExecutable = mongodExecutable;
		this.distribution = distribution;

		ProcessOutput outputConfig = runtimeConfig.getMongodOutputConfig();

		try {
			File tmpDbDir;
			if (config.getDatabaseDir() != null) {
				tmpDbDir = Files.createOrCheckDir(config.getDatabaseDir());
			} else {
				tmpDbDir = Files.createTempDir("embedmongo-db");
				this.dbDir = tmpDbDir;
			}
			process = ProcessControl.fromCommandLine(
					runtimeConfig.getCommandLinePostProcessor().process(
							distribution,
							Mongod.enhanceCommandLinePlattformSpecific(distribution,
									Mongod.getCommandLine(this.config, this.mongodExecutable.getFile(), tmpDbDir))), true);

			Runtime.getRuntime().addShutdownHook(new JobKiller());

			LogWatchStreamProcessor logWatch = new LogWatchStreamProcessor("waiting for connections on port", "failed",
					StreamToLineProcessor.wrap(outputConfig.getMongodOutput()));
			Processors.connect(process.getReader(), logWatch);
			Processors.connect(process.getError(), StreamToLineProcessor.wrap(outputConfig.getMongodError()));
			logWatch.waitForResult(TIMEOUT);
			if (logWatch.isInitWithSuccess()) {
				mongodProcessId = Mongod.getMongodProcessId(logWatch.getOutput(), -1);
			} else {
				throw new IOException("Could not start mongod process");
			}

		} catch (IOException iox) {
			stop();
			throw iox;
		}
	}

	public synchronized void stop() {
		if (!stopped) {

			stopped = true;

			logger.info("try to stop mongod");
			if (!sendStopToMongoInstance()) {
				logger.warning("could not stop mongod with db command, try next");
				if (!sendKillToMongodProcess()) {
					logger.warning("could not stop mongod, try next");
					if (!tryKillToMongodProcess()) {
						logger.warning("could not stop mongod the second time, try one last thing");
					}
				}
			}

			process.stop();

			if ((dbDir != null) && (!Files.forceDelete(dbDir)))
				logger.warning("Could not delete temp db dir: " + dbDir);

		}
	}

	private boolean sendStopToMongoInstance() {
		try {
			return Mongod.sendShutdown(Network.getLocalHost(), config.getPort());
		} catch (UnknownHostException e) {
			logger.log(Level.SEVERE, "sendStop", e);
		}
		return false;
	}

	private boolean sendKillToMongodProcess() {
		if (mongodProcessId > 0) {
			return ProcessControl.killProcess(distribution.getPlatform(),
					StreamToLineProcessor.wrap(runtimeConfig.getMongodOutputConfig().getCommandsOutput()), mongodProcessId);
		}
		return false;
	}

	private boolean tryKillToMongodProcess() {
		if (mongodProcessId > 0) {
			return ProcessControl.tryKillProcess(distribution.getPlatform(),
					StreamToLineProcessor.wrap(runtimeConfig.getMongodOutputConfig().getCommandsOutput()), mongodProcessId);
		}
		return false;
	}

	public MongodConfig getConfig() {
		return config;
	}

	/**
	 *
	 */
	class JobKiller extends Thread {

		@Override
		public void run() {
			MongodProcess.this.stop();
		}
	}
}
