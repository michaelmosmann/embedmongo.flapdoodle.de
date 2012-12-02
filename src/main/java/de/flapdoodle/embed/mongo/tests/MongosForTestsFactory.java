/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
 *
 * with contributions from
 * 	konstantin-ba@github,Archimedes Trajano (trajano@github)
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
package de.flapdoodle.embed.mongo.tests;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.logging.Logger;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.MongosExecutable;
import de.flapdoodle.embed.mongo.MongosProcess;
import de.flapdoodle.embed.mongo.MongosStarter;
import de.flapdoodle.embed.mongo.config.AbstractMongoConfig.Net;
import de.flapdoodle.embed.mongo.config.AbstractMongoConfig.Timeout;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.MongosConfig;
import de.flapdoodle.embed.mongo.config.MongosRuntimeConfig;
import de.flapdoodle.embed.mongo.config.RuntimeConfig;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.runtime.Network;

/**
 * This class encapsulates everything that would be needed to do embedded
 * MongoDB with sharding testing.
 */
public class MongosForTestsFactory {

	private static Logger logger = Logger.getLogger(MongosForTestsFactory.class
			.getName());

	public static MongosForTestsFactory with(final IVersion version)
			throws IOException {
		return new MongosForTestsFactory(version);
	}

	private final MongodExecutable mongoConfigExecutable;

	private final MongodProcess mongoConfigProcess;

	private final MongosExecutable mongosExecutable;

	private final MongosProcess mongosProcess;

	/**
	 * Create the testing utility using the latest production version of
	 * MongoDB.
	 * 
	 * @throws IOException
	 */
	public MongosForTestsFactory() throws IOException {
		this(Version.Main.V2_0);
	}

	/**
	 * Create the testing utility using the specified version of MongoDB.
	 * 
	 * @param version
	 *            version of MongoDB.
	 */
	public MongosForTestsFactory(final IVersion version) throws IOException {

		final MongodStarter mongoConfigRuntime = MongodStarter.getInstance(RuntimeConfig
				.getInstance(logger));

		int configServerPort = 27019;
		int mongosPort = 27017;
		mongoConfigExecutable = mongoConfigRuntime.prepare(MongodConfig.getConfigInstance(version, new Net(configServerPort, Network.localhostIsIPv6())));
		mongoConfigProcess = mongoConfigExecutable.start();

		final MongosStarter runtime = MongosStarter.getInstance(MongosRuntimeConfig
				.getInstance(logger));
		mongosExecutable = runtime.prepare(new MongosConfig(version, new Net(mongosPort, Network.localhostIsIPv6()), new Timeout(), Network.getLocalHost().getHostName() + ":" + configServerPort));
		mongosProcess = mongosExecutable.start();
	}

	/**
	 * Creates a new Mongo connection.
	 * 
	 * @throws MongoException
	 * @throws UnknownHostException
	 */
	public Mongo newMongo() throws UnknownHostException, MongoException {
		return new Mongo(new ServerAddress(mongosProcess.getConfig().net().getServerAddress(),
				mongosProcess.getConfig().net().getPort()));
	}
	
	/**
	 * Creates a new DB with unique name for connection.
	 */
	public DB newDB(Mongo mongo) {
		return mongo.getDB(UUID.randomUUID().toString());
	}

	/**
	 * Cleans up the resources created by the utility.
	 */
	public void shutdown() {
		mongosProcess.stop();
		mongosExecutable.stop();
	}
}
