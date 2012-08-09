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
package de.flapdoodle.embedmongo;

import de.flapdoodle.embedmongo.config.RuntimeConfig;
import de.flapdoodle.process.config.IRuntimeConfig;
import de.flapdoodle.process.config.store.IDownloadConfig;
import de.flapdoodle.process.distribution.Distribution;
import de.flapdoodle.process.io.file.Files;
import de.flapdoodle.process.io.progress.IProgressListener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Class for downloading mongodb runtime
 */
public class Downloader {

	public static final int CONNECTION_TIMEOUT = 10000;
	public static final int READ_TIMEOUT = 10000;
	public static final int DEFAULT_CONTENT_LENGTH = 20 * 1024 * 1024;
	public static final int BUFFER_LENGTH = 1024 * 8;
	public static final int READ_COUNT_MULTIPLIER = 100;

	private Downloader() {

	}

	/**
	 * http://fastdl.mongodb.org/linux/mongodb-linux-x86_64-1.6.5.tgz
	 * http://fastdl.mongodb.org/linux/mongodb-linux-i686-1.7.6.tgz
	 * http://fastdl.mongodb.org/win32/mongodb-win32-x86_64-1.6.5.zip
	 * http://fastdl.mongodb.org/win32/mongodb-win32-i386-1.7.6.zip
	 * <p/>
	 * http://fastdl.mongodb.org/osx/mongodb-osx-i386-1.6.5.tgz
	 * http://fastdl.mongodb.org/osx/mongodb-osx-i386-tiger-1.7.6.tgz
	 */
	public static String getDownloadUrl(IDownloadConfig runtime, Distribution distribution) {
		return runtime.getDownloadPath() + Paths.getPath(distribution);
	}

	public static File download(IDownloadConfig runtime, Distribution distribution) throws IOException {

		String progressLabel = "Download " + distribution;
		IProgressListener progress = runtime.getProgressListener();
		progress.start(progressLabel);

		File ret = Files.createTempFile(runtime.getFileNaming()
				.nameFor("embedmongo-download", "." + Paths.getArchiveType(distribution)));
		if (ret.canWrite()) {

			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(ret));

			URL url = new URL(getDownloadUrl(runtime, distribution));
			URLConnection openConnection = url.openConnection();
			openConnection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (compatible; "
							+ "Embedded MongoDB; +https://github.com/flapdoodle-oss/embedmongo.flapdoodle.de)");
			openConnection.setConnectTimeout(CONNECTION_TIMEOUT);
			openConnection.setReadTimeout(READ_TIMEOUT);

			InputStream downloadStream = openConnection.getInputStream();

			long length = openConnection.getContentLength();
			progress.info(progressLabel, "DownloadSize: " + length);

			if (length == -1) length = DEFAULT_CONTENT_LENGTH;


			try {
				BufferedInputStream bis = new BufferedInputStream(downloadStream);
				byte[] buf = new byte[BUFFER_LENGTH];
				int read = 0;
				long readCount = 0;
				while ((read = bis.read(buf)) != -1) {
					bos.write(buf, 0, read);
					readCount = readCount + read;
					if (readCount > length) length = readCount;

					progress.progress(progressLabel, (int) (readCount * READ_COUNT_MULTIPLIER / length));
				}
			} finally {
				downloadStream.close();
				bos.flush();
				bos.close();
			}
		} else {
			throw new IOException("Can not write " + ret);
		}
		progress.done(progressLabel);
		return ret;
	}

}
