# Organisation Flapdoodle OSS

We are now a github organisation. You are invited to participate.

# Embedded MongoDB

Embedded MongoDB will provide a platform neutral way for running mongodb in unittests.

## Why?

- dropping databases causing some pains (often you have to wait long time after each test)
- its easy, much easier as installing right version by hand
- you can change version per test

## Comments about Embedded MongoDB in the Wild

http://stackoverflow.com/questions/6437226/embedded-mongodb-when-running-integration-tests
http://www.cubeia.com/index.php/blog/archives/436

## Howto



### Maven

Stable (Maven Central Repository, Released: 13.06.2012 - wait 24hrs for maven central)

	<dependency>
		<groupId>de.flapdoodle.embedmongo</groupId>
		<artifactId>de.flapdoodle.embedmongo</artifactId>
		<version>1.16</version>
	</dependency>

Snapshots (Repository http://oss.sonatype.org/content/repositories/snapshots)

	<dependency>
		<groupId>de.flapdoodle.embedmongo</groupId>
		<artifactId>de.flapdoodle.embedmongo</artifactId>
		<version>1.17-SNAPSHOT</version>
	</dependency>

### Changelog

- Much better windows support in 1.15.
- Please DON'T use 1.13 (had some troubles).

#### 1.17 (SNAPSHOT)

- added version 2.0.6 and 2.1.2

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

Versions: some older, 1.8.5, 1.9.0, 2.0.4, 2.1.0
Support for Linux, Windows and MacOSX.

### Usage

	int port = 12345;
	MongodProcess mongod = null;
	MongoDBRuntime runtime = MongoDBRuntime.getDefaultInstance();
	
	try {
		mongod = runtime.start(new MongodConfig(Version.V2_0, port,Network.localhostIsIPv6()));

		Mongo mongo = new Mongo("localhost", port);
		DB db = mongo.getDB("test");
		DBCollection col = db.createCollection("testCol", new BasicDBObject());
		col.save(new BasicDBObject("testDoc", new Date()));

	} finally {
		if (mongod != null)	mongod.stop();
	}

### Usage - custom mongod filename 

	int port = 12345;
	MongodProcess mongod = null;
	RuntimeConfig runtimeConfig=new RuntimeConfig();
	runtimeConfig.setExecutableNaming(new UserTempNaming());
	MongoDBRuntime runtime = MongoDBRuntime.getInstance(runtimeConfig);
	
	try {
		mongod = runtime.start(new MongodConfig(Version.V2_0, port,Network.localhostIsIPv6()));

		Mongo mongo = new Mongo("localhost", port);
		DB db = mongo.getDB("test");
		DBCollection col = db.createCollection("testCol", new BasicDBObject());
		col.save(new BasicDBObject("testDoc", new Date()));

	} finally {
		if (mongod != null)	mongod.stop();
	}

### Unit Tests

	public abstract class AbstractMongoOMTest extends TestCase {
	
		private MongodExecutable _mongodExe;
		private MongodProcess _mongod;
	
		private Mongo _mongo;
		private static final String DATABASENAME = "mongo_test";
	
		@Override
		protected void setUp() throws Exception {
	
			MongoDBRuntime runtime = MongoDBRuntime.getDefaultInstance();
			_mongodExe = runtime.prepare(new MongodConfig(Version.V2_0, 12345));
			_mongod=_mongodExe.start();
			
			super.setUp();
	
			_mongo = new Mongo("localhost", 12345);
		}
	
		@Override
		protected void tearDown() throws Exception {
			super.tearDown();
			
			_mongod.stop();
			_mongodExe.cleanup();
		}
	
		public Mongo getMongo() {
			return _mongo;
		}
	
		public String getDatabaseName() {
			return DATABASENAME;
		}
	}

### Customize Artifact Storage

	...
	IArtifactStoragePathNaming pathNaming = ...

	RuntimeConfig runtimeConfig=new RuntimeConfig();
	runtimeConfig.setExecutableNaming(new UserTempNaming());
	runtimeConfig.setArtifactStorePathNaming(pathNaming);
	MongoDBRuntime runtime = MongoDBRuntime.getInstance(runtimeConfig);
	...

### Usage - custom mongod process output 

	...
	RuntimeConfig runtimeConfig=new RuntimeConfig();
	runtimeConfig.setMongodOutputConfig(new MongodProcessOutputConfig(Processors.namedConsole("[mongod>]"),
		Processors.namedConsole("[MONGOD>]"), Processors.namedConsole("[console>]")));
	MongoDBRuntime runtime = MongoDBRuntime.getInstance(runtimeConfig);
	...

## Other MongoDB Stuff

- https://github.com/thiloplanz/jmockmongo - mongodb mocking
- https://github.com/lordofthejars/nosql-unit - extended nosql unit testing

