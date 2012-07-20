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

/**
 * MongoDB Version enum
 */
public enum Version implements MongoDBVersion {

	V1_6_5("1.6.5"),
	@Deprecated
	V1_7_6("1.7.6"),
	@Deprecated
	V1_8_0_rc0("1.8.0-rc0"),
	@Deprecated
	V1_8_0("1.8.0"),
	@Deprecated
	V1_8_1("1.8.1"),
	@Deprecated
	V1_8_2_rc0("1.8.2-rc0"),
	@Deprecated
	V1_8_2("1.8.2"),
	@Deprecated
	V1_8_4("1.8.4"),
	V1_8_5("1.8.5"),
	@Deprecated
	V1_9_0("1.9.0"),
	@Deprecated
	V2_0_1("2.0.1"),
	@Deprecated
	V2_0_4("2.0.4"),
	V2_0_5("2.0.5"),
	V2_0_6("2.0.6"),

	@Deprecated
	V2_1_0("2.1.0"),
	V2_1_1("2.1.1"),
	V2_1_2("2.1.2"),

	V1_6("1.6.5", "1.6.5"),
	V1_8("1.8", "1.8.5"),
	V2_0("2.0", "2.0.6"),
	V2_1("2.1", "2.1.2");


	private final String versionName;
	private final String specificVersion;


	Version(String vName) {
		this.versionName = vName;
		this.specificVersion = vName;
	}

	Version(String vName, String specificVName) {
		this.versionName = vName;
		this.specificVersion = specificVName;
	}

	@Override
	public String getVersionName() {
		return versionName;
	}

	@Override
	public String getSpecificVersion() {
		return specificVersion;
	}


	@Override
	public String toString() {
		return "Version{" +
				"versionName='" + versionName + '\'' +
				", specificVersion='" + specificVersion + '\'' +
				'}';
	}

}
