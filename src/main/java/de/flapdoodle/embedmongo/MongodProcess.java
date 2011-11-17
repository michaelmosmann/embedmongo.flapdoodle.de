/**
 * Copyright (C) 2011 Michael Mosmann <michael@mosmann.de>
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.sun.net.httpserver.Authenticator.Success;

import de.flapdoodle.embedmongo.config.MongodConfig;

public class MongodProcess {

	private static final Logger _logger = Logger.getLogger(MongodProcess.class.getName());

	private final MongodConfig _config;
	private final MongodExecutable _mongodExecutable;
	private Process _process;

	private File _dbDir;

	boolean _stopped = false;

	public MongodProcess(MongodConfig config, MongodExecutable mongodExecutable) throws IOException {
		_config = config;
		_mongodExecutable = mongodExecutable;

		try {
			File dbDir;
			if (config.getDatabaseDir() != null) {
				dbDir = Files.createOrCheckDir(config.getDatabaseDir());
			} else {
				dbDir = Files.createTempDir("embedmongo-db");
				_dbDir = dbDir;
			}

			ProcessBuilder processBuilder = new ProcessBuilder(getCommandLine(_config, _mongodExecutable.getFile(), dbDir));
			processBuilder.redirectErrorStream();
			_process = processBuilder.start();
			Runtime.getRuntime().addShutdownHook(new JobKiller());

			InputStream inputStream = _process.getInputStream();
			InputStreamReader reader = new InputStreamReader(inputStream);

			if (LogWatch.waitForStart(reader, "waiting for connections on port", "failed", 2000)) {
				ConsoleOutput consoleOutput = new ConsoleOutput(reader);
				consoleOutput.setDaemon(true);
				consoleOutput.start();
			} else {
				throw new IOException("Could not start mongod process");
			}

		} catch (IOException iox) {
			stop();
			throw iox;
		}
	}

	private static List<String> getCommandLine(MongodConfig config, File mongodExecutable, File dbDir) {
		List<String> ret = new ArrayList<String>();
		ret.addAll(Arrays.asList(mongodExecutable.getAbsolutePath(), "-v", "--port", "" + config.getPort(), "--dbpath", ""
				+ dbDir.getAbsolutePath(), "--noprealloc", "--nohttpinterface", "--smallfiles"));
		if (config.isIpv6()) {
			ret.add("--ipv6");
		}
		return ret;
	}

	public synchronized void stop() {
		if (!_stopped) {
			if (_process != null)
				_process.destroy();
			if ((_dbDir!=null) && (!Files.forceDelete(_dbDir)))
				_logger.warning("Could not delete temp db dir: " + _dbDir);

			if ((_mongodExecutable.getFile()!=null) && (!Files.forceDelete(_mongodExecutable.getFile())))
				_logger.warning("Could not delete mongod executable: " + _mongodExecutable.getFile());

			_stopped = true;
		}
	}

	class JobKiller extends Thread {

		@Override
		public void run() {
			MongodProcess.this.stop();
		}
	}

	static class LogWatch extends Thread {

		private final InputStreamReader _reader;
		private final StringBuilder _output = new StringBuilder();
		private final String _success;
		private final String _failure;

		private boolean _initWithSuccess = false;

		private LogWatch(InputStreamReader reader, String success, String failure) {
			_reader = reader;
			_success = success;
			_failure = failure;
		}

		@Override
		public void run() {
			try {
				int read;
				char[] buf = new char[512];
				while ((read = _reader.read(buf)) != -1) {
					CharSequence line = new String(buf, 0, read);
					System.out.print(line);
					_output.append(line);

					if (_output.indexOf(_success) != -1) {
						_initWithSuccess = true;
						break;
					}
					if (_output.indexOf(_failure) != -1) {
						_initWithSuccess = false;
						break;
					}
				}

			} catch (IOException iox) {
				_logger.log(Level.SEVERE, "out", iox);
			}
			synchronized (this) {
				notify();
			}
		}

		public boolean isInitWithSuccess() {
			return _initWithSuccess;
		}

		public static boolean waitForStart(InputStreamReader reader, String success, String failed, long timeout) {
			LogWatch logWatch = new LogWatch(reader, success, failed);
			logWatch.start();

			synchronized (logWatch) {
				try {
					logWatch.wait(timeout);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return logWatch.isInitWithSuccess();
		}

	}

	static class ConsoleOutput extends Thread {

		private final InputStreamReader _reader;

		public ConsoleOutput(InputStreamReader reader) {
			_reader = reader;
		}

		@Override
		public void run() {
			try {
				int read;
				char[] buf = new char[512];
				while ((read = _reader.read(buf)) != -1) {
					System.out.print(new String(buf, 0, read));
				}
			} catch (IOException iox) {
				//				_logger.log(Level.SEVERE,"out",iox);
			}
		}
	}
}
