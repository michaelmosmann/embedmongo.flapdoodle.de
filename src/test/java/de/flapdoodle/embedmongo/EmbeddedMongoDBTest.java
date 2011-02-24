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
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import de.flapdoodle.embedmongo.distribution.BitSize;
import de.flapdoodle.embedmongo.distribution.Distribution;
import de.flapdoodle.embedmongo.distribution.Platform;
import de.flapdoodle.embedmongo.distribution.Version;
import de.flapdoodle.embedmongo.extract.Extractors;
import de.flapdoodle.embedmongo.extract.IExtractor;
import de.flapdoodle.embedmongo.extract.TgzExtractor;

import junit.framework.TestCase;


public class EmbeddedMongoDBTest extends TestCase {

	public void testNothing() {
		
	}
	
	public void NOtestCheck() throws IOException {
		Distribution distribution = new Distribution(Version.V1_6_5, Platform.Linux, BitSize.B32);
		EmbeddedMongoDB.checkDistribution(distribution);
		
		File artifact = LocalArtifactStore.getArtifact(distribution);
		System.out.println("Artifact: "+artifact);

		IExtractor extractor = Extractors.getExtractor(distribution);
		extractor.extract(artifact, Files.createTempFile("extract",Paths.getMongodExecutable(distribution)),Paths.getMongodExecutablePattern(distribution));
	}
	
}
