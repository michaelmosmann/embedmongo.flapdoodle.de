package de.flapdoodle.embed.mongo;

import java.io.IOException;

import de.flapdoodle.embed.process.config.ExecutableProcessConfig;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.runtime.AbstractProcess;
import de.flapdoodle.embed.process.runtime.Executable;
import de.flapdoodle.embed.process.runtime.IStopable;


public abstract class AbstractMongoProcess<T extends ExecutableProcessConfig, E extends Executable<T, P>, P extends IStopable> extends AbstractProcess<T, E, P> {

	public AbstractMongoProcess(Distribution distribution, T config, IRuntimeConfig runtimeConfig, E executable)
			throws IOException {
		super(distribution, config, runtimeConfig, executable);
	}


}
