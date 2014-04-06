package de.flapdoodle.embed.mongo.config;

import de.flapdoodle.embed.process.config.ISupportConfig;
import de.flapdoodle.embed.process.extract.ExecutableFileAlreadyExistsException;


public abstract class AbstractSupportConfig implements ISupportConfig {

	@Override
	public String messageOnException(Class<?> context, Exception exception) {
		if (exception instanceof ExecutableFileAlreadyExistsException) {
			return "\n\n" +
					"-----------------------------------------------------\n" +
					"There was a file name collision extracting the executable.\n" +
					"See "+SupportConfig.baseUrl()+"#executable-collision\n" +
					"-----------------------------------------------------\n\n";
		}
		return null;
	}


}
