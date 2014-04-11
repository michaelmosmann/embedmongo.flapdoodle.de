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

import com.mongodb.*;
import de.flapdoodle.embed.mongo.config.*;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.runtime.MongoImport;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.runtime.Network;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by canyaman on 10/04/14.
 */
public class MongoImportExecutableTest  extends TestCase {

    private static final Logger _logger = Logger.getLogger(MongoImportExecutableTest.class.getName());

    @Test
    public void testStartMongoImport() throws IOException, InterruptedException {

        IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION).net(new Net(12346, Network.localhostIsIPv6())).build();

        IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder().defaults(Command.MongoD).build();

        MongodExecutable mongodExe = MongodStarter.getInstance(runtimeConfig).prepare(mongodConfig);
        MongodProcess mongod = mongodExe.start();

        String jsonFile=Thread.currentThread().getContextClassLoader().getResource("sample.json").toString();
        jsonFile=jsonFile.replaceFirst("file:","");

        MongoImportExecutable mongoImportExecutable=mongoImportExecutable(12346,"importDatabase","importCollection",jsonFile,true,true,true);
        MongoImportProcess mongoImportProcess=null;
        Boolean dataImported=false;
        try {
            mongoImportProcess=mongoImportExecutable.start();
            dataImported=true;
        }catch (Exception e){
            _logger.info("MongoImport exception:" + e.getStackTrace());
            dataImported=false;
        }finally {
           Assert.assertTrue("mongoDB import data in json format", dataImported);
           mongoImportProcess.stop();
        }

        mongod.stop();
        mongodExe.stop();
    }

    private MongoImportExecutable mongoImportExecutable(int port, String dbName, String collection, String jsonFile, Boolean jsonArray,Boolean upsert, Boolean drop) throws UnknownHostException,
            IOException {
        IMongoImportConfig mongoImportConfig = new MongoImportConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(port, Network.localhostIsIPv6()))
                .db(dbName)
                .collection(collection)
                .upsert(upsert)
                .dropCollection(drop)
                .jsonArray(jsonArray)
                .importFile(jsonFile)
                .build();

        MongoImportExecutable mongoImportExecutable = MongoImportStarter.getDefaultInstance().prepare(mongoImportConfig);
        return mongoImportExecutable;
    }

}
