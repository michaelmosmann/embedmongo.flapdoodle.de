package de.flapdoodle.process.runtime;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import de.flapdoodle.embedmongo.MongodProcess;
import de.flapdoodle.embedmongo.config.MongodConfig;
import de.flapdoodle.process.config.ExecutableProcessConfig;
import de.flapdoodle.process.config.IRuntimeConfig;
import de.flapdoodle.process.distribution.Distribution;
import de.flapdoodle.process.io.file.Files;

public abstract class Executable<T extends ExecutableProcessConfig,P> {

	private static Logger logger = Logger.getLogger(Executable.class.getName());

	private final T mongodConfig;
	private final IRuntimeConfig runtimeConfig;
	private final File mongodExecutable;
	private boolean stopped;

	private final Distribution distribution;

	public Executable(Distribution distribution, T mongodConfig,
			IRuntimeConfig runtimeConfig, File mongodExecutable) {
		this.distribution = distribution;
		this.mongodConfig = mongodConfig;
		this.runtimeConfig = runtimeConfig;
		this.mongodExecutable = mongodExecutable;
		Runtime.getRuntime().addShutdownHook(new JobKiller());
	}

	public synchronized void cleanup() {
		if (!stopped) {
			if (mongodExecutable.exists() && !Files.forceDelete(mongodExecutable))
				logger.warning("Could not delete mongod executable NOW: " + mongodExecutable);
			stopped = true;
		}
	}

	/**
	 *
	 */
	class JobKiller extends Thread {

		@Override
		public void run() {
			cleanup();
		}
	}

	public File getFile() {
		return mongodExecutable;
	}

	public P start() throws IOException {
		return start(distribution, mongodConfig, runtimeConfig);
	}

	protected abstract P start(Distribution distribution, T config, IRuntimeConfig runtime) throws IOException;

}
