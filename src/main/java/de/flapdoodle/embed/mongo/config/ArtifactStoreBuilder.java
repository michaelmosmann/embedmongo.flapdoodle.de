package de.flapdoodle.embed.mongo.config;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.io.directories.PropertyOrPlatformTempDir;


public class ArtifactStoreBuilder extends de.flapdoodle.embed.process.store.ArtifactStoreBuilder {

	public ArtifactStoreBuilder defaults(Command command) {
		tempDir(new PropertyOrPlatformTempDir());
		executableNaming(new UUIDTempNaming());
		download(new DownloadConfigBuilder().defaultsForCommand(command));
		setOverride(true);
		return this;
	}
}
