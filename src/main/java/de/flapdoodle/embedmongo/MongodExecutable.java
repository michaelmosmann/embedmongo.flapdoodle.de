package de.flapdoodle.embedmongo;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import de.flapdoodle.embedmongo.MongodProcess.JobKiller;
import de.flapdoodle.embedmongo.config.MongodConfig;


public class MongodExecutable {

	private static final Logger _logger = Logger.getLogger(MongodExecutable.class.getName());
	
	private final MongodConfig _mongodConfig;
	private final File _mongodExecutable;
	private boolean _stopped;

	public MongodExecutable(MongodConfig mongodConfig, File mongodExecutable) {
		_mongodConfig = mongodConfig;
		_mongodExecutable = mongodExecutable;
		Runtime.getRuntime().addShutdownHook(new JobKiller());
	}

	public synchronized void cleanup() {
		if (!_stopped) {
			if (!_mongodExecutable.delete())
				_logger.warning("Could not delete temp mongod exe: " + _mongodExecutable);
			_stopped = true;
		}
	}

	class JobKiller extends Thread {

		@Override
		public void run() {
			_logger.warning("CleanUp");
			cleanup();
		}
	}

	public File getFile() {
		return _mongodExecutable;
	}

	public MongodProcess start() throws IOException {
		return new MongodProcess(_mongodConfig,this);
	}

}
