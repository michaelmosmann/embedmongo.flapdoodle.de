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

public class RuntimeConfig {

	private IProgressListener _progressListener = new StandardConsoleProgressListener();
	private String _downloadPath = "http://fastdl.mongodb.org/";
	private String _artifactStorePath=".embedmongo";
	private ITempNaming _defaultfileNaming=new UUIDTempNaming();
	private ITempNaming _executableNaming=_defaultfileNaming;

	public void setDownloadPath(String downloadPath) {
		_downloadPath = downloadPath;
	}

	public String getDownloadPath() {
		return _downloadPath;
	}

	public void setProgressListener(IProgressListener progressListener) {
		_progressListener = progressListener;
	}

	
	public ITempNaming getDefaultfileNaming() {
		return _defaultfileNaming;
	}

	
	public void setDefaultfileNaming(ITempNaming defaultfileNaming) {
		_defaultfileNaming = defaultfileNaming;
	}

	
	public ITempNaming getExecutableNaming() {
		return _executableNaming;
	}

	
	public void setExecutableNaming(ITempNaming executableNaming) {
		_executableNaming = executableNaming;
	}

	public IProgressListener getProgressListener() {
		return _progressListener;
	}

	public String getArtifactStorePath() {
		return _artifactStorePath;
	}
}
