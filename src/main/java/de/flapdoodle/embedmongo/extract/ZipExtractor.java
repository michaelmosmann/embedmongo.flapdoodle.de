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

package de.flapdoodle.embedmongo.extract;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import de.flapdoodle.embedmongo.Files;
import de.flapdoodle.embedmongo.config.RuntimeConfig;
import de.flapdoodle.embedmongo.output.IProgressListener;


public class ZipExtractor implements IExtractor {
	@Override
	public void extract(RuntimeConfig runtime, File source, File destination, Pattern file) throws IOException {
		IProgressListener progressListener = runtime.getProgressListener();
		String progressLabel = "Extract "+source;
		progressListener.start(progressLabel);
		
		FileInputStream fin = new FileInputStream(source);
		BufferedInputStream in = new BufferedInputStream(fin);

		ZipArchiveInputStream zipIn = new ZipArchiveInputStream(in);
		try {
			ZipArchiveEntry entry;
			while ((entry = zipIn.getNextZipEntry()) != null) {
				if (file.matcher(entry.getName()).matches()) {
//					System.out.println("File: " + entry.getName());
					if (zipIn.canReadEntryData(entry)) {
//						System.out.println("Can Read: " + entry.getName());
						long size = entry.getSize();
						Files.write(zipIn, size, destination);
						destination.setExecutable(true);
//						System.out.println("DONE");
						progressListener.done(progressLabel);
					}
					break;

				} else {
//					System.out.println("SKIP File: " + entry.getName());
				}
			}

		} finally {
			zipIn.close();
		}

	}
}
