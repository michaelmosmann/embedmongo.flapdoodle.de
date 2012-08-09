package de.flapdoodle.process.config;

import de.flapdoodle.process.distribution.IVersion;

public class ExecutableProcessConfig {

	protected final IVersion version;

	public ExecutableProcessConfig(IVersion version) {
		this.version = version;
	}

	public IVersion getVersion() {
		return version;
	}

}
