# Embedded MongoDB

Embedded MongoDB will provide a platform neutral way for running mongodb in unittests

## Why?

easy access??

## Howto

	int port = 12345;
	MongodProcess mongod = null;

	try {
		mongod = EmbeddedMongoDB.start(new MongodConfig(Version.V1_6_5, port));

		Mongo mongo = new Mongo("localhost", port);
		DB db = mongo.getDB("test");
		DBCollection col = db.createCollection("testCol", new BasicDBObject());
		col.save(new BasicDBObject("testDoc", new Date()));

	} finally {
		if (mongod != null)	mongod.stop();
	}


