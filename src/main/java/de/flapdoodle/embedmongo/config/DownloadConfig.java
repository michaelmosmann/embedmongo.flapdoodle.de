package de.flapdoodle.embedmongo.config;

import de.flapdoodle.process.config.store.ArtifactStoreInUserHome;
import de.flapdoodle.process.config.store.IArtifactStoragePathNaming;
import de.flapdoodle.process.config.store.IDownloadConfig;
import de.flapdoodle.process.extract.ITempNaming;
import de.flapdoodle.process.extract.UUIDTempNaming;
import de.flapdoodle.process.io.progress.IProgressListener;
import de.flapdoodle.process.io.progress.StandardConsoleProgressListener;

public class DownloadConfig implements IDownloadConfig {

	private ITempNaming fileNaming = new UUIDTempNaming();
	
	private String downloadPath = "http://fastdl.mongodb.org/";

	private IProgressListener progressListener = new StandardConsoleProgressListener();
	private IArtifactStoragePathNaming artifactStorePath = new ArtifactStoreInUserHome();

	@Override
	public ITempNaming getFileNaming() {
		return fileNaming;
	}

	public void setFileNaming(ITempNaming fileNaming) {
		this.fileNaming = fileNaming;
	}


	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}

	@Override
	public String getDownloadPath() {
		return downloadPath;
	}

	public void setProgressListener(IProgressListener progressListener) {
		this.progressListener = progressListener;
	}

	@Override
	public IProgressListener getProgressListener() {
		return progressListener;
	}

	public void setArtifactStorePathNaming(IArtifactStoragePathNaming value) {
		this.artifactStorePath = value;
	}

	@Override
	public IArtifactStoragePathNaming getArtifactStorePathNaming() {
		return artifactStorePath;
	}

}
