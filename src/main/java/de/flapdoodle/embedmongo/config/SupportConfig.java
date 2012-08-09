package de.flapdoodle.embedmongo.config;

import de.flapdoodle.process.config.ISupportConfig;


public class SupportConfig implements ISupportConfig {

	static SupportConfig _instance=new SupportConfig();
	
	@Override
	public String getName() {
		return "mongod";
	}

	@Override
	public String getSupportUrl() {
		return "https://github.com/flapdoodle-oss/embedmongo.flapdoodle.de/issues\n";
	}
	
	public static ISupportConfig getInstance() {
		return _instance;
	}

}
