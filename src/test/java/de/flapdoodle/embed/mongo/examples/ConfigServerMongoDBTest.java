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

import junit.framework.TestCase;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.Mongo;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.distribution.Version;

public class ConfigServerMongoDBTest extends TestCase {

	private MongodExecutable _mongodExe;
	private MongodProcess _mongod;

	private Mongo _mongo;

	@Override
	protected void setUp() throws Exception {

		MongodStarter runtime = MongodStarter.getDefaultInstance();
		MongodConfig config = MongodConfig.getConfigInstance(Version.Main.PRODUCTION,
				new MongodConfig.Net());
		_mongodExe = runtime.prepare(config);
		_mongod = _mongodExe.start();

		super.setUp();

		_mongo = new Mongo(config.net().getServerAddress().getHostName(),
				config.net().getPort());
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

	/*
	 * Get command list options (http://docs.mongodb.org/manual/reference/command/getCmdLineOpts/)
	 */
	public void testIsConfigServer() {
		DB mongoAdminDB = getMongo().getDB("admin");
		CommandResult cr = mongoAdminDB.command(new BasicDBObject(
				"getCmdLineOpts", 1));
		Object arguments = cr.get("argv");
		if (arguments instanceof BasicDBList) {
			BasicDBList argumentList = (BasicDBList) arguments;
			for(Object arg: argumentList) {
				if (arg.equals("--configsvr")) {
					return;
				}
			}
			fail("Could not find --configsvr in the argument list.");
		}
		else{
			fail("Could not get argv from getCmdLineOpts command.");
		}
	}

}
