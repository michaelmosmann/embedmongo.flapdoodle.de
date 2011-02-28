package de.flapdoodle.embedmongo.config;

import de.flapdoodle.embedmongo.output.ConsoleProgressListener;
import de.flapdoodle.embedmongo.output.IProgressListener;


public class EmbedderConfig {
	IProgressListener _progressListener=new ConsoleProgressListener();
}
