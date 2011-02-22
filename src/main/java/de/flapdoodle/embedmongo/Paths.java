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

import de.flapdoodle.embedmongo.distribution.Distribution;


public class Paths {

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
		
		String splatform;
		String archiveType;
		switch (distribution.getPlatform()) {
			case Linux:
				splatform="linux";
				archiveType="tgz";
				break;
			case Windows:
				splatform="win32";
				archiveType="zip";
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
		
		return splatform+"/mongodb-"+splatform+"-"+sbitSize+"-"+sversion+"."+archiveType;
	}

}
