# Organisation Flapdoodle OSS
[![Build Status](https://travis-ci.org/flapdoodle-oss/de.flapdoodle.embed.mongo.svg?branch=master)](https://travis-ci.org/flapdoodle-oss/de.flapdoodle.embed.mongo)

We are now a github organisation. You are invited to participate. :)

# Embedded MongoDB

Embedded MongoDB will provide a platform neutral way for running mongodb in unittests.

## Why?

- dropping databases causing some pains (often you have to wait long time after each test)
- its easy, much easier as installing right version by hand
- you can change version per test

## Dependencies

### Build on top of

- Embed Process Util [de.flapdoodle.embed.process](https://github.com/flapdoodle-oss/de.flapdoodle.embed.process)

### Other ways to use Embedded MongoDB

- in a Maven build using [embedmongo-maven-plugin](https://github.com/joelittlejohn/embedmongo-maven-plugin)
- in a Clojure/Leiningen project using [lein-embongo](https://github.com/joelittlejohn/lein-embongo)
- in a Gradle build using [gradle-mongo-plugin](https://github.com/sourcemuse/GradleMongoPlugin)
- in a Scala/specs2 specification using [specs2-embedmongo](https://github.com/athieriot/specs2-embedmongo)
- in Scala tests using [scalatest-embedmongo](https://github.com/SimplyScala/scalatest-embedmongo)

### Comments about Embedded MongoDB in the Wild

- http://stackoverflow.com/questions/6437226/embedded-mongodb-when-running-integration-tests
- http://www.cubeia.com/index.php/blog/archives/436
- http://blog.diabol.se/?p=390

### Other MongoDB Stuff

- https://github.com/thiloplanz/jmockmongo - mongodb mocking
- https://github.com/lordofthejars/nosql-unit - extended nosql unit testing
- https://github.com/jirutka/embedmongo-spring - Spring Factory Bean for EmbedMongo

## Howto

### Maven

**IMPORTANT NOTE: maven groupId and artifactId change**

*	groupId from __de.flapdoodle.embedmongo__ to __de.flapdoodle.embed__
*	artifactId from __de.flapdoodle.embedmongo__ to __de.flapdoodle.embed.mongo__

Stable (Maven Central Repository, Released: 12.09.2014 - wait 24hrs for [maven central](http://repo1.maven.org/maven2/de/flapdoodle/embed/de.flapdoodle.embed.mongo/maven-metadata.xml))

	<dependency>
		<groupId>de.flapdoodle.embed</groupId>
		<artifactId>de.flapdoodle.embed.mongo</artifactId>
		<version>1.46.4</version>
	</dependency>

Snapshots (Repository http://oss.sonatype.org/content/repositories/snapshots)

	<dependency>
		<groupId>de.flapdoodle.embed</groupId>
		<artifactId>de.flapdoodle.embed.mongo</artifactId>
		<version>1.46.5-SNAPSHOT</version>
	</dependency>

### Gradle

Make sure you have mavenCentral() in your repositories or that your enterprise/local server prixies the maven central repository.

	dependencies {
		testCompile group: "de.flapdoodle.embed", name: "de.flapdoodle.embed.mongo", "version: 1.46.4"
	}

### Build from source

When you fork or clone our branch you should always be able to build the library by running

	mvn package

There is also a build.gradle file available which might sometimes be outdated but we try to keep it working. So the gradle command is

	gradle build

Or if you want to use the gradle wrapper:

	./gradlew build

### Changelog

[Changelog](Changelog.md)

### Supported Versions

Versions: some older, a stable and a development version
Support for Linux, Windows and MacOSX.

### Usage
```java
	import de.flapdoodle.embed.mongo.config.ArtifactStoreBuilder;

	...
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
```

### Usage - Optimization

You should make the MongodStarter instance or the RuntimeConfig instance static (per Class or per JVM).
The main purpose of that is the caching of extracted executables and library files. This is done by the ArtifactStore instance
configured with the RuntimeConfig instance. Each instance uses its own cache so multiple RuntimeConfig instances will use multiple
ArtifactStores an multiple caches with much less cache hits:)  

### Usage - custom mongod filename

To avoid windows firewall dialog popups you can chose a stable executable name with UserTempNaming.
This way the firewall dialog only popup once any your done. See [Executable Collision](#executable-collision)
```java
	import de.flapdoodle.embed.mongo.config.ArtifactStoreBuilder;

	...

	int port = 12345;

	Command command = Command.MongoD;

	IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
		.defaults(command)
		.artifactStore(new ArtifactStoreBuilder()
			.defaults(command)
			.download(new DownloadConfigBuilder()
			.defaultsForCommand(command))
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
```

### Unit Tests
```java
	public abstract class AbstractMongoDBTest extends TestCase {

		/**
		 * please store Starter or RuntimeConfig in a static final field
		 * if you want to use artifact store caching (or else disable caching)
		 */
		private static final MongodStarter starter = MongodStarter.getDefaultInstance();

		private MongodExecutable _mongodExe;
		private MongodProcess _mongod;

		private MongoClient _mongo;
		@Override
		protected void setUp() throws Exception {

			_mongodExe = starter.prepare(new MongodConfigBuilder()
				.version(Version.Main.PRODUCTION)
				.net(new Net(12345, Network.localhostIsIPv6()))
				.build());
			_mongod = _mongodExe.start();

			super.setUp();

			_mongo = new MongoClient("localhost", 12345);
		}

		@Override
		protected void tearDown() throws Exception {
			super.tearDown();

			_mongod.stop();
			_mongodExe.stop();
		}

		public Mongo getMongo() {
			return _mongo;
		}

	}
```

#### ... with some more help
```java
	...
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
	...
```

### Customize Download URL
```java
	...
	Command command = Command.MongoD;

	IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
		.defaults(command)
		.artifactStore(new ArtifactStoreBuilder()
			.defaults(command)
			.download(new DownloadConfigBuilder()
				.defaultsForCommand(command)
				.downloadPath("http://my.custom.download.domain/")))
		.build();
	...
```

### Customize Proxy for Download
```java
	...
	Command command = Command.MongoD;

	IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
		.defaults(command)
		.artifactStore(new ArtifactStoreBuilder()
			.defaults(command)
			.download(new DownloadConfigBuilder()
				.defaultsForCommand(command)
				.proxyFactory(new HttpProxyFactory("fooo", 1234))))
			.build();
	...
```

### Customize Artifact Storage
```java
	...
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
	...
```

### Usage - custom mongod process output

#### ... to console with line prefix
```java
	...
	ProcessOutput processOutput = new ProcessOutput(Processors.namedConsole("[mongod>]"),
			Processors.namedConsole("[MONGOD>]"), Processors.namedConsole("[console>]"));

	IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
		.defaults(Command.MongoD)
		.processOutput(processOutput)
		.build();

	MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);
	...
```

#### ... to file
```java
	...
	IStreamProcessor mongodOutput = Processors.named("[mongod>]",
			new FileStreamProcessor(File.createTempFile("mongod", "log")));
	IStreamProcessor mongodError = new FileStreamProcessor(File.createTempFile("mongod-error", "log"));
	IStreamProcessor commandsOutput = Processors.namedConsole("[console>]");

	IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
		.defaults(Command.MongoD)
		.processOutput(new ProcessOutput(mongodOutput, mongodError, commandsOutput))
		.build();

	MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);
	...

	...
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
	...
```

#### ... to java logging
```java
	...
	Logger logger = Logger.getLogger(getClass().getName());

	ProcessOutput processOutput = new ProcessOutput(Processors.logTo(logger, Level.INFO), Processors.logTo(logger,
			Level.SEVERE), Processors.named("[console>]", Processors.logTo(logger, Level.FINE)));

	IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
		.defaultsWithLogger(Command.MongoD,logger)
		.processOutput(processOutput)
		.artifactStore(new ArtifactStoreBuilder()
			.defaults(Command.MongoD)
			.download(new DownloadConfigBuilder()
				.defaultsForCommand(Command.MongoD)
				.progressListener(new LoggingProgressListener(logger, Level.FINE))))
		.build();

	MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);
	...
```

#### ... to default java logging (the easy way)
```java
	...
	Logger logger = Logger.getLogger(getClass().getName());

	IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
		.defaultsWithLogger(Command.MongoD, logger)
		.build();

	MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);
	...
```

#### ... to null device
```java
	...
	Logger logger = Logger.getLogger(getClass().getName());

	IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
		.defaultsWithLogger(Command.MongoD, logger)
		.processOutput(ProcessOutput.getDefaultInstanceSilent())
		.build();

	MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);
	...
```

### Custom Version
```java
	...
	int port = 12345;
	IMongodConfig mongodConfig = new MongodConfigBuilder()
		.version(Versions.withFeatures(new GenericVersion("2.0.7-rc1"),Feature.SYNC_DELAY))
		.net(new Net(port, Network.localhostIsIPv6()))
		.build();

	MongodStarter runtime = MongodStarter.getDefaultInstance();
	MongodProcess mongod = null;

	MongodExecutable mongodExecutable = null;
	try {
		mongodExecutable = runtime.prepare(mongodConfig);
		mongod = mongodExecutable.start();

		...

	} finally {
		if (mongod != null) {
			mongod.stop();
		}
		if (mongodExecutable != null)
			mongodExecutable.stop();
	}
	...
```

### Main Versions
```java
	IVersion version = Version.V2_2_5;
	// uses latest supported 2.2.x Version
	version = Version.Main.V2_2;
	// uses latest supported production version
	version = Version.Main.PRODUCTION;
	// uses latest supported development version
	version = Version.Main.DEVELOPMENT;
```

### Use Free Server Port

	Warning: maybe not as stable, as expected.

#### ... by hand
```java
	...
	int port = Network.getFreeServerPort();
	...
```

#### ... automagic
```java
	...
	IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION).build();

	MongodStarter runtime = MongodStarter.getDefaultInstance();

	MongodExecutable mongodExecutable = null;
	MongodProcess mongod = null;
	try {
		mongodExecutable = runtime.prepare(mongodConfig);
		mongod = mongodExecutable.start();

		MongoClient mongo = new MongoClient(new ServerAddress(mongodConfig.net().getServerAddress(), mongodConfig.net().getPort()));
		...

	} finally {
		if (mongod != null) {
			mongod.stop();
		}
		if (mongodExecutable != null)
			mongodExecutable.stop();
	}
	...
```

### ... custom timeouts
```java
	...
	IMongodConfig mongodConfig = new MongodConfigBuilder()
		.version(Version.Main.PRODUCTION)
		.timeout(new Timeout(30000))
		.build();
	...
```

### Command Line Post Processing
```java
	...
	ICommandLinePostProcessor postProcessor= ...

	IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
		.defaults(Command.MongoD)
		.commandLinePostProcessor(postProcessor)
		.build();
	...
```
### Custom Command Line Options

We changed the syncDelay to 0 which turns off sync to disc. To turn on default value used defaultSyncDelay().
```java
	IMongodConfig mongodConfig = new MongodConfigBuilder()
	.version(Version.Main.PRODUCTION)
	.cmdOptions(new MongoCmdOptionsBuilder()
		.syncDeplay(10)
		.useNoPrealloc(false)
		.useSmallFiles(false)
		.useNoJournal(false)
		.enableTextSearch(true)
		.build())
	.build();
	...
```

### Snapshot database files from temp dir

We changed the syncDelay to 0 which turns off sync to disc. To get the files to create an snapshot you must turn on default value (use defaultSyncDelay()).
```java
	IMongodConfig mongodConfig = new MongodConfigBuilder()
	.version(Version.Main.PRODUCTION)
	.processListener(new ProcessListenerBuilder()
		.copyDbFilesBeforeStopInto(destination)
		.build())
	.cmdOptions(new MongoCmdOptionsBuilder()
		.defaultSyncDelay()
		.build())
	.build();
	...
```

### Custom database directory  

If you set a custom database directory, it will not be deleted after shutdown
```java
	Storage replication = new Storage("/custom/databaseDir",null,0);

	IMongodConfig mongodConfig = new MongodConfigBuilder()
			.version(Version.Main.PRODUCTION)
			.replication(replication)
			.build();
	...
```

### Start mongos with mongod instance

this is an very easy example to use mongos and mongod
```java
	int port = 12121;
	int defaultConfigPort = 12345;
	String defaultHost = "localhost";

	MongodProcess mongod = startMongod(defaultConfigPort);

	try {
		MongosProcess mongos = startMongos(port, defaultConfigPort, defaultHost);
		try {
			MongoClient mongoClient = new MongoClient(defaultHost, defaultConfigPort);
			System.out.println("DB Names: " + mongoClient.getDatabaseNames());
		} finally {
			mongos.stop();
		}
	} finally {
		mongod.stop();
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
```

### Import JSON file with mongoimport command
```java
    public void testStartAndStopMongoImportAndMongod() throws UnknownHostException, IOException {
        int defaultConfigPort = 12345;
        String defaultHost = "localhost";
        String database = "importTestDB";
        String collection = "importedCollection";
        String jsonFile=filePathAsString;
        MongodProcess mongod = startMongod(defaultConfigPort);

        try {
            MongoImportProcess mongoImport = startMongoImport(defaultConfigPort, database,collection,jsonFile,true,true,true);
            try {
                MongoClient mongoClient = new MongoClient(defaultHost, defaultConfigPort);
                System.out.println("DB Names: " + mongoClient.getDatabaseNames());
            } finally {
                mongoImport.stop();
            }
        } finally {
            mongod.stop();
        }
    }

    private MongoImportProcess startMongoImport(int port, String dbName, String collection, String jsonFile, Boolean jsonArray,Boolean upsert, Boolean drop) throws UnknownHostException,
            IOException {
        IMongoImportConfig mongoImportConfig = new MongoImportConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(port, Network.localhostIsIPv6()))
                .db(dbName)
                .collection(collection)
                .upsert(upsert)
                .dropCollection(drop)
                .jsonArray(jsonArray)
                .importFile(jsonFile)
                .build();

        MongoImportExecutable mongoImportExecutable = MongoImportStarter.getDefaultInstance().prepare(mongoImportConfig);
        MongoImportProcess mongoImport = mongoImportExecutable.start();
        return mongoImport;
    }
```

### Executable Collision

There is a good chance of filename collisions if you use a custom naming schema for the executable (see [Usage - custom mongod filename](#usage---custom-mongod-filename)). If you got an exception, then you should make your RuntimeConfig or MongoStarter class or jvm static (static final in your test class or singleton class for all tests).

----

YourKit is kindly supporting open source projects with its full-featured Java Profiler.
YourKit, LLC is the creator of innovative and intelligent tools for profiling
Java and .NET applications. Take a look at YourKit's leading software products:
<a href="http://www.yourkit.com/java/profiler/index.jsp">YourKit Java Profiler</a> and
<a href="http://www.yourkit.com/.net/profiler/index.jsp">YourKit .NET Profiler</a>.
