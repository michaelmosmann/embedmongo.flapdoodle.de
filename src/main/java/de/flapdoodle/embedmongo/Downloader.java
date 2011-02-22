package de.flapdoodle.embedmongo;

import de.flapdoodle.embedmongo.distribution.Distribution;


public class Downloader {

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
		return "http://fastdl.mongodb.org/"+Paths.getPath(distribution);
	}

}
