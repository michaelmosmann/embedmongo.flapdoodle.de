package de.flapdoodle.process.config.store;

import de.flapdoodle.process.extract.ITempNaming;
import de.flapdoodle.process.io.progress.IProgressListener;


public interface IDownloadConfig {
	
	String getDownloadPath();
	
	IProgressListener getProgressListener();

	IArtifactStoragePathNaming getArtifactStorePathNaming();
	
	ITempNaming getFileNaming();

}
