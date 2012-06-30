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
package de.flapdoodle.embedmongo.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import de.flapdoodle.embedmongo.MongoDBRuntime;
import de.flapdoodle.embedmongo.config.MongodProcessOutputConfig;
import de.flapdoodle.embedmongo.config.RuntimeConfig;
import de.flapdoodle.embedmongo.io.IStreamProcessor;
import de.flapdoodle.embedmongo.io.Processors;


public class TestExampleReadMeCode extends TestCase {
	
	public void testCustomMongodProcessOutput() {
		RuntimeConfig runtimeConfig=new RuntimeConfig();
		runtimeConfig.setMongodOutputConfig(new MongodProcessOutputConfig(Processors.namedConsole("[mongod>]"),
			Processors.namedConsole("[MONGOD>]"), Processors.namedConsole("[console>]")));
		MongoDBRuntime runtime = MongoDBRuntime.getInstance(runtimeConfig);
	}
	
	public void testCustomMongodProcessOutputFile() throws IOException {
		RuntimeConfig runtimeConfig=new RuntimeConfig();
		IStreamProcessor mongodOutput = Processors.named("[mongod>]", new FileStreamProcessor(File.createTempFile("mongod", "log")));
		IStreamProcessor mongodError = new FileStreamProcessor(File.createTempFile("mongod-error", "log"));
		IStreamProcessor commandsOutput = Processors.namedConsole("[console>]");
		
		runtimeConfig.setMongodOutputConfig(new MongodProcessOutputConfig(mongodOutput,
			mongodError, commandsOutput));
		MongoDBRuntime runtime = MongoDBRuntime.getInstance(runtimeConfig);
	}
	
	/*
	 * Ist fürs Readme, deshalb nicht statisch und public
	 */
	public class FileStreamProcessor implements IStreamProcessor {

		private FileOutputStream outputStream;

		public FileStreamProcessor(File file) throws FileNotFoundException {
			outputStream = new FileOutputStream(file);
		}
		
		@Override
		public void process(String block) {
			try {
				outputStream.write(block.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onProcessed() {
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
