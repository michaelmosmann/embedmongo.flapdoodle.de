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


public class ReaderProcessor extends Thread {

	private final Reader _reader;
	private final IStreamProcessor _streamProcessor;

	protected ReaderProcessor(Reader reader,IStreamProcessor streamProcessor) {
		_reader = reader;
		_streamProcessor = streamProcessor;
		
		setDaemon(true);
		start();
	}

	@Override
	public void run() {
		try {
			int read;
			char[] buf = new char[512];
			while ((read = _reader.read(buf)) != -1) {
				_streamProcessor.process(new String(buf, 0, read));
			}
		} catch (IOException iox) {
			// _logger.log(Level.SEVERE,"out",iox);
		}
		
		_streamProcessor.onProcessed();
	}
}
