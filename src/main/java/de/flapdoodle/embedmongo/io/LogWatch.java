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
package de.flapdoodle.embedmongo.io;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.flapdoodle.embedmongo.MongodProcess;
import de.flapdoodle.embedmongo.runtime.NUMA;

public class LogWatch extends Thread {

	private static final Logger _logger = Logger.getLogger(LogWatch.class.getName());

	private final Reader _reader;
	private final StringBuilder _output = new StringBuilder();
	private final String _success;
	private final String _failure;

	private boolean _initWithSuccess = false;

	private LogWatch(Reader reader, String success, String failure) {
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
				System.out.flush();
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

	public static boolean waitForStart(Reader reader, String success, String failed, long timeout) {
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
