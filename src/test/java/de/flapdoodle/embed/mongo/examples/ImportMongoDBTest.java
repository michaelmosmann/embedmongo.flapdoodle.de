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
package de.flapdoodle.embed.mongo.examples;

import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.*;
import de.flapdoodle.embed.mongo.config.*;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Created by canyaman on 10/04/14.
 */
public class ImportMongoDBTest extends TestCase {

    @Test
    public void testStartAndStopMongoImportAndMongod() throws UnknownHostException, IOException {
        // ->
        int port = 12121;
        int defaultConfigPort = 12345;
        String defaultHost = "localhost";
        String database = "importTestDB";
        String collection = "importedCollection";
        String jsonFile=Thread.currentThread().getContextClassLoader().getResource("sample.json").toString();
        jsonFile=jsonFile.replaceFirst("file:","");
        MongodProcess mongod = startMongod(defaultConfigPort);

        try {
            MongoImportProcess mongoImport = startMongoImport(defaultConfigPort, database,collection,jsonFile,true,true,true);
            try {
                MongoClient mongoClient = new MongoClient(defaultHost, defaultConfigPort);
                System.out.println("DB Names: " + mongoClient.getDatabaseNames());
            } finally {
                mongoImport.stop();
            }
        } finally {
            mongod.stop();
        }
        // <-
    }

    private MongoImportProcess startMongoImport(int port, String dbName, String collection, String jsonFile, Boolean jsonArray,Boolean upsert, Boolean drop) throws UnknownHostException,
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
        MongoImportProcess mongoImport = mongoImportExecutable.start();
        return mongoImport;
    }

    private MongodProcess startMongod(int defaultConfigPort) throws UnknownHostException, IOException {
        IMongodConfig mongoConfigConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(defaultConfigPort, Network.localhostIsIPv6()))
                .configServer(true)
                .build();

        MongodExecutable mongodExecutable = MongodStarter.getDefaultInstance().prepare(mongoConfigConfig);
        MongodProcess mongod = mongodExecutable.start();
        return mongod;
    }
}
