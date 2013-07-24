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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.flapdoodle.embed.mongo.config.IMongosConfig;
import de.flapdoodle.embed.mongo.config.MongosConfig;
import de.flapdoodle.embed.mongo.config.SupportConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.runtime.NUMA;

/**
 *
 */
public class Mongos {

	private static Logger logger = Logger.getLogger(Mongos.class.getName());

	public static List<String> getCommandLine(IMongosConfig config, File mongosExecutable)
			throws UnknownHostException {
		List<String> ret = new ArrayList<String>();
		ret.addAll(Arrays.asList(mongosExecutable.getAbsolutePath(), "-v", "--port", "" + config.net().getPort(),
				"--nohttpinterface", 
				"--chunkSize", "1"));
		if (config.net().isIpv6()) {
			ret.add("--ipv6");
		}
		if (config.net().getBindIp()!=null) {
			ret.add("--bind_ip");
			ret.add(config.net().getBindIp());
		}
		if (config.getConfigDB()!=null) {
			ret.add("--configdb");
			ret.add(config.getConfigDB());
		}
		return ret;
	}
}
