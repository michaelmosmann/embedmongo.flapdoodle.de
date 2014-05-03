/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
 *
 * with contributions from
 * 	konstantin-ba@github,Archimedes Trajano	(trajano@github)
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

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.flapdoodle.embed.mongo.config.IMongoConfig;
import de.flapdoodle.embed.mongo.runtime.Mongod;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.io.LogWatchStreamProcessor;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.io.StreamToLineProcessor;
import de.flapdoodle.embed.process.runtime.AbstractProcess;
import de.flapdoodle.embed.process.runtime.Executable;
import de.flapdoodle.embed.process.runtime.IStopable;
import de.flapdoodle.embed.process.runtime.ProcessControl;


public abstract class AbstractMongoProcess<T extends IMongoConfig, E extends Executable<T, P>, P extends IStopable> extends AbstractProcess<T, E, P> {

	private static Logger logger = Logger.getLogger(AbstractMongoProcess.class.getName());
	
	boolean stopped=false;
	
	public AbstractMongoProcess(Distribution distribution, T config, IRuntimeConfig runtimeConfig, E executable)
			throws IOException {
		super(distribution, config, runtimeConfig, executable);
	}
	
	@Override
	protected final void onAfterProcessStart(ProcessControl process, IRuntimeConfig runtimeConfig) throws IOException {
		ProcessOutput outputConfig = runtimeConfig.getProcessOutput();
		LogWatchStreamProcessor logWatch = new LogWatchStreamProcessor(successMessage(), knownFailureMessages(),
				StreamToLineProcessor.wrap(outputConfig.getOutput()));
		Processors.connect(process.getReader(), logWatch);
		Processors.connect(process.getError(), StreamToLineProcessor.wrap(outputConfig.getError()));
		logWatch.waitForResult(getConfig().timeout().getStartupTimeout());
		if (logWatch.isInitWithSuccess()) {
			setProcessId(Mongod.getMongodProcessId(logWatch.getOutput(), -1));
		} else {
			String failureFound = logWatch.getFailureFound();
			if (failureFound==null) {
				failureFound="\n" +
						"----------------------\n" +
						"Hmm.. no failure message.. \n" +
						"...the cause must be somewhere in the process output\n" +
						"----------------------\n" +
						""+logWatch.getOutput();
			}
			throw new IOException("Could not start process: "+failureFound);
		}
	}

	protected String successMessage() {
		return "waiting for connections on port";
	}
	
	private Set<String> knownFailureMessages() {
		HashSet<String> ret = new HashSet<String>();
		ret.add("failed errno");
		ret.add("ERROR:");
		ret.add("error command line");
		return ret;
	}

	@Override
	public void stopInternal() {
		synchronized (this) {
			if (!stopped) {

				stopped = true;

				logger.info("try to stop mongod");
				if (!sendStopToMongoInstance()) {
					logger.warning("could not stop mongod with db command, try next");
					if (!sendKillToProcess()) {
						logger.warning("could not stop mongod, try next");
						if (!tryKillToProcess()) {
							logger.warning("could not stop mongod the second time, try one last thing");
						}
					}
				}

				stopProcess();
			}
		}
	}
	
	@Override
	protected void cleanupInternal() {
		deleteTempFiles();
	}

	protected void deleteTempFiles() {

	}

	protected final boolean sendStopToMongoInstance() {
		try {
			return Mongod.sendShutdown(getConfig().net().getServerAddress(), getConfig().net().getPort());
		} catch (UnknownHostException e) {
			logger.log(Level.SEVERE, "sendStop", e);
		}
		return false;
	}

}
