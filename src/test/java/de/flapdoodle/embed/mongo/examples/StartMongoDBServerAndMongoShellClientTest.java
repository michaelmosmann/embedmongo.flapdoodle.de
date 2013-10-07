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

import org.junit.Test;

import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.MongoShellExecutable;
import de.flapdoodle.embed.mongo.MongoShellProcess;
import de.flapdoodle.embed.mongo.MongoShellStarter;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.MongosExecutable;
import de.flapdoodle.embed.mongo.MongosProcess;
import de.flapdoodle.embed.mongo.MongosStarter;
import de.flapdoodle.embed.mongo.config.IMongoShellConfig;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.IMongosConfig;
import de.flapdoodle.embed.mongo.config.MongoShellConfigBuilder;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.MongosConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;


public class StartMongoDBServerAndMongoShellClientTest {

	/*
	 // ->
	 this is an very easy example to use mongos and mongod
	 // <- 
	 */
	@Test
	public void startAndStopMongoDBAndMongoShell() throws UnknownHostException, IOException {
			// ->
		int port = 12345;
		String defaultHost = "localhost";

		MongodProcess mongod = startMongod(port);

		try {
			Thread.sleep(1000);
			MongoShellProcess mongoShell = startMongoShell(port, defaultHost);
			Thread.sleep(1000);
			try {
				MongoClient mongoClient = new MongoClient(defaultHost, port);
				System.out.println("DB Names: " + mongoClient.getDatabaseNames());
			} finally {
				mongoShell.stop();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mongod.stop();
		}
			// <-
	}
	
	// ->
	private MongoShellProcess startMongoShell(int defaultConfigPort, String defaultHost) throws UnknownHostException,
			IOException {
		IMongoShellConfig mongoShellConfig = new MongoShellConfigBuilder()
			.version(Version.Main.PRODUCTION)
			.net(new Net(defaultConfigPort, Network.localhostIsIPv6()))
			.parameters("printjson(db.getCollectionNames())")
			.build();

		MongoShellExecutable mongosExecutable = MongoShellStarter.getDefaultInstance().prepare(mongoShellConfig);
		MongoShellProcess mongos = mongosExecutable.start();
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
	// <-
}
