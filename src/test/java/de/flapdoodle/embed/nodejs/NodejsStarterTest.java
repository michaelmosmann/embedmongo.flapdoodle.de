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
package de.flapdoodle.embed.nodejs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import de.flapdoodle.process.distribution.Distribution;
import de.flapdoodle.process.io.file.Files;
import de.flapdoodle.process.runtime.ICommandLinePostProcessor;

import junit.framework.TestCase;


public class NodejsStarterTest extends TestCase {

	public void testNodejs() throws IOException, InterruptedException {
		NodejsProcess node = null;
		NodejsConfig nodejsConfig = new NodejsConfig(NodejsVersion.V0_8_6);
		
		final File helloWorld = Files.createTempFile("node-hello-world.js");
		Files.write("console.log(\"Running Hello World inside NodeJS\");", helloWorld);
		
		NodejsRuntimeConfig config = new NodejsRuntimeConfig();
		ICommandLinePostProcessor processor=new ICommandLinePostProcessor() {
			@Override
			public List<String> process(Distribution distribution, List<String> args) {
				ArrayList<String> ret = Lists.newArrayList(args);
				ret.add(helloWorld.getAbsolutePath());
				return ret;
			}
		};
		config.setCommandLinePostProcessor(processor);
		NodejsStarter runtime = new NodejsStarter(config);
		
		try {
			NodejsExecutable mongodExecutable = runtime.prepare(nodejsConfig);
			node = mongodExecutable.start();

			Thread.sleep(1000);

		} finally {
			if (node != null)
				node.stop();
			
			Files.forceDelete(helloWorld);
		}

	}
}
