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
 * Generic version implementation for currently unsupported mongodb versions by embedmongo.
 */
public class GenericVersion implements MongoDBVersion {
	private String versionName;
	private String specificVersion;

	/**
	 * C'tor with version name = specific version
	 * @param vName
	 */
	public GenericVersion(String vName) {
		init(vName, vName);
	}

	/**
	 * C'tor for version and specific version, e.g. vName=2.1 sName=2.1.1
	 * @param vName
	 * @param sName
	 */
	public GenericVersion(String vName, String sName) {
		init(vName, sName);
	}

	private void init(String vName, String sName) {
		versionName = vName;
		specificVersion = sName;
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
		return "GenericVersion{" +
				"versionName='" + versionName + '\'' +
				", specificVersion='" + specificVersion + '\'' +
				'}';
	}
}
