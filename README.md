# Organisation Flapdoodle OSS

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

- In a Maven build using [embedmongo-maven-plugin](https://github.com/joelittlejohn/embedmongo-maven-plugin)
- In a Clojure/Leiningen project using [lein-embongo](https://github.com/joelittlejohn/lein-embongo)
- In a Scala/specs2 specification using [specs2-embedmongo](https://github.com/athieriot/specs2-embedmongo)

### Comments about Embedded MongoDB in the Wild

http://stackoverflow.com/questions/6437226/embedded-mongodb-when-running-integration-tests
http://www.cubeia.com/index.php/blog/archives/436

### Other MongoDB Stuff

- https://github.com/thiloplanz/jmockmongo - mongodb mocking
- https://github.com/lordofthejars/nosql-unit - extended nosql unit testing
- https://github.com/jirutka/embedmongo-spring - Spring Factory Bean for EmbedMongo

## Howto

### Maven

**IMPORTANT NOTE: maven groupId and artifactId change**

*	groupId from __de.flapdoodle.embedmongo__ to __de.flapdoodle.embed__
*	artifactId from __de.flapdoodle.embedmongo__ to __de.flapdoodle.embed.mongo__

Stable (Maven Central Repository, Released: 15.03.2013 - wait 24hrs for [maven central](http://repo1.maven.org/maven2/de/flapdoodle/embed/de.flapdoodle.embed.mongo/maven-metadata.xml))

	<dependency>
		<groupId>de.flapdoodle.embed</groupId>
		<artifactId>de.flapdoodle.embed.mongo</artifactId>
		<version>1.29</version>
	</dependency>

Snapshots (Repository http://oss.sonatype.org/content/repositories/snapshots)

	<dependency>
		<groupId>de.flapdoodle.embed</groupId>
		<artifactId>de.flapdoodle.embed.mongo</artifactId>
		<version>1.30-SNAPSHOT</version>
	</dependency>


### Build from source

When you fork or clone our branch you should always be able to build the library by running 

	mvn package

There is also a build.gradle file available which might sometimes be outdated but we try to keep it working. So the gradle command is

	gradle build

Or if you want to use the gradle wrapper:

	./gradlew build
 

### Changelog

#### 1.30 (SNAPSHOT)

- deprecate old versions (left current production(2.4.1) and previous version left(2.2.3))
- coming versions will change use the 2.4 branch of mongodb as production version
- no release time check for deprecated versions, so use with care

#### 1.29

- major api changes, speed improvement, easier configuration, need to update documentation

#### 1.28

- mongod config refactor
- configurable startup timeout
- added windows2008 support

#### 1.27

- dep version change

#### 1.26

- bind_ip configuration parameter support (MongodConfig constructor)

#### 1.25

- dep version change

#### 1.24

- dep version change

#### 1.23

- added 2.0.7, 2.2.0
- mongodb java driver update to 2.9.0 (test dependency)

#### 1.22

- maven dep version range does not work as expected -> disabled

#### 1.21

- dependency version range for de.flapdoodle.embed.process

#### 1.20

- NPE fix with custom database directory
- custom database directory will not be deleted

#### 1.19

- **massive refactoring, some api breaks**
- **project split**
- some relevant process.stop() improvements

#### 1.18

- added some unit test support (thanx to trajano)
- added some logging only runtime config option
- added 2.0.7-rc1, 2.2.0-rc0
- command line post processor hock

#### 1.17

- added version 2.0.6 and 2.1.2
- version refactoring
- you can now have a custom version, so you do not depend on a new release of this project

#### 1.16

- added version 2.0.5 (main version 2.0 now points to it)
- changed http user agent
- customizeable mongod process output 
- better loopback device detection for mongod process shutdown via command

#### 1.15

- now we send ctrl+c on linux and osx, then send shutdown to server, then taskkill on windows (may the force be with us)
- disable journal for faster turnaround times
- noauth added
- customize artifact storage path
- detection if localhost is not loopback (command shutdown on mongod does not work for remote access)
- formated process output
- much better windows support

#### 1.14

- changed back to send ctrl+c and then send shutdown

#### 1.13

- mongod process management improvement 
 (windows mongod shutdown improvement (alpha) (some trouble stopping process on windows - http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4770092))
 - send shutdown to server
 - send ctrl+c to mongod will perform clean shutdown (untested on windows using taskkill)
- now with 2.1.1

#### 1.12

- NUMA support (alpha) - http://www.mongodb.org/display/DOCS/NUMA

#### 1.11

- timeout fix on slow systems
- stability on win plattforms (hopefully)

#### 1.10

- race condition and cleanup of mongod process

#### 1.9

- fixed 64Bit detection - amd64
- now with main versions 1.6, 1.8, 2.0, 2.1

### Supported Versions

Versions: some older, 1.8.5, 1.9.0, 2.0.6, 2.1.2
Support for Linux, Windows and MacOSX.

### Usage

	int port = 12345;
	MongodConfig mongodConfig = new MongodConfig(Version.Main.V2_0, port, Network.localhostIsIPv6());

	MongodStarter runtime = MongodStarter.getDefaultInstance();

	MongodExecutable mongodExecutable = null; 
	try {
		mongodExecutable = runtime.prepare(mongodConfig);
		MongodProcess mongod = mongodExecutable.start();

		Mongo mongo = new Mongo("localhost", port);
		DB db = mongo.getDB("test");
		DBCollection col = db.createCollection("testCol", new BasicDBObject());
		col.save(new BasicDBObject("testDoc", new Date()));

	} finally {
		if (mongodExecutable != null)
			mongodExecutable.stop();
	}

### Usage - custom mongod filename 

	int port = 12345;
	MongodConfig mongodConfig = new MongodConfig(Version.Main.V2_0, port, Network.localhostIsIPv6());

	RuntimeConfig runtimeConfig = new RuntimeConfig();
	runtimeConfig.setExecutableNaming(new UserTempNaming());
	MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);

	MongodExecutable mongodExecutable=null;
	try {
		mongodExecutable = runtime.prepare(mongodConfig);
		MongodProcess mongod = mongodExecutable.start();

		Mongo mongo = new Mongo("localhost", port);
		DB db = mongo.getDB("test");
		DBCollection col = db.createCollection("testCol", new BasicDBObject());
		col.save(new BasicDBObject("testDoc", new Date()));

	} finally {
		if (mongodExecutable != null)
			mongodExecutable.stop();
	}

### Unit Tests

	public abstract class AbstractMongoDBTest extends TestCase {

		private MongodExecutable _mongodExe;
		private MongodProcess _mongod;

		private Mongo _mongo;
		@Override
		protected void setUp() throws Exception {

			MongodStarter runtime = MongodStarter.getDefaultInstance();
			_mongodExe = runtime.prepare(new MongodConfig(Version.Main.V2_0, 12345, Network.localhostIsIPv6()));
			_mongod = _mongodExe.start();

			super.setUp();

			_mongo = new Mongo("localhost", 12345);
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
	
#### ... with some more help

	...
	MongodForTestsFactory factory = null;
	try {
		factory = MongodForTestsFactory.with(Version.Main.V2_0);

		Mongo mongo = factory.newMongo();
		DB db = mongo.getDB("test-" + UUID.randomUUID());
		DBCollection col = db.createCollection("testCol", new BasicDBObject());
		col.save(new BasicDBObject("testDoc", new Date()));

	} finally {
		if (factory != null)
			factory.shutdown();
	}
	...


### Customize Artifact Storage

	...
	IArtifactStoragePathNaming artifactStorePath = ...
	ITempNaming executableNaming = ...
		
	RuntimeConfig runtimeConfig = new RuntimeConfig();
	runtimeConfig.getDownloadConfig().setArtifactStorePathNaming(artifactStorePath);
	runtimeConfig.setExecutableNaming(executableNaming);

	MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);
	MongodExecutable mongodExe = runtime.prepare(mongodConfig);
	...

### Usage - custom mongod process output 

#### ... to console with line prefix
	...
	RuntimeConfig runtimeConfig = new RuntimeConfig();
	runtimeConfig.setProcessOutput(new ProcessOutput(Processors.namedConsole("[mongod>]"),
		Processors.namedConsole("[MONGOD>]"), Processors.namedConsole("[console>]")));
	MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);
	...

#### ... to file
	...
	RuntimeConfig runtimeConfig=new RuntimeConfig();
	IStreamProcessor mongodOutput = Processors.named("[mongod>]", new FileStreamProcessor(File.createTempFile("mongod", "log")));
	IStreamProcessor mongodError = new FileStreamProcessor(File.createTempFile("mongod-error", "log"));
	IStreamProcessor commandsOutput = Processors.namedConsole("[console>]");
		
	runtimeConfig.setProcessOutput(new ProcessOutput(mongodOutput,
		mongodError, commandsOutput));
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
	
#### ... to java logging
	...
	Logger logger=...
	
	RuntimeConfig runtimeConfig = new RuntimeConfig();
	runtimeConfig.setProcessOutput(new ProcessOutput(Processors.logTo(logger, Level.INFO),
			Processors.logTo(logger, Level.SEVERE), Processors.named("[console>]",Processors.logTo(logger, Level.FINE))));
	MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);
	...

#### ... to default java logging (the easy way)	
	...
	
	Logger logger=...
	
	RuntimeConfig runtimeConfig = RuntimeConfig.getInstance(logger);
	MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);
	...

### Custom Version

	...
	int port = 12345;
	MongodProcess mongod = null;
	MongodConfig mongodConfig = new MongodConfig(new GenericVersion("2.0.7-rc1"), port, Network.localhostIsIPv6());

	MongodStarter runtime = MongodStarter.getDefaultInstance();

	try {
		MongodExecutable mongodExecutable = runtime.prepare(mongodConfig);
		mongod = mongodExecutable.start();

		...
		
	} finally {
		if (mongod != null)
			mongod.stop();
	}
	...
### Main Versions
	...
	IVersion version=Version.V2_0_1;
	// uses latest supported 2.1.x Version
	version=Version.Main.V2_1;
	// uses latest supported production version
	version=Version.Main.PRODUCTION;
	// uses latest supported development version
	version=Version.Main.DEVELOPMENT;


### Use Free Server Port

	Warning: maybe not as stable, as expected.

#### ... by hand
	...
	int port = Network.getFreeServerPort();
	...
	
#### ... automagic
	...
	MongodProcess mongod = null;
	MongodConfig mongodConfig = new MongodConfig(Version.Main.V2_0);

	MongodStarter runtime = MongodStarter.getDefaultInstance();

	try {
		MongodExecutable mongodExecutable = runtime.prepare(mongodConfig);
		mongod = mongodExecutable.start();

		Mongo mongo = new Mongo(new ServerAddress(mongodConfig.net().getServerAddress(), mongodConfig.net().getPort()));
	}
	...

### Command Line Post Processing
	...
	ICommandLinePostProcessor postProcessort=...
	
	RuntimeConfig runtimeConfig = new RuntimeConfig();
	runtimeConfig.setsetCommandLinePostProcessor(postProcessor);
	...

----

YourKit is kindly supporting open source projects with its full-featured Java Profiler.
YourKit, LLC is the creator of innovative and intelligent tools for profiling
Java and .NET applications. Take a look at YourKit's leading software products:
<a href="http://www.yourkit.com/java/profiler/index.jsp">YourKit Java Profiler</a> and
<a href="http://www.yourkit.com/.net/profiler/index.jsp">YourKit .NET Profiler</a>.
