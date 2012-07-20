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
package de.flapdoodle.embedmongo.config;

import de.flapdoodle.embedmongo.distribution.MongoDBVersion;

/**
 *
 */
public class MongodConfig {

	private final MongoDBVersion version;
	private final int port;
	private final String databaseDir;
	private final boolean ipv6;

	public MongodConfig(MongoDBVersion version, int port, boolean ipv6) {
		this(version, port, ipv6, null);
	}

	public MongodConfig(MongoDBVersion version, int port, boolean ipv6, String databaseDir) {
		this.version = version;
		this.port = port;
		this.ipv6 = ipv6;
		this.databaseDir = databaseDir;
	}

	public MongoDBVersion getVersion() {
		return version;
	}

	public int getPort() {
		return port;
	}


	public boolean isIpv6() {
		return ipv6;
	}


	public String getDatabaseDir() {
		return databaseDir;
	}
}
