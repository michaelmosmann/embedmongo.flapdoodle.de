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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import de.flapdoodle.embedmongo.distribution.Distribution;
import de.flapdoodle.embedmongo.output.IProgressListener;

public class Downloader {

	private Downloader() {

	}

	/**
	 * http://fastdl.mongodb.org/linux/mongodb-linux-x86_64-1.6.5.tgz
	 * http://fastdl.mongodb.org/linux/mongodb-linux-i686-1.7.6.tgz
	 * http://fastdl.mongodb.org/win32/mongodb-win32-x86_64-1.6.5.zip
	 * http://fastdl.mongodb.org/win32/mongodb-win32-i386-1.7.6.zip
	 * 
	 * http://fastdl.mongodb.org/osx/mongodb-osx-i386-1.6.5.tgz
	 * http://fastdl.mongodb.org/osx/mongodb-osx-i386-tiger-1.7.6.tgz
	 */
	public static String getDownloadUrl(Distribution distribution) {
		return "http://fastdl.mongodb.org/" + Paths.getPath(distribution);
	}

	public static File download(Distribution distribution) throws IOException {
		
		File ret = Files.createTempFile("embedmongo-download","."+Paths.getArchiveType(distribution));
		if (ret.canWrite())
		{
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(ret));
			
			URL url = new URL(getDownloadUrl(distribution));
			URLConnection openConnection = url.openConnection();
			openConnection.setConnectTimeout(10000);
			openConnection.setReadTimeout(10000);
			
			InputStream downloadStream = openConnection.getInputStream();
			try {
				BufferedInputStream bis = new BufferedInputStream(downloadStream);
				byte[] buf = new byte[512];
				int count = 0;
				int read=0;
				int readCount=0;
				while ((read=bis.read(buf)) != -1) {
					bos.write(buf, 0, read);
					readCount=readCount+read;
					
					System.out.print("+");
					count++;
					if (count >= 100) {
						count = 0;
						System.out.println(""+readCount);
					}
				}
			} finally {
				downloadStream.close();
				bos.flush();
				bos.close();
			}
			System.out.println("DONE");
		}
		return ret;
	}

}
