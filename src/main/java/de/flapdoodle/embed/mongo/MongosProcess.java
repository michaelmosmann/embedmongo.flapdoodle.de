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
package de.flapdoodle.embed.mongo;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.flapdoodle.embed.mongo.config.MongosConfig;
import de.flapdoodle.embed.mongo.config.MongosSupportConfig;
import de.flapdoodle.embed.mongo.runtime.Mongod;
import de.flapdoodle.embed.mongo.runtime.Mongos;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.ISupportConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.io.LogWatchStreamProcessor;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.io.StreamToLineProcessor;
import de.flapdoodle.embed.process.io.file.Files;
import de.flapdoodle.embed.process.runtime.AbstractProcess;
import de.flapdoodle.embed.process.runtime.ProcessControl;

/**
 *
 */
public class MongosProcess extends AbstractProcess<MongosConfig, MongosExecutable, MongosProcess> {

	private static Logger logger = Logger.getLogger(MongosProcess.class.getName());
	public static final int TIMEOUT = 20000;

	//	private final IRuntimeConfig runtimeConfig;
	//	private final MongodExecutable mongodExecutable;
	//	private ProcessControl process;
	//	private int mongodProcessId;

	private File dbDir;
	boolean dbDirIsTemp;
	
	private boolean stopped = false;

	//
	//	private Distribution distribution;

	public MongosProcess(Distribution distribution, MongosConfig config, IRuntimeConfig runtimeConfig,
			MongosExecutable mongosExecutable) throws IOException {
		super(distribution, config, runtimeConfig, mongosExecutable);
	}

	@Override
	protected void onBeforeProcess(IRuntimeConfig runtimeConfig) throws IOException {
		super.onBeforeProcess(runtimeConfig);

		MongosConfig config=getConfig();
		
		File tmpDbDir;
//		if (config.getDatabaseDir() != null) {
//			tmpDbDir = Files.createOrCheckDir(config.getDatabaseDir());
//		} else {
			tmpDbDir = Files.createTempDir("embedmongo-db");
			dbDirIsTemp=true;
//		}
		this.dbDir = tmpDbDir;
	}

	@Override
	protected void onAfterProcessStart(ProcessControl process, IRuntimeConfig runtimeConfig) throws IOException {
		ProcessOutput outputConfig = runtimeConfig.getProcessOutput();
		LogWatchStreamProcessor logWatch = new LogWatchStreamProcessor("waiting for connections on port", "failed",
				StreamToLineProcessor.wrap(outputConfig.getOutput()));
		Processors.connect(process.getReader(), logWatch);
		Processors.connect(process.getError(), StreamToLineProcessor.wrap(outputConfig.getError()));
		logWatch.waitForResult(TIMEOUT);
		if (logWatch.isInitWithSuccess()) {
			setProcessId(Mongos.getMongosProcessId(logWatch.getOutput(), -1));
		} else {
			throw new IOException("Could not start mongod process");
		}
	}

	@Override
	protected ISupportConfig supportConfig() {
		return MongosSupportConfig.getInstance();
	}
	
	@Override
	protected List<String> getCommandLine(Distribution distribution, MongosConfig config, File exe) throws IOException {
		return Mongod.enhanceCommandLinePlattformSpecific(distribution, Mongos.getCommandLine(getConfig(), exe, dbDir));
	}

	@Override
	public void stop() {

		synchronized (this) {
			if (!stopped) {

				stopped = true;

				logger.info("try to stop mongos");
				if (!sendStopToMongoInstance()) {
					logger.warning("could not stop mongos with db command, try next");
					if (!sendKillToProcess()) {
						logger.warning("could not stop mongos, try next");
						if (!tryKillToProcess()) {
							logger.warning("could not stop mongos the second time, try one last thing");
						}
					}
				}

				stopProcess();

				if ((dbDir != null) && (dbDirIsTemp) && (!Files.forceDelete(dbDir)))
					logger.warning("Could not delete temp db dir: " + dbDir);

			}
		}
	}

	private boolean sendStopToMongoInstance() {
		try {
			return Mongos.sendShutdown(getConfig().net().getServerAddress(), getConfig().net().getPort());
		} catch (UnknownHostException e) {
			logger.log(Level.SEVERE, "sendStop", e);
		}
		return false;
	}
	//	public MongodConfig getConfig() {
	//		return config;
	//	}

	//	/**
	//	 *
	//	 */
	//	class JobKiller extends Thread {
	//
	//		@Override
	//		public void run() {
	//			MongodProcess.this.stop();
	//		}
	//	}
}
