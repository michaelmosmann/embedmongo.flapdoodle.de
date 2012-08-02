package de.flapdoodle.embedmongo.output;

import java.util.logging.Logger;

public class LoggingProgressListener implements IProgressListener {

	private final Logger _logger;

	public LoggingProgressListener(Logger logger) {
		_logger = logger;
	}

	@Override
	public void start(String label) {
		_logger.info(label + " starting...");
	}

	@Override
	public void progress(String label, int percent) {
		_logger.info(label + ": " + percent + "% achieved.");
	}

	@Override
	public void info(String label, String message) {
		_logger.info(label + ": " + message);
	}

	@Override
	public void done(String label) {
		_logger.info(label + " achieved successfully.");
	}
}
