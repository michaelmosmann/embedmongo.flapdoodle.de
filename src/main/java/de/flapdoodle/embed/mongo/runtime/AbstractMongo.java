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
package de.flapdoodle.embed.mongo.runtime;

import java.util.List;

import de.flapdoodle.embed.mongo.config.IMongoCmdOptions;
import de.flapdoodle.embed.mongo.config.IMongoConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Feature;


public class AbstractMongo {

	protected static <T extends IMongoConfig> void applyDefaultOptions(T config, List<String> ret) {
		ret.add("--nohttpinterface");
		if (config.version().enabled(Feature.SYNC_DELAY)) {
			applySyncDelay(ret, config.cmdOptions());
		}
	}

	private static void applySyncDelay(List<String> ret, IMongoCmdOptions cmdOptions) {
		Integer syncDelay=cmdOptions.syncDelay();
		if (syncDelay!=null) {
			ret.add("--syncdelay="+syncDelay);
		}
	}

	protected static void applyNet(Net net, List<String> ret) {
		ret.add("--port");
		ret.add("" + net.getPort());
		if (net.isIpv6()) {
			ret.add("--ipv6");
		}
		if (net.getBindIp()!=null) {
			ret.add("--bind_ip");
			ret.add(net.getBindIp());
		}
	}

}
