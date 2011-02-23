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
import java.util.UUID;


public class Files {
	private Files() {
		
	}
	
	public static File createTempFile(String prefix,String postfix) throws IOException {
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		File tempFile = new File(tempDir,prefix+"-"+UUID.randomUUID().toString()+postfix);
		if (!tempFile.createNewFile()) throw new IOException("Could not create Tempfile: "+tempFile);
		return tempFile;
	}
}
