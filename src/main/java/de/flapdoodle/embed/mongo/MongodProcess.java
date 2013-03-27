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
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.SupportConfig;
import de.flapdoodle.embed.mongo.runtime.Mongod;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.ISupportConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.io.directories.PropertyOrPlatformTempDir;
import de.flapdoodle.embed.process.io.file.Files;

/**
 *
 */
public class MongodProcess extends AbstractMongoProcess<MongodConfig, MongodExecutable, MongodProcess> {

	private static Logger logger = Logger.getLogger(MongodProcess.class.getName());

	private File dbDir;
	boolean dbDirIsTemp;

	public MongodProcess(Distribution distribution, MongodConfig config, IRuntimeConfig runtimeConfig,
			MongodExecutable mongodExecutable) throws IOException {
		super(distribution, config, runtimeConfig, mongodExecutable);

	}

	@Override
	protected void onBeforeProcess(IRuntimeConfig runtimeConfig) throws IOException {
		super.onBeforeProcess(runtimeConfig);

		MongodConfig config = getConfig();

		File tmpDbDir;
		if (config.replication().getDatabaseDir() != null) {
			tmpDbDir = Files.createOrCheckDir(config.replication().getDatabaseDir());
		} else {
			tmpDbDir = Files.createTempDir(PropertyOrPlatformTempDir.defaultInstance(),"embedmongo-db");
			dbDirIsTemp = true;
		}
		this.dbDir = tmpDbDir;
	}

	@Override
	protected ISupportConfig supportConfig() {
		return new SupportConfig(Command.MongoD);
	}

	@Override
	protected List<String> getCommandLine(Distribution distribution, MongodConfig config, File exe) throws IOException {
		return Mongod.enhanceCommandLinePlattformSpecific(distribution, Mongod.getCommandLine(getConfig(), exe, dbDir));
	}

	@Override
	protected void deleteTempFiles() {
		super.deleteTempFiles();
		
		if ((dbDir != null) && (dbDirIsTemp) && (!Files.forceDelete(dbDir))) {
			logger.warning("Could not delete temp db dir: " + dbDir);
		}
	}

}
