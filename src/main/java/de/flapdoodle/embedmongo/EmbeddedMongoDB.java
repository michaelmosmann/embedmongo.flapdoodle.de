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
import java.util.logging.Level;
import java.util.logging.Logger;

import de.flapdoodle.embedmongo.distribution.Distribution;
import de.flapdoodle.embedmongo.extract.Extractors;
import de.flapdoodle.embedmongo.extract.IExtractor;


public class EmbeddedMongoDB {

	private static final Logger _logger = Logger.getLogger(EmbeddedMongoDB.class.getName());
	
	private final MongodConfig _mongodConfig;

	public static boolean checkDistribution(Distribution distribution) throws IOException {
		if (!LocalArtifactStore.checkArtifact(distribution)) {
			return LocalArtifactStore.store(distribution, Downloader.download(distribution));
		}
		return true;
	}

	public EmbeddedMongoDB(MongodConfig mongodConfig) {
		_mongodConfig = mongodConfig;
	}
	
	public MongodProcess start() {
		try
		{
			Distribution distribution = Distribution.detectFor(_mongodConfig.getVersion());
			if (checkDistribution(distribution)) {
				File artifact = LocalArtifactStore.getArtifact(distribution);
				IExtractor extractor = Extractors.getExtractor(distribution);
				
				File mongodExe = Files.createTempFile("extract",Paths.getMongodExecutable(distribution));
				extractor.extract(artifact, mongodExe,Paths.getMongodExecutablePattern(distribution));

				return new MongodProcess(_mongodConfig,mongodExe);
			}
		}
		catch (IOException iox) {
			_logger.log(Level.SEVERE,"start",iox);
		}
		return null;
	}
	
	
	
}
