package de.flapdoodle.embed.mongo.examples;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Test;

import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.MongosExecutable;
import de.flapdoodle.embed.mongo.MongosProcess;
import de.flapdoodle.embed.mongo.MongosStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.IMongosConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.MongosConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;


public class StartConfigAndMongoDBServerTest {

	
	@Test
	public void shouldFixFollowingTest() throws UnknownHostException, IOException {
		if (false) {
			tryToStartAndToStopWithoutAnyException();
		}
	}
	
	public void tryToStartAndToStopWithoutAnyException() throws UnknownHostException, IOException {
		int port=12121;
		int defaultConfigPort=12345;
		String defaultHost="localhost";
		
		MongodProcess mongod = startMongod(defaultConfigPort);
		
		try {
			MongosProcess mongos = startMongos(port, defaultConfigPort, defaultHost);
			try {
				MongoClient mongoClient = new MongoClient(defaultHost, defaultConfigPort);
				System.out.println("DB Names: "+mongoClient.getDatabaseNames());
			} finally {
				mongos.stop();
			}
		} finally {
			mongod.stop();
		}
	}

	private MongosProcess startMongos(int port, int defaultConfigPort, String defaultHost) throws UnknownHostException,
			IOException {
		IMongosConfig mongosConfig = new MongosConfigBuilder()
			.version(Version.Main.PRODUCTION)
			.net(new Net(port, Network.localhostIsIPv6()))
			.configDB(defaultHost + ":" + defaultConfigPort)
			.build();

		MongosExecutable mongosExecutable = MongosStarter.getDefaultInstance().prepare(mongosConfig);
		MongosProcess mongos = mongosExecutable.start();
		return mongos;
	}

	private MongodProcess startMongod(int defaultConfigPort) throws UnknownHostException, IOException {
		IMongodConfig mongoConfigConfig = new MongodConfigBuilder()
			.version(Version.Main.PRODUCTION)
			.net(new Net(defaultConfigPort, Network.localhostIsIPv6()))
			.configServer(true)
			.build();

		MongodExecutable mongodExecutable = MongodStarter.getDefaultInstance().prepare(mongoConfigConfig);
		MongodProcess mongod = mongodExecutable.start();
		return mongod;
	}
}
