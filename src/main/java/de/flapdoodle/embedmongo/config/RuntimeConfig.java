/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
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
package de.flapdoodle.embedmongo.config;

import de.flapdoodle.embedmongo.extract.ITempNaming;
import de.flapdoodle.embedmongo.extract.UUIDTempNaming;
import de.flapdoodle.embedmongo.output.IProgressListener;
import de.flapdoodle.embedmongo.output.StandardConsoleProgressListener;

/**
 *
 */
public class RuntimeConfig {

	private IProgressListener progressListener = new StandardConsoleProgressListener();
	private String downloadPath = "http://fastdl.mongodb.org/";
	private IArtifactStoragePathNaming artifactStorePath = new ArtifactStoreInUserHome();
	private ITempNaming defaultfileNaming = new UUIDTempNaming();
	private ITempNaming executableNaming = defaultfileNaming;
	private MongodProcessOutputConfig mongodOutputConfig = MongodProcessOutputConfig.getDefaultInstance();

	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}

	public String getDownloadPath() {
		return downloadPath;
	}

	public void setProgressListener(IProgressListener progressListener) {
		this.progressListener = progressListener;
	}

	public ITempNaming getDefaultfileNaming() {
		return defaultfileNaming;
	}

	public void setDefaultfileNaming(ITempNaming defaultfileNaming) {
		this.defaultfileNaming = defaultfileNaming;
	}

	public ITempNaming getExecutableNaming() {
		return executableNaming;
	}

	public void setExecutableNaming(ITempNaming executableNaming) {
		this.executableNaming = executableNaming;
	}

	public IProgressListener getProgressListener() {
		return progressListener;
	}

	public IArtifactStoragePathNaming getArtifactStorePathNaming() {
		return artifactStorePath;
	}

	public void setArtifactStorePathNaming(IArtifactStoragePathNaming value) {
		this.artifactStorePath = value;
	}

	public MongodProcessOutputConfig getMongodOutputConfig() {
		return mongodOutputConfig;
	}

	public void setMongodOutputConfig(MongodProcessOutputConfig mongodOutputConfig) {
		this.mongodOutputConfig = mongodOutputConfig;
	}
}
