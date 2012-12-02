package de.flapdoodle.embed.mongo;


public enum Command {
	MongoD("mongod"),MongoS("mongos");
	
	private final String commandName;

	Command(String commandName) {
		this.commandName = commandName;
	}
	
	public String commandName() {
		return commandName;
	}
}
