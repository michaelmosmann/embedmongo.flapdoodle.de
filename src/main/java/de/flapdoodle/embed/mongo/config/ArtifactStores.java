package de.flapdoodle.embed.mongo.config;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.Paths;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.io.directories.PropertyOrPlatformTempDir;
import de.flapdoodle.embed.process.store.ArtifactStoreBuilder;
import de.flapdoodle.embed.process.store.IArtifactStore;

public class ArtifactStores {

	private ArtifactStores() {
		// no instance
	}

	public static IArtifactStore defaultArtifactStore() {
		return artifactStore(Command.MongoD);
	}

	public static IArtifactStore mongoSArtifactStore() {
		return artifactStore(Command.MongoS);
	}

	private static IArtifactStore artifactStore(Command command) {
		return builder(command).build();
	}

	public static ArtifactStoreBuilder builder(Command command) {
		return defaultBuilder().download(new DownloadConfigBuilder().defaults().packageResolver(new Paths(command)).build());
	}

	public static ArtifactStoreBuilder defaultBuilder() {
		return new ArtifactStoreBuilder().tempDir(new PropertyOrPlatformTempDir()).executableNaming(new UUIDTempNaming());
	}

}
