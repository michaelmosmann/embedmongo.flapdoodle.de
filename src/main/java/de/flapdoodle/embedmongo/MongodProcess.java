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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongodProcess {

	private static final Logger _logger = Logger.getLogger(MongodProcess.class.getName());
	
	private final MongodConfig _config;
	private final File _mongodExecutable;
	private Process _process;

	private File _dbDir;
	
	boolean _stopped=false;

	public MongodProcess(MongodConfig config, File mongodExecutable) throws IOException {
		_config = config;
		_mongodExecutable = mongodExecutable;

		try {
			_dbDir = Files.createTempDir("embedmongo-db");
			ProcessBuilder processBuilder = new ProcessBuilder(getCommandLine(mongodExecutable));
			processBuilder.redirectErrorStream();
			_process = processBuilder.start();
			ConsoleOutput consoleOutput = new ConsoleOutput();
			consoleOutput.setDaemon(true);
			consoleOutput.start();
			
			Runtime.getRuntime().addShutdownHook(new JobKiller());
			
		} catch (IOException iox) {
			if (_dbDir!=null) _dbDir.delete();
			_mongodExecutable.delete();
			throw iox;
		}
	}
	
	private List<String> getCommandLine(File mongodExecutable) {
		return Arrays.asList(_mongodExecutable.getAbsolutePath(),"-v","--port",""+_config.getPort(),"--dbpath",""+_dbDir.getAbsolutePath(),"--noprealloc");
	}

	public synchronized void stop() {
		if (!_stopped) {
			_process.destroy();
			if (!Files.deleteDir(_dbDir)) _logger.warning("Could not delete temp db dir: "+_dbDir);
			if (!_mongodExecutable.delete()) _logger.warning("Could not delete temp mongod exe: "+_mongodExecutable);
			_stopped=true;
		}
	}

	class JobKiller extends Thread {
		@Override
		public void run() {
			stop();
		}
	}
	
	class ConsoleOutput extends Thread {
		@Override
		public void run() {
			try
			{
				InputStream inputStream = _process.getInputStream();
				InputStreamReader reader=new InputStreamReader(inputStream);
				int read;
				char[] buf=new char[512];
				while ((read=reader.read(buf))!=-1) {
					System.out.print(new String(buf,0,read));
				}
			}
			catch (IOException iox) {
//				_logger.log(Level.SEVERE,"out",iox);
			}
		}
	}
}
