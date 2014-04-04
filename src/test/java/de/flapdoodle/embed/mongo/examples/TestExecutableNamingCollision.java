package de.flapdoodle.embed.mongo.examples;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Test;

import junit.framework.TestCase;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ArtifactStoreBuilder;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.extract.UserTempNaming;
import de.flapdoodle.embed.process.runtime.Network;

public class TestExecutableNamingCollision extends TestCase {

	private MongodExecutable _mongodExe;
	private MongodProcess _mongod;

	private MongoClient _mongo;
	
	private static final IRuntimeConfig _runtimeConfig;

	static
	{
		System.out.println("------------------------CONSTR-----------------------------");
		_runtimeConfig = new RuntimeConfigBuilder()
		.defaults(Command.MongoD)
		.artifactStore(new ArtifactStoreBuilder()
				.defaults(Command.MongoD)
				.executableNaming(new UserTempNaming())
				.build())
		.build();
	}
	
	
	@Override
	protected void setUp() throws Exception {
		System.out.println("------------------------SETUP-----------------------------");
		
		
		MongodStarter runtime = MongodStarter.getInstance(_runtimeConfig);
		_mongodExe = runtime.prepare(createMongodConfig());
		_mongod = _mongodExe.start();

		super.setUp();

		_mongo = new MongoClient("localhost", 12345);
		System.out.println("------------------------SETUP DONE-----------------------------");
	}

	protected IMongodConfig createMongodConfig() throws UnknownHostException, IOException {
		return createMongodConfigBuilder().build();
	}

	protected MongodConfigBuilder createMongodConfigBuilder() throws UnknownHostException, IOException {
		return new MongodConfigBuilder().version(Version.Main.PRODUCTION).net(new Net(12345, Network.localhostIsIPv6()));
	}

	@Override
	protected void tearDown() throws Exception {
		System.out.println("------------------------TEARDOWN-----------------------------");
		super.tearDown();

		_mongod.stop();
		_mongodExe.stop();
		System.out.println("------------------------DONE-----------------------------");
	}

	public Mongo getMongo() {
		return _mongo;
	}

	@Test
	public void testOne() {
		System.out.println("------------------------One-----------------------------");
	}

	@Test
	public void testTwo() {
		System.out.println("------------------------Two-----------------------------");
	}

	@Test
	public void testAnOther() {
		System.out.println("------------------------3-----------------------------");
	}
}
