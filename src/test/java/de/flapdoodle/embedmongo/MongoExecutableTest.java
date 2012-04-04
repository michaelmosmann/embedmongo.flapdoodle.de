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

import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;

import de.flapdoodle.embedmongo.config.MongodConfig;
import de.flapdoodle.embedmongo.distribution.Version;

/**
 * Integration test for starting and stopping MongodExecutable
 * @author m.joehren
 *
 */
public class MongoExecutableTest extends TestCase {


	@Test
	public void testStartStopTenTimesWithNewMongoExecutable() throws IOException {
		for (int i = 0; i < 10; i++) {
			MongodExecutable mongodExe = MongoDBRuntime.getDefaultInstance().prepare(new MongodConfig(Version.V2_0_1, 12345,
					false));
			MongodProcess mongod = mongodExe.start();
			mongod.stop();
			mongodExe.cleanup();
		}

	}


}