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
import java.util.logging.Logger;


public class Readers {

	private Readers() {
		throw new IllegalAccessError("singleton");
	}
	
	public static String readAll(Reader reader) throws IOException {
		StringBuilder sb=new StringBuilder();
		int read;
		char[] buf = new char[512];
		while ((read = reader.read(buf)) != -1) {
			sb.append(new String(buf, 0, read));
		}
		return sb.toString();
	}
}
