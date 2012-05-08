/**
 * Copyright (C) 2011
 * Michael Mosmann <michael@mosmann.de>
 * Martin Jöhren <m.joehren@googlemail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.embedmongo;

import java.io.IOException;

import junit.framework.TestCase;
import de.flapdoodle.embedmongo.config.ArtifactStoreInFixedPath;
import de.flapdoodle.embedmongo.config.IArtifactStoragePathNaming;
import de.flapdoodle.embedmongo.config.MongodConfig;
import de.flapdoodle.embedmongo.config.RuntimeConfig;
import de.flapdoodle.embedmongo.distribution.Version;
import de.flapdoodle.embedmongo.runtime.Network;

public class TestCustomStoragePath extends TestCase {

	public void testCustomPath() throws IOException {

		MongodConfig mongodConfig = new MongodConfig(Version.V2_0, 12345, Network.localhostIsIPv6());

		RuntimeConfig config = new RuntimeConfig();
		ArtifactStoreInFixedPath artifactStorePath = new ArtifactStoreInFixedPath(System.getProperty("user.home") + "/.embeddedMongodbCustomPath");
		config.setArtifactStorePathNaming(artifactStorePath);
		
		MongodExecutable mongodExe = MongoDBRuntime.getInstance(config).prepare(mongodConfig);
		MongodProcess mongod = mongodExe.start();

		mongod.stop();
		mongodExe.cleanup();
	}
}
