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

Stable (Maven Central Repository, Released: 22.04.2012 - wait 24hrs for maven central)

	<dependency>
		<groupId>de.flapdoodle.embedmongo</groupId>
		<artifactId>de.flapdoodle.embedmongo</artifactId>
		<version>1.12</version>
	</dependency>

Snapshots (Repository http://oss.sonatype.org/content/repositories/snapshots)

	<dependency>
		<groupId>de.flapdoodle.embedmongo</groupId>
		<artifactId>de.flapdoodle.embedmongo</artifactId>
		<version>1.13-SNAPSHOT</version>
	</dependency>

### Changelog

#### 1.13 (SNAPSHOT)

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
