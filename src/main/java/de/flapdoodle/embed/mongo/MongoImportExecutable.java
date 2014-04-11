/**
 * Copyright (C) 2011
 *   Can Yaman <can@yaman.me>
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

import de.flapdoodle.embed.mongo.config.IMongoImportConfig;
import de.flapdoodle.embed.mongo.config.IMongosConfig;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.runtime.Executable;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by canyaman on 10/04/14.
 */
public class MongoImportExecutable extends Executable<IMongoImportConfig, MongoImportProcess> {
    public MongoImportExecutable(Distribution distribution, IMongoImportConfig mongodConfig, IRuntimeConfig runtimeConfig,
                            IExtractedFileSet files) {
        super(distribution, mongodConfig, runtimeConfig, files);
    }

    private static Logger logger = Logger.getLogger(MongosExecutable.class.getName());

    @Override
    protected MongoImportProcess start(Distribution distribution, IMongoImportConfig config, IRuntimeConfig runtime)
            throws IOException {
        return new MongoImportProcess(distribution, config, runtime, this);
    }
}
