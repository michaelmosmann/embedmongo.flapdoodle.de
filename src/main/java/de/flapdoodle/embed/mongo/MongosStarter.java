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
package de.flapdoodle.embed.mongo;

import java.io.File;
import java.util.logging.Logger;

import de.flapdoodle.embed.mongo.config.IMongosConfig;
import de.flapdoodle.embed.mongo.config.MongosConfig;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.runtime.Starter;

/**
 *
 */
public class MongosStarter extends Starter<IMongosConfig,MongosExecutable,MongosProcess> {

	private static Logger logger = Logger.getLogger(MongosStarter.class.getName());

	private MongosStarter(IRuntimeConfig config) {
		super(config);
	}

	public static MongosStarter getInstance(IRuntimeConfig config) {
		return new MongosStarter(config);
	}

	public static MongosStarter getDefaultInstance() {
		return getInstance(new RuntimeConfigBuilder().defaults(Command.MongoS).build());
	}

	@Override
	protected MongosExecutable newExecutable(IMongosConfig mongosConfig, Distribution distribution, IRuntimeConfig runtime, File mongodExe) {
		return new MongosExecutable(distribution, mongosConfig, runtime, mongodExe);
	}
}
