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