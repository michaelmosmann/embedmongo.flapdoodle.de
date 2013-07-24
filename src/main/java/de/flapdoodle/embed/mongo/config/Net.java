/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
 *
 * with contributions from
 * 	konstantin-ba@github,Archimedes Trajano	(trajano@github)
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
import java.net.InetAddress;
import java.net.UnknownHostException;

import de.flapdoodle.embed.process.runtime.Network;

public class Net {

	private final String bindIp;
	private final int port;
	private final boolean ipv6;

	public Net() throws UnknownHostException, IOException {
		this(null, Network.getFreeServerPort(), Network.localhostIsIPv6());
	}

	public Net(int port, boolean ipv6) {
		this(null, port, ipv6);
	}

	public Net(String bindIp, int port, boolean ipv6) {
		this.bindIp = bindIp;
		this.port = port;
		this.ipv6 = ipv6;
	}

	public String getBindIp() {
		return bindIp;
	}

	public int getPort() {
		return port;
	}

	public boolean isIpv6() {
		return ipv6;
	}

	public InetAddress getServerAddress() throws UnknownHostException {
		if (bindIp != null) {
			return InetAddress.getByName(bindIp);
		}
		return Network.getLocalHost();
	}
}