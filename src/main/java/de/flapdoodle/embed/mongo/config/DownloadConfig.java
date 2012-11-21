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
package de.flapdoodle.embed.mongo.config;

import de.flapdoodle.embed.mongo.Paths;
import de.flapdoodle.embed.process.config.store.IDownloadConfig;
import de.flapdoodle.embed.process.config.store.IPackageResolver;
import de.flapdoodle.embed.process.extract.ITempNaming;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.io.directories.UserHome;
import de.flapdoodle.embed.process.io.directories.IDirectory;
import de.flapdoodle.embed.process.io.progress.IProgressListener;
import de.flapdoodle.embed.process.io.progress.StandardConsoleProgressListener;

public class DownloadConfig implements IDownloadConfig {

	private ITempNaming fileNaming = new UUIDTempNaming();
	
	private String downloadPath = "http://fastdl.mongodb.org/";

	private IProgressListener progressListener = new StandardConsoleProgressListener();
	private IDirectory artifactStorePath = new UserHome(".embedmongo");

	private String downloadPrefix="embedmongo-download";

	private String userAgent="Mozilla/5.0 (compatible; "
			+  "Embedded MongoDB; +https://github.com/flapdoodle-oss/embedmongo.flapdoodle.de)";

	private IPackageResolver packageResolver=new Paths();

	@Override
	public ITempNaming getFileNaming() {
		return fileNaming;
	}

	public void setFileNaming(ITempNaming fileNaming) {
		this.fileNaming = fileNaming;
	}


	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}

	@Override
	public String getDownloadPath() {
		return downloadPath;
	}

	public void setProgressListener(IProgressListener progressListener) {
		this.progressListener = progressListener;
	}

	@Override
	public IProgressListener getProgressListener() {
		return progressListener;
	}

	public void setArtifactStorePathNaming(IDirectory value) {
		this.artifactStorePath = value;
	}

	@Override
	public IDirectory getArtifactStorePath() {
		return artifactStorePath;
	}

	@Override
	public String getDownloadPrefix() {
		return downloadPrefix;
	}
	
	@Override
	public String getUserAgent() {
		return userAgent;
	}

	@Override
	public IPackageResolver getPackageResolver() {
		return packageResolver;
	}
	
	public void setPackageResolver(IPackageResolver packageResolver) {
		this.packageResolver = packageResolver;
	}
}
