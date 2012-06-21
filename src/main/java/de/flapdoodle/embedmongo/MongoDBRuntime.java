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
package de.flapdoodle.embedmongo;

import de.flapdoodle.embedmongo.config.MongodConfig;
import de.flapdoodle.embedmongo.config.RuntimeConfig;
import de.flapdoodle.embedmongo.distribution.Distribution;
import de.flapdoodle.embedmongo.extract.Extractors;
import de.flapdoodle.embedmongo.extract.IExtractor;
import de.flapdoodle.embedmongo.output.IProgressListener;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class MongoDBRuntime {

    private static Logger logger = Logger.getLogger(MongoDBRuntime.class.getName());

    private final RuntimeConfig runtime;

    private MongoDBRuntime(RuntimeConfig config) {
        runtime = config;
    }

    public static MongoDBRuntime getInstance(RuntimeConfig config) {
        return new MongoDBRuntime(config);
    }

    public static MongoDBRuntime getDefaultInstance() {
        return getInstance(new RuntimeConfig());
    }

    public boolean checkDistribution(Distribution distribution) throws IOException {
        if (!LocalArtifactStore.checkArtifact(runtime, distribution)) {
            return LocalArtifactStore.store(runtime, distribution, Downloader.download(runtime, distribution));
        }
        return true;
    }

    public MongodExecutable prepare(MongodConfig mongodConfig) {
        try {
            IProgressListener progress = runtime.getProgressListener();

            Distribution distribution = Distribution.detectFor(mongodConfig.getVersion());
            progress.done("Detect Distribution");
            if (checkDistribution(distribution)) {
                progress.done("Check Distribution");
                File mongodExe = extractMongod(distribution);

                return new MongodExecutable(distribution, mongodConfig, runtime.getMongodOutputConfig(), mongodExe);
            }
        } catch (IOException iox) {
            logger.log(Level.SEVERE, "start", iox);
        }
        return null;
    }


    protected File extractMongod(Distribution distribution) throws IOException {
        File artifact = LocalArtifactStore.getArtifact(runtime, distribution);
        IExtractor extractor = Extractors.getExtractor(distribution);

        File mongodExe = Files.createTempFile(
                runtime.getExecutableNaming().nameFor("extract", Paths.getMongodExecutable(distribution)));
        extractor.extract(runtime, artifact, mongodExe, Paths.getMongodExecutablePattern(distribution));
        return mongodExe;
    }


}
