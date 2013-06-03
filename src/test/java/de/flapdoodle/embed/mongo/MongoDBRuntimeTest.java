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
package de.flapdoodle.embed.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import de.flapdoodle.embed.mongo.config.ArtifactStoreBuilder;
import de.flapdoodle.embed.mongo.config.DownloadConfigBuilder;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.BitSize;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.distribution.Platform;
import de.flapdoodle.embed.process.runtime.Network;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// CHECKSTYLE:OFF
public class MongoDBRuntimeTest extends TestCase {

	public void testNothing() {

	}

	public void testDistributions() throws IOException {
		RuntimeConfigBuilder defaultBuilder = new RuntimeConfigBuilder()
				.defaults(Command.MongoD);
		
		IRuntimeConfig config = defaultBuilder.build();

		for (Platform platform : Platform.values()) {
			for (IVersion version : Versions.testableVersions(Version.Main.class)) {
				for (BitSize bitsize : BitSize.values()) {
					// there is no osx 32bit version for v2.2.1
					if (!shipThisVersion(platform, version, bitsize)) {
						check(config, new Distribution(version, platform, bitsize));
					}
				}
			}
		}

		config = defaultBuilder.artifactStore(new ArtifactStoreBuilder()
						.defaults(Command.MongoD)
						.download(new DownloadConfigBuilder()
								.defaults()
								.packageResolver(new Paths(Command.MongoD) {
										@Override
										protected boolean useWindows2008PlusVersion() {
											return true;
										}
									}))).build();
		
		Platform platform = Platform.Windows;
		BitSize bitsize = BitSize.B64;
		for (IVersion version : Versions.testableVersions(Version.Main.class)) {
			// there is no windows 2008 version for 1.8.5 
			boolean skip = ((version.asInDownloadPath().equals(Version.V1_8_5.asInDownloadPath()))
					&& (platform == Platform.Windows) && (bitsize == BitSize.B64));
			if (!skip)
				check(config, new Distribution(version, platform, bitsize));
		}

	}

	private boolean shipThisVersion(Platform platform, IVersion version, BitSize bitsize) {
		// there is no osx 32bit version for v2.2.1 and above
		String currentVersion = version.asInDownloadPath();
        if ((platform == Platform.OS_X) && (bitsize == BitSize.B32)) {
			if (currentVersion.equals(Version.V2_2_1.asInDownloadPath())) return true;
			if (currentVersion.equals(Version.V2_2_3.asInDownloadPath())) return true;
			if (currentVersion.equals(Version.V2_2_4.asInDownloadPath())) return true;
			if (currentVersion.equals(Version.V2_3_0.asInDownloadPath())) return true;
			if (currentVersion.equals(Version.V2_4_0_RC3.asInDownloadPath())) return true;
			if (currentVersion.equals(Version.V2_4_0.asInDownloadPath())) return true;
			if (currentVersion.equals(Version.Main.PRODUCTION.asInDownloadPath())) return true;
			if (currentVersion.equals(Version.Main.DEVELOPMENT.asInDownloadPath())) return true;
		}
		return false;
	}

	private void check(IRuntimeConfig runtime, Distribution distribution) throws IOException {
		assertTrue("Check", runtime.getArtifactStore().checkDistribution(distribution));
		File mongod = runtime.getArtifactStore().extractExe(distribution);
		assertNotNull("Extracted", mongod);
		assertTrue("Delete", mongod.delete());
	}

	public void testCheck() throws IOException, InterruptedException {

		Timer timer = new Timer();

		int port = 12345;
		MongodProcess mongodProcess = null;
		MongodExecutable mongod = null;
		
		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder().defaults(Command.MongoD).build();
		MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);

		timer.check("After Runtime");

		try {
			mongod = runtime.prepare(new MongodConfig(Version.Main.PRODUCTION, port, Network.localhostIsIPv6()));
			timer.check("After mongod");
			assertNotNull("Mongod", mongod);
			mongodProcess = mongod.start();
			timer.check("After mongodProcess");

			Mongo mongo = new Mongo("localhost", port);
			timer.check("After Mongo");
			DB db = mongo.getDB("test");
			timer.check("After DB test");
			DBCollection col = db.createCollection("testCol", new BasicDBObject());
			timer.check("After Collection testCol");
			col.save(new BasicDBObject("testDoc", new Date()));
			timer.check("After save");

		} finally {
			if (mongodProcess != null)
				mongodProcess.stop();
			timer.check("After mongodProcess stop");
			if (mongod != null)
				mongod.stop();
			timer.check("After mongod stop");
		}
		timer.log();
	}

	static class Timer {

		long _start = System.currentTimeMillis();
		long _last = _start;

		List<String> _log = new ArrayList<String>();

		void check(String label) {
			long current = System.currentTimeMillis();
			long diff = current - _last;
			_last = current;

			_log.add(label + ": " + diff + "ms");
		}

		void log() {
			for (String line : _log) {
				System.out.println(line);
			}
		}
	}

}
