/**
 * Copyright (C) 2011 Michael Mosmann <michael@mosmann.de>
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

package de.flapdoodle.embedmongo;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.bson.BSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import junit.framework.TestCase;

import de.flapdoodle.embedmongo.config.MongodConfig;
import de.flapdoodle.embedmongo.distribution.BitSize;
import de.flapdoodle.embedmongo.distribution.Distribution;
import de.flapdoodle.embedmongo.distribution.Platform;
import de.flapdoodle.embedmongo.distribution.Version;

public class MongoDBRuntimeTest extends TestCase {

	public void testNothing() {

	}
	
	public void testDistributions() throws IOException {
		MongoDBRuntime runtime = MongoDBRuntime.getDefaultInstance();
		check(runtime, new Distribution(Version.V1_7_6,Platform.Linux,BitSize.B32));
		check(runtime, new Distribution(Version.V1_7_6,Platform.Windows,BitSize.B32));
		check(runtime, new Distribution(Version.V1_7_6,Platform.OS_X,BitSize.B32));
	}

	private void check(MongoDBRuntime runtime, Distribution distribution) throws IOException {
		assertTrue("Check",runtime.checkDistribution(distribution));
		File mongod = runtime.extractMongod(distribution);
		assertNotNull("Extracted",mongod);
		assertTrue("Delete",mongod.delete());
	}

	public void testCheck() throws IOException, InterruptedException {
		int port = 12345;
		MongodProcess mongod = null;
		MongoDBRuntime runtime = MongoDBRuntime.getDefaultInstance();
		
		try {
			mongod = runtime.start(new MongodConfig(Version.V1_6_5, port));
			assertNotNull("Mongod", mongod);

			Mongo mongo = new Mongo("localhost", port);
			DB db = mongo.getDB("test");
			DBCollection col = db.createCollection("testCol", new BasicDBObject());
			col.save(new BasicDBObject("testDoc", new Date()));

		} finally {
			if (mongod != null)
				mongod.stop();
		}
	}

}
