/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
 *
 * with contributions from
 * 	konstantin-ba@github,Archimedes Trajano (trajano@github)
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
package de.flapdoodle.embed.mongo.config;

import java.io.IOException;
import java.net.UnknownHostException;

import de.flapdoodle.embed.process.config.ExecutableProcessConfig;
import de.flapdoodle.embed.process.distribution.IVersion;

/**
 *
 */
public class MongodConfig extends AbstractMongoConfig {

	private final Storage storage;
	private final boolean configServer;

	public MongodConfig(IVersion version) throws UnknownHostException, IOException {
		this(version, new Net(), new Storage(), new Timeout());
	}

	public MongodConfig(IVersion version, int port, boolean ipv6) {
		this(version, new Net(null, port, ipv6), new Storage(), new Timeout());
	}

	@Deprecated
	public MongodConfig(IVersion version, int port, boolean ipv6, String databaseDir) {
		this(version, new Net(null, port, ipv6), new Storage(databaseDir, null, 0), new Timeout());
	}

	@Deprecated
	public MongodConfig(IVersion version, String bindIp, int port, boolean ipv6, String databaseDir, String replSetName,
			int oplogSize) {
		this(version, new Net(bindIp, port, ipv6), new Storage(databaseDir, replSetName, oplogSize), new Timeout());
	}

	/*
	 * Preferred constructor to Mongod config server
	 */
	public static MongodConfig getConfigInstance(IVersion version, Net network) {
		return new MongodConfig(version, network, new Storage(), new Timeout(), true);
	}

	public MongodConfig(IVersion version, Net network, Storage storage, Timeout timeout) {
		this(version, network, storage, timeout, false);
	}

	public MongodConfig(IVersion version, Net network, Storage storage, Timeout timeout, boolean configServer) {
		super(version,network,timeout);
		this.storage = storage;
		this.configServer = configServer;
	}

	public Storage replication() {
		return storage;
	}

	public boolean isConfigServer() {
		return configServer;
	}

	/**
	 * @see MongodConfig#net()
	 * @return
	 */
	@Deprecated
	public String getBindIp() {
		return network.getBindIp();
	}

	/**
	 * @see MongodConfig#net()
	 * @return
	 */
	@Deprecated
	public int getPort() {
		return network.getPort();
	}

	/**
	 * @see MongodConfig#net()
	 * @return
	 */
	@Deprecated
	public boolean isIpv6() {
		return network.isIpv6();
	}
}
