package de.flapdoodle.process.runtime;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import de.flapdoodle.embedmongo.Downloader;
import de.flapdoodle.embedmongo.MongodExecutable;
import de.flapdoodle.embedmongo.Paths;
import de.flapdoodle.embedmongo.config.MongodConfig;
import de.flapdoodle.embedmongo.exceptions.MongodException;
import de.flapdoodle.process.config.ExecutableProcessConfig;
import de.flapdoodle.process.config.IRuntimeConfig;
import de.flapdoodle.process.config.store.IDownloadConfig;
import de.flapdoodle.process.distribution.Distribution;
import de.flapdoodle.process.extract.Extractors;
import de.flapdoodle.process.extract.IExtractor;
import de.flapdoodle.process.io.file.Files;
import de.flapdoodle.process.io.progress.IProgressListener;
import de.flapdoodle.process.store.LocalArtifactStore;


public abstract class Starter<CONFIG extends ExecutableProcessConfig,EXECUTABLE extends Executable<CONFIG, PROCESS>,PROCESS> {
	
	private static Logger logger = Logger.getLogger(Starter.class.getName());
	
	private final IRuntimeConfig runtime;
	
	protected Starter(IRuntimeConfig config) {
		runtime = config;
	}

	protected boolean checkDistribution(Distribution distribution) throws IOException {
		IDownloadConfig downloadConfig = runtime.getDownloadConfig();
		if (!LocalArtifactStore.checkArtifact(downloadConfig, distribution)) {
			return LocalArtifactStore.store(downloadConfig, distribution, Downloader.download(downloadConfig, distribution));
		}
		return true;
	}

	public EXECUTABLE prepare(CONFIG mongodConfig) {
		IProgressListener progress = runtime.getDownloadConfig().getProgressListener();
		
		Distribution distribution = Distribution.detectFor(mongodConfig.getVersion());
		progress.done("Detect Distribution");
		
		try {
			if (checkDistribution(distribution)) {
				progress.done("Check Distribution");
				File mongodExe = extractMongod(distribution);

				return newExecutable(mongodConfig, distribution, runtime, mongodExe);
			} else {
				throw new MongodException("could not find Distribution",distribution);
			}
		} catch (IOException iox) {
			logger.log(Level.SEVERE, "start", iox);
			throw new MongodException(distribution,iox);
		}
	}


	protected File extractMongod(Distribution distribution) throws IOException {
		IDownloadConfig downloadConfig = runtime.getDownloadConfig();
		File artifact = LocalArtifactStore.getArtifact(downloadConfig, distribution);
		IExtractor extractor = Extractors.getExtractor(distribution);

		File mongodExe = Files.createTempFile(
				runtime.getExecutableNaming().nameFor("extract", executableFilename(distribution)));
		extractor.extract(downloadConfig, artifact, mongodExe, executeablePattern(distribution));
		return mongodExe;
	}

	protected abstract EXECUTABLE newExecutable(CONFIG mongodConfig, Distribution distribution, IRuntimeConfig runtime, File mongodExe);
	
	protected abstract Pattern executeablePattern(Distribution distribution);

	protected abstract String executableFilename(Distribution distribution);

}
