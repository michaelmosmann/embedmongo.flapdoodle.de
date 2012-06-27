/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
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

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import de.flapdoodle.embedmongo.config.MongodConfig;
import de.flapdoodle.embedmongo.distribution.Version;
import de.flapdoodle.embedmongo.runtime.Network;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Test whether a race condition occurs between setup and tear down of setting
 * up and closing a mongo process.
 * <p/>
 * This test will run a long time based on the download process for all mongodb versions.
 *
 * @author m.joehren
 */
@RunWith(value = Parameterized.class)
public class MongoDBExampleAllVersionsTest {
	@Parameters
	public static Collection<Object[]> data() {
		Collection<Object[]> result = new ArrayList<Object[]>();
		for (Version v : Version.values())
			result.add(new Object[]{v});
		result.clear();
		result.add(new Object[]{Version.V2_0_4});
		return result;
	}

	private static final int PORT = 12345;
	private Version mongoVersion;
	private MongodExecutable mongodExe;
	private MongodProcess mongod;

	private Mongo mongo;
	private static final String DATABASENAME = "mongo_test";

	public MongoDBExampleAllVersionsTest(Version v) {
		this.mongoVersion = v;
	}

	@Before
	public void setUp() throws Exception {

		MongoDBRuntime runtime = MongoDBRuntime.getDefaultInstance();
		mongodExe = runtime.prepare(new MongodConfig(this.mongoVersion, PORT,
				Network.localhostIsIPv6()));
		mongod = mongodExe.start();

		mongo = new Mongo("localhost", PORT);
	}

	@After
	public void tearDown() throws Exception {

		mongod.stop();
		mongodExe.cleanup();
	}

	public Mongo getMongo() {
		return mongo;
	}

	public String getDatabaseName() {
		return DATABASENAME;
	}

	@Test
	public void testInsert1() {
		DB db = mongo.getDB("test");
		DBCollection col = db.createCollection("testCol", new BasicDBObject());
		col.save(new BasicDBObject("testDoc", new Date()));
	}

	@Test
	public void testInsert2() {
		DB db = mongo.getDB("test");
		DBCollection col = db.createCollection("testCol", new BasicDBObject());
		col.save(new BasicDBObject("testDoc", new Date()));
	}

}
