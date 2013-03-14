package de.flapdoodle.embed.mongo.config;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.process.config.store.IDownloadConfig;
import de.flapdoodle.embed.process.io.progress.LoggingProgressListener;
import de.flapdoodle.embed.process.runtime.ICommandLinePostProcessor;


public class RuntimeConfigBuilder extends de.flapdoodle.embed.process.config.RuntimeConfigBuilder {

	public RuntimeConfigBuilder defaultsWithLogger(Command command, Logger logger) {
		defaults(command);
		processOutput(MongodProcessOutputConfig.getInstance(logger));

		IDownloadConfig downloadConfig = new DownloadConfigBuilder()
				.defaultsForCommand(command)
				.progressListener(new LoggingProgressListener(logger, Level.FINE))
				.build();

		artifactStore(new ArtifactStoreBuilder().defaults(command).download(downloadConfig).build());
		return this;
	}
	
	public RuntimeConfigBuilder defaults(Command command) {
		processOutput(MongodProcessOutputConfig.getDefaultInstance());
		commandLinePostProcessor(new ICommandLinePostProcessor.Noop());
		artifactStore(new ArtifactStoreBuilder().defaults(command));
		setOverride(true);
		return this;
	}
}
