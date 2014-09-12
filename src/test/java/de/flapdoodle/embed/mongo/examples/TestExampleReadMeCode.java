/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
 *
 * with contributions from
 * 	konstantin-ba@github,Archimedes Trajano	(trajano@github)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.embed.mongo.examples;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ArtifactStoreBuilder;
import de.flapdoodle.embed.mongo.config.DownloadConfigBuilder;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongoCmdOptionsBuilder;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.config.Storage;
import de.flapdoodle.embed.mongo.config.Timeout;
import de.flapdoodle.embed.mongo.config.processlistener.ProcessListenerBuilder;
import de.flapdoodle.embed.mongo.distribution.Feature;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.distribution.Versions;
import de.flapdoodle.embed.mongo.tests.MongodForTestsFactory;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.config.store.HttpProxyFactory;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.GenericVersion;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.extract.ITempNaming;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.extract.UserTempNaming;
import de.flapdoodle.embed.process.io.IStreamProcessor;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.io.directories.FixedPath;
import de.flapdoodle.embed.process.io.directories.IDirectory;
import de.flapdoodle.embed.process.io.progress.LoggingProgressListener;
import de.flapdoodle.embed.process.runtime.ICommandLinePostProcessor;
import de.flapdoodle.embed.process.runtime.Network;
import junit.framework.TestCase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestExampleReadMeCode extends TestCase {

	// ### Usage
	public void testStandard() throws UnknownHostException, IOException {
		// ->
		MongodStarter starter = MongodStarter.getDefaultInstance();

		int port = 12345;
		IMongodConfig mongodConfig = new MongodConfigBuilder()
				.version(Version.Main.PRODUCTION)
				.net(new Net(port, Network.localhostIsIPv6()))
				.build();

		MongodExecutable mongodExecutable = null;
		try {
			mongodExecutable = starter.prepare(mongodConfig);
			MongodProcess mongod = mongodExecutable.start();

			MongoClient mongo = new MongoClient("localhost", port);
			DB db = mongo.getDB("test");
			DBCollection col = db.createCollection("testCol", new BasicDBObject());
			col.save(new BasicDBObject("testDoc", new Date()));

		} finally {
			if (mongodExecutable != null)
				mongodExecutable.stop();
		}
		// <-
	}

	// ### Usage - Optimization
	/*
	// ->
 		You should make the MongodStarter instance or the RuntimeConfig instance static (per Class or per JVM).
 		The main purpose of that is the caching of extracted executables and library files. This is done by the ArtifactStore instance
 		configured with the RuntimeConfig instance. Each instance uses its own cache so multiple RuntimeConfig instances will use multiple
 		ArtifactStores an multiple caches with much less cache hits:)  
	// <-
	 */
	
	// ### Usage - custom mongod filename 
	/*
	// ->
		To avoid windows firewall dialog popups you can chose a stable executable name with UserTempNaming. 
		This way the firewall dialog only popup once any your done. See [Executable Naming](#executable-naming) 
	// <-
	 */
	public void testCustomMongodFilename() throws UnknownHostException, IOException {
		// ->		
		int port = 12345;

		Command command = Command.MongoD;

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
		.defaults(command)
		.artifactStore(new ArtifactStoreBuilder()
				.defaults(command)
				.download(new DownloadConfigBuilder()
						.defaultsForCommand(command))
		// <-
		// disable caching with user temp naming if runtimeConfig is not the only
		// instance in this test
				.useCache(false)
		// ->
				.executableNaming(new UserTempNaming()))
		.build();

		IMongodConfig mongodConfig = new MongodConfigBuilder()
				.version(Version.Main.PRODUCTION)
				.net(new Net(port, Network.localhostIsIPv6()))
				.build();

		MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);

		MongodExecutable mongodExecutable = null;
		try {
			mongodExecutable = runtime.prepare(mongodConfig);
			MongodProcess mongod = mongodExecutable.start();

			MongoClient mongo = new MongoClient("localhost", port);
			DB db = mongo.getDB("test");
			DBCollection col = db.createCollection("testCol", new BasicDBObject());
			col.save(new BasicDBObject("testDoc", new Date()));

		} finally {
			if (mongodExecutable != null)
				mongodExecutable.stop();
		}
		// <-
	}

	// ### Unit Tests
	public void testUnitTests() {
		// @include AbstractMongoDBTest.java
		Class<?> see = AbstractMongoDBTest.class;
	}

	// #### ... with some more help
	public void testMongodForTests() throws IOException {
		// ->
		// ...
		MongodForTestsFactory factory = null;
		try {
			factory = MongodForTestsFactory.with(Version.Main.PRODUCTION);

			MongoClient mongo = factory.newMongo();
			DB db = mongo.getDB("test-" + UUID.randomUUID());
			DBCollection col = db.createCollection("testCol", new BasicDBObject());
			col.save(new BasicDBObject("testDoc", new Date()));

		} finally {
			if (factory != null)
				factory.shutdown();
		}
		// ...
		// <-
	}

	// ### Customize Download URL
	public void testCustomizeDownloadURL() {
		// ->
		// ...
		Command command = Command.MongoD;

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaults(command)
				.artifactStore(new ArtifactStoreBuilder()
						.defaults(command)
						.download(new DownloadConfigBuilder()
								.defaultsForCommand(command)
								.downloadPath("http://my.custom.download.domain/")))
				.build();
		// ...
		// <-
	}

	// ### Customize Proxy for Download 
	public void testCustomProxy() {
		// ->
		// ...
		Command command = Command.MongoD;

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaults(command)
				.artifactStore(new ArtifactStoreBuilder()
						.defaults(command)
						.download(new DownloadConfigBuilder()
								.defaultsForCommand(command)
								.proxyFactory(new HttpProxyFactory("fooo", 1234))))
				.build();
		// ...
		// <-
	}
	
	// ### Customize Artifact Storage
	public void testCustomizeArtifactStorage() throws IOException {

		IMongodConfig mongodConfig = new MongodConfigBuilder()
				.version(Version.Main.PRODUCTION)
				.net(new Net(12345, Network.localhostIsIPv6()))
				.build();

		// ->
		// ...
		IDirectory artifactStorePath = new FixedPath(System.getProperty("user.home") + "/.embeddedMongodbCustomPath");
		ITempNaming executableNaming = new UUIDTempNaming();

		Command command = Command.MongoD;

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaults(command)
				.artifactStore(new ArtifactStoreBuilder()
						.defaults(command)
						.download(new DownloadConfigBuilder()
								.defaultsForCommand(command)
								.artifactStorePath(artifactStorePath))
						.executableNaming(executableNaming))
				.build();

		MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);
		MongodExecutable mongodExe = runtime.prepare(mongodConfig);
		// ...
		// <-
		MongodProcess mongod = mongodExe.start();

		mongod.stop();
		mongodExe.stop();
	}

	// ### Usage - custom mongod process output
	// #### ... to console with line prefix
	public void testCustomOutputToConsolePrefix() {
		// ->
		// ...
		ProcessOutput processOutput = new ProcessOutput(Processors.namedConsole("[mongod>]"),
				Processors.namedConsole("[MONGOD>]"), Processors.namedConsole("[console>]"));

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaults(Command.MongoD)
				.processOutput(processOutput)
				.build();

		MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);
		// ...
		// <-
	}

	// #### ... to file
	public void testCustomOutputToFile() throws FileNotFoundException, IOException {
		// ->
		// ...
		IStreamProcessor mongodOutput = Processors.named("[mongod>]",
				new FileStreamProcessor(File.createTempFile("mongod", "log")));
		IStreamProcessor mongodError = new FileStreamProcessor(File.createTempFile("mongod-error", "log"));
		IStreamProcessor commandsOutput = Processors.namedConsole("[console>]");

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaults(Command.MongoD)
				.processOutput(new ProcessOutput(mongodOutput, mongodError, commandsOutput))
				.build();

		MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);
		// ...
		// <-
	}

	/*
	 * Ist fürs Readme, deshalb nicht statisch und public
	 */
	// ->

	// ...
	public class FileStreamProcessor implements IStreamProcessor {

		private FileOutputStream outputStream;

		public FileStreamProcessor(File file) throws FileNotFoundException {
			outputStream = new FileOutputStream(file);
		}

		@Override
		public void process(String block) {
			try {
				outputStream.write(block.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onProcessed() {
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	// ...
	// <-

	// #### ... to java logging
	public void testCustomOutputToLogging() throws FileNotFoundException, IOException {
		// ->
		// ...
		Logger logger = Logger.getLogger(getClass().getName());

		ProcessOutput processOutput = new ProcessOutput(Processors.logTo(logger, Level.INFO), Processors.logTo(logger,
				Level.SEVERE), Processors.named("[console>]", Processors.logTo(logger, Level.FINE)));

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaultsWithLogger(Command.MongoD, logger)
				.processOutput(processOutput)
				.artifactStore(new ArtifactStoreBuilder()
						.defaults(Command.MongoD)
						.download(new DownloadConfigBuilder()
								.defaultsForCommand(Command.MongoD)
								.progressListener(new LoggingProgressListener(logger, Level.FINE))))
				.build();

		MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);
		// ...
		// <-
	}

	// #### ... to default java logging (the easy way)
	public void testDefaultOutputToLogging() throws FileNotFoundException, IOException {
		// ->
		// ...
		Logger logger = Logger.getLogger(getClass().getName());

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaultsWithLogger(Command.MongoD, logger)
				.build();

		MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);
		// ...
		// <-
	}

	// #### ... to null device
	public void testDefaultOutputToNone() throws FileNotFoundException, IOException {
		int port = 12345;
		IMongodConfig mongodConfig = new MongodConfigBuilder()
				.version(Versions.withFeatures(new GenericVersion("2.0.7-rc1"), Feature.SYNC_DELAY))
				.net(new Net(port, Network.localhostIsIPv6()))
				.build();
		// ->
		// ...
		Logger logger = Logger.getLogger(getClass().getName());

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaultsWithLogger(Command.MongoD, logger)
				.processOutput(ProcessOutput.getDefaultInstanceSilent())
				.build();

		MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);
		// ...
		// <-
		MongodProcess mongod = null;

		MongodExecutable mongodExecutable = null;
		try {
			mongodExecutable = runtime.prepare(mongodConfig);
			mongod = mongodExecutable.start();

			MongoClient mongo = new MongoClient("localhost", port);
			DB db = mongo.getDB("test");
			DBCollection col = db.createCollection("testCol", new BasicDBObject());
			col.save(new BasicDBObject("testDoc", new Date()));

		} finally {
			if (mongod != null) {
				mongod.stop();
			}
			if (mongodExecutable != null)
				mongodExecutable.stop();
		}
	}

	// ### Custom Version
	public void testCustomVersion() throws UnknownHostException, IOException {
		// ->
		// ...
		int port = 12345;
		IMongodConfig mongodConfig = new MongodConfigBuilder()
				.version(Versions.withFeatures(new GenericVersion("2.0.7-rc1"), Feature.SYNC_DELAY))
				.net(new Net(port, Network.localhostIsIPv6()))
				.build();

		MongodStarter runtime = MongodStarter.getDefaultInstance();
		MongodProcess mongod = null;

		MongodExecutable mongodExecutable = null;
		try {
			mongodExecutable = runtime.prepare(mongodConfig);
			mongod = mongodExecutable.start();

			// <-
			MongoClient mongo = new MongoClient("localhost", port);
			DB db = mongo.getDB("test");
			DBCollection col = db.createCollection("testCol", new BasicDBObject());
			col.save(new BasicDBObject("testDoc", new Date()));
			// ->
			// ...

		} finally {
			if (mongod != null) {
				mongod.stop();
			}
			if (mongodExecutable != null)
				mongodExecutable.stop();
		}
		// ...
		// <-

	}

	// ### Main Versions
	public void testMainVersions() throws UnknownHostException, IOException {
		// ->
		IVersion version = Version.V2_2_5;
		// uses latest supported 2.2.x Version
		version = Version.Main.V2_2;
		// uses latest supported production version
		version = Version.Main.PRODUCTION;
		// uses latest supported development version
		version = Version.Main.DEVELOPMENT;
		// <-
	}

	// ### Use Free Server Port
	/*
	// ->
		Warning: maybe not as stable, as expected.
	// <-
	 */
	// #### ... by hand
	public void testFreeServerPort() throws UnknownHostException, IOException {
		// ->
		// ...
		int port = Network.getFreeServerPort();
		// ...
		// <-
	}

	// #### ... automagic
	public void testFreeServerPortAuto() throws UnknownHostException, IOException {
		// ->
		// ...
		IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION).build();

		MongodStarter runtime = MongodStarter.getDefaultInstance();

		MongodExecutable mongodExecutable = null;
		MongodProcess mongod = null;
		try {
			mongodExecutable = runtime.prepare(mongodConfig);
			mongod = mongodExecutable.start();

			MongoClient mongo = new MongoClient(
					new ServerAddress(mongodConfig.net().getServerAddress(), mongodConfig.net().getPort()));
			// <-
			DB db = mongo.getDB("test");
			DBCollection col = db.createCollection("testCol", new BasicDBObject());
			col.save(new BasicDBObject("testDoc", new Date()));
			// ->
			// ...

		} finally {
			if (mongod != null) {
				mongod.stop();
			}
			if (mongodExecutable != null)
				mongodExecutable.stop();
		}
		// ...
		// <-
	}

	// ### ... custom timeouts
	public void testCustomTimeouts() throws UnknownHostException, IOException {
		// ->
		// ...
		IMongodConfig mongodConfig = new MongodConfigBuilder()
				.version(Version.Main.PRODUCTION)
				.timeout(new Timeout(30000))
				.build();
		// ...
		// <-
	}

	// ### Command Line Post Processing
	public void testCommandLinePostProcessing() {

		// ->
		// ...
		ICommandLinePostProcessor postProcessor = // ...
				// <-
				new ICommandLinePostProcessor() {
					@Override
					public List<String> process(Distribution distribution, List<String> args) {
						return null;
					}
				};
		// ->

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaults(Command.MongoD)
				.commandLinePostProcessor(postProcessor)
				.build();
		// ...
		// <-
	}

	// ### Custom Command Line Options
	/*
	// ->
		We changed the syncDelay to 0 which turns off sync to disc. To turn on default value used defaultSyncDelay().
	// <-
	 */
	public void testCommandLineOptions() throws UnknownHostException, IOException {
		// ->
		IMongodConfig mongodConfig = new MongodConfigBuilder()
				.version(Version.Main.PRODUCTION)
				.cmdOptions(new MongoCmdOptionsBuilder()
						.syncDelay(10)
						.useNoPrealloc(false)
						.useSmallFiles(false)
						.useNoJournal(false)
						.enableTextSearch(true)
						.build())
				.build();
		// ...
		// <-

	}

	// ### Snapshot database files from temp dir
	/*
	// ->
		We changed the syncDelay to 0 which turns off sync to disc. To get the files to create an snapshot you must turn on default value (use defaultSyncDelay()).
	// <-
	 */
	public void testSnapshotDbFiles() throws UnknownHostException, IOException {
		File destination = null;
		// ->
		IMongodConfig mongodConfig = new MongodConfigBuilder()
				.version(Version.Main.PRODUCTION)
				.processListener(new ProcessListenerBuilder()
						.copyDbFilesBeforeStopInto(destination)
						.build())
				.cmdOptions(new MongoCmdOptionsBuilder()
						.defaultSyncDelay()
						.build())
				.build();
		// ...
		// <-
	}
	// ### Custom database directory  
	/*
	// ->
		If you set a custom database directory, it will not be deleted after shutdown
	// <-
	 */
	public void testCustomDatabaseDirectory() throws UnknownHostException, IOException {
		// ->
		Storage replication = new Storage("/custom/databaseDir",null,0);
		
		IMongodConfig mongodConfig = new MongodConfigBuilder()
				.version(Version.Main.PRODUCTION)
				.replication(replication)
				.build();
		// ...
		// <-
	}
	// ### Start mongos with mongod instance
	// @include StartConfigAndMongoDBServerTest.java
	
	// ## Common Errors
	
	// ### Executable Collision

	/*
	// ->
	There is a good chance of filename collisions if you use a custom naming schema for the executable (see [Usage - custom mongod filename](#usage---custom-mongod-filename)).
	If you got an exception, then you should make your RuntimeConfig or MongoStarter class or jvm static (static final in your test class or singleton class for all tests).
	// <-
	*/
	
}
