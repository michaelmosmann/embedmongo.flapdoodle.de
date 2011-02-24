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

package de.flapdoodle.embedmongo.distribution;

import java.util.Properties;

public class Distribution {

	private final Version _version;
	private final Platform _platform;
	private final BitSize _bitsize;

	public Distribution(Version version, Platform platform, BitSize bitsize) {
		_version = version;
		_platform = platform;
		_bitsize = bitsize;
	}

	public Version getVersion() {
		return _version;
	}

	public Platform getPlatform() {
		return _platform;
	}

	public BitSize getBitsize() {
		return _bitsize;
	}
	
	@Override
	public String toString() {
		return ""+_version+":"+_platform+":"+_bitsize;
	}
	
	public static Distribution detectFor(Version version) {
//		Properties properties = System.getProperties();
//		for (Object key : properties.keySet()) {
//			System.out.println(key+"="+properties.getProperty(key.toString()));
//		}
		BitSize bitSize=BitSize.B32;
		String osArch = System.getProperty("os.arch");
		if (osArch.equals("i686_64")) bitSize=BitSize.B64;
		
		String osName = System.getProperty("os.name");
		Platform platform=null;
		if (osName.equals("Linux")) platform=Platform.Linux;
		if (osName.equals("Windows")) platform=Platform.Linux;
		
		if (platform==null) throw new IllegalArgumentException("Could not detect Platform: os.name="+osName);
		
		return new Distribution(version, platform, bitSize);
	}
}
