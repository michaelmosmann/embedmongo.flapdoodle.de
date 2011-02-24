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

import java.util.regex.Pattern;

import de.flapdoodle.embedmongo.distribution.ArchiveType;
import de.flapdoodle.embedmongo.distribution.Distribution;


public class Paths {

	public static Pattern getMongodExecutablePattern(Distribution distribution) {
		return Pattern.compile(".*"+getMongodExecutable(distribution));
	}
	
	public static String getMongodExecutable(Distribution distribution) {
		String mongodPattern;
		switch (distribution.getPlatform()) {
			case Linux:
				mongodPattern="mongod";
				break;
			case Windows:
				mongodPattern="mongod.exe";
				break;
			default:
				throw new IllegalArgumentException("Unknown Platform "+distribution.getPlatform());
		}
		return mongodPattern;
	}
	
	public static ArchiveType getArchiveType(Distribution distribution) {
		ArchiveType archiveType;
		switch (distribution.getPlatform()) {
			case Linux:
				archiveType=ArchiveType.TGZ;
				break;
			case Windows:
				archiveType=ArchiveType.ZIP;
				break;
			default:
				throw new IllegalArgumentException("Unknown Platform "+distribution.getPlatform());
		}
		return archiveType;
	}
	
	public static String getPath(Distribution distribution) {
		String sversion;
		switch (distribution.getVersion()) {
			case V1_6_5:
				sversion="1.6.5";
				break;
			case V1_7_6:
				sversion="1.7.6";
				break;
			default:
				throw new IllegalArgumentException("Unknown Version "+distribution.getVersion());
		}
		
		ArchiveType archiveType=getArchiveType(distribution);
		String sarchiveType;
		switch (archiveType) {
			case TGZ:
				sarchiveType="tgz";
				break;
			case ZIP:
				sarchiveType="zip";
				break;
			default:
				throw new IllegalArgumentException("Unknown ArchiveType "+archiveType);
		}
		
		String splatform;
		switch (distribution.getPlatform()) {
			case Linux:
				splatform="linux";
				break;
			case Windows:
				splatform="win32";
				break;
			default:
				throw new IllegalArgumentException("Unknown Platform "+distribution.getPlatform());
		}
		
		String sbitSize;
		switch (distribution.getBitsize()) {
			case B32:
				switch (distribution.getPlatform())
				{
					case Linux:
						sbitSize="i686";
						break;
					case Windows:
						sbitSize="i386";
						break;
					default:
						throw new IllegalArgumentException("Unknown Platform "+distribution.getPlatform());
				}
				break;
			case B64:
				sbitSize="x86_64";
				break;
			default:
				throw new IllegalArgumentException("Unknown BitSize "+distribution.getBitsize());
		}
		
		return splatform+"/mongodb-"+splatform+"-"+sbitSize+"-"+sversion+"."+sarchiveType;
	}

}
