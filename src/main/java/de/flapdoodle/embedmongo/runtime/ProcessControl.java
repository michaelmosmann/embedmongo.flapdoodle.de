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
package de.flapdoodle.embedmongo.runtime;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.logging.Logger;


public class ProcessControl {

	private static final Logger _logger = Logger.getLogger(ProcessControl.class.getName());
	
	private Process _process;
	private InputStreamReader _reader;

	public ProcessControl(Process process) {
		_process = process;
		_reader = new InputStreamReader(_process.getInputStream());
	}
	
	public Reader getReader() {
		return _reader;
	}
	
	public void stop() {
		if (_process!=null) {
			try {
				// streams need to be closed, otherwise process may block
				// see http://kylecartmell.com/?p=9
				_process.getErrorStream().close();
				_process.getInputStream().close();
				_process.getOutputStream().close();
	
			} catch (IOException e) {
				_logger.severe(e.getMessage());
			} finally {
				_process.destroy();
			}
			_reader=null;
		}
	}

	public int waitFor() throws InterruptedException {
		return _process.waitFor();
	}
	
	public static ProcessControl fromCommandLine(List<String> commandLine) throws IOException {
		ProcessBuilder processBuilder = new ProcessBuilder(commandLine);
		processBuilder.redirectErrorStream();
		return new ProcessControl(processBuilder.start());
	}
}
