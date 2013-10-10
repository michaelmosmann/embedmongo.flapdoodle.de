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

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import de.flapdoodle.embed.mongo.config.IMongoShellConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;

/**
 *
 */
public class MongoShell extends AbstractMongo {

	public static List<String> getCommandLine(IMongoShellConfig config, IExtractedFileSet files)
			throws UnknownHostException {
		List<String> ret = new ArrayList<String>();
		ret.addAll(Arrays.asList(files.executable().getAbsolutePath()));
		
		String hostname="localhost";
		Net net = config.net();
		if (net.isIpv6()) {
			//ret.add("--ipv6");
		}
		if (net.getBindIp()!=null) {
			hostname=net.getBindIp();
		}

		

		
		ret.add(hostname+":" + net.getPort());
		if (!config.getScriptParameters().isEmpty()) {
			ret.add("--eval");
			StringBuilder eval = new StringBuilder("\"");
			for (String parameter : config.getScriptParameters()) {
				eval.append(parameter).append("; ");
			}
			eval.append("\"");
      ret.add(eval.toString());
		}
		if (config.getScriptName()!=null) {
			ret.add(config.getScriptName());
		}
		
		return ret;
	}
}
