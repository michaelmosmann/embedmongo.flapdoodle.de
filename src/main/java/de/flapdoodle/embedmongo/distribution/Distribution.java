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
		return new Distribution(version, Platform.detect(), BitSize.detect());
	}
}
