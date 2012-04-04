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
package de.flapdoodle.embedmongo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;

import de.flapdoodle.embedmongo.config.RuntimeConfig;
import de.flapdoodle.embedmongo.distribution.Distribution;

public class LocalArtifactStore {

	public static boolean checkArtifact(RuntimeConfig runtime, Distribution distribution) {
		return getArtifact(runtime, distribution) != null;
	}

	public static boolean store(RuntimeConfig runtime, Distribution distribution, File download) {
		File dir = createOrGetBaseDir(runtime);
		File artifactFile = new File(dir, Paths.getPath(distribution));
		createOrCheckDir(artifactFile.getParentFile());
		if (!Files.moveFile(download, artifactFile))
			throw new IllegalArgumentException("Could not move " + download + " to " + artifactFile);
		File checkFile = new File(dir, Paths.getPath(distribution));
		return checkFile.exists() & checkFile.isFile() & checkFile.canRead();
	}

	private static File createOrGetBaseDir(RuntimeConfig runtime) {
		File dir = new File(getPath(runtime));
		createOrCheckDir(dir);
		return dir;
	}

	private static void createOrCheckDir(File dir) {
		if (!dir.exists()) {
			if (!dir.mkdir())
				throw new IllegalArgumentException("Could NOT create Directory " + dir);
		}
		if (!dir.isDirectory())
			throw new IllegalArgumentException("" + dir + " is not a Directory");
	}

	private static String getPath(RuntimeConfig runtime) {
		return System.getProperty("user.home") + "/"+runtime.getArtifactStorePath()+"/";//"/.embedmongo/";
	}

	public static File getArtifact(RuntimeConfig runtime, Distribution distribution) {
		File dir = createOrGetBaseDir(runtime);
		File artifactFile = new File(dir, Paths.getPath(distribution));
		if ((artifactFile.exists()) && (artifactFile.isFile()))
			return artifactFile;
		return null;
	}
}
