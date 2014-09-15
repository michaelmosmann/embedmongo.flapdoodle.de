/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
 *
 * with contributions from
 * 	konstantin-ba@github,Archimedes Trajano	(trajano@github), Victor Rosenberg (harley84@github)
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
package de.flapdoodle.embed.mongo.examples;

import com.mongodb.*;
import de.flapdoodle.embed.mongo.config.MongoCmdOptionsBuilder;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.processlistener.ProcessListenerBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.io.directories.PlatformTempDir;
import de.flapdoodle.embed.process.io.file.Files;
import de.flapdoodle.embed.process.runtime.Network;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;


public class ExperimentalTestEnabledMongoTest extends AbstractMongoDBTest {

    @Override
    protected MongodConfigBuilder createMongodConfigBuilder() throws IOException {
        return new MongodConfigBuilder()
                .net(new Net(12345, Network.localhostIsIPv6()))
                .version(Version.Main.V2_4)
                // the test fails without this line
                .cmdOptions(new MongoCmdOptionsBuilder().enableTextSearch(true).build());
    }

    public void testCreateTextIndex() {
        DB db = getMongo().getDB("test");
        DBCollection col = db.createCollection("testCol", new BasicDBObject());
        col.ensureIndex(new BasicDBObject("subject", "text"));
        col.save(new BasicDBObject("subject", "some text"));

        DBObject textSearchCommand = new BasicDBObject();
        textSearchCommand.put("text", "testCol");
        textSearchCommand.put("search", "*text*");
        CommandResult commandResult = db.command(textSearchCommand);
        assertEquals(1, ((BasicDBList) commandResult.get("results")).size());
    }
}
