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
package de.flapdoodle.embed.mongo.config;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.Paths;
import de.flapdoodle.embed.process.config.store.IDownloadPath;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.Platform;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.io.directories.UserHome;
import de.flapdoodle.embed.process.io.progress.StandardConsoleProgressListener;

public class DownloadConfigBuilder extends de.flapdoodle.embed.process.config.store.DownloadConfigBuilder {

	public DownloadConfigBuilder packageResolverForCommand(Command command) {
		packageResolver(new Paths(command));
		return this;
	}

	public DownloadConfigBuilder defaultsForCommand(Command command) {
		return defaults().packageResolverForCommand(command);
	}

	public DownloadConfigBuilder defaults() {
		fileNaming().setDefault(new UUIDTempNaming());
		downloadPath().setDefault(new PlattformDependendDownloadPath());
		progressListener().setDefault(new StandardConsoleProgressListener());
		artifactStorePath().setDefault(new UserHome(".embedmongo"));
		downloadPrefix().setDefault(new DownloadPrefix("embedmongo-download"));
		userAgent().setDefault(new UserAgent("Mozilla/5.0 (compatible; Embedded MongoDB; +https://github.com/flapdoodle-oss/embedmongo.flapdoodle.de)"));
		return this;
	}

	private static class PlattformDependendDownloadPath implements IDownloadPath {

		@Override
		public String getPath(Distribution distribution) {
			if (distribution.getPlatform()==Platform.Windows) {
				return "http://downloads.mongodb.org/";
			}
			return "http://fastdl.mongodb.org/";
		}
		
	}
}
