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
