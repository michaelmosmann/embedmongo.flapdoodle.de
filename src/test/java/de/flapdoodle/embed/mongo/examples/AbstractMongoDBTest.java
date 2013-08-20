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

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;

import junit.framework.TestCase;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

// ->
public abstract class AbstractMongoDBTest extends TestCase {

	private MongodExecutable _mongodExe;
	private MongodProcess _mongod;

	private Mongo _mongo;
	@Override
	@Before
	protected void setUp() throws Exception {

		MongodStarter runtime = MongodStarter.getDefaultInstance();
		_mongodExe = runtime.prepare(createMongodConfig());
		_mongod = _mongodExe.start();

		super.setUp();

		_mongo = new MongoClient("localhost", 12345);
	}

	protected IMongodConfig createMongodConfig() throws UnknownHostException, IOException {
		return createMongodConfigBuilder().build();
	}

	protected MongodConfigBuilder createMongodConfigBuilder() throws UnknownHostException, IOException {
		return new MongodConfigBuilder()
			.version(Version.Main.PRODUCTION)
			.net(new Net(12345, Network.localhostIsIPv6()));
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();

		_mongod.stop();
		_mongodExe.stop();
	}

	public Mongo getMongo() {
		return _mongo;
	}

}
// <-
