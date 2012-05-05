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
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongodShutdown {

	private static final Logger _logger = Logger.getLogger(MongodShutdown.class.getName());

	/**
	 * Binary sample of shutdown command 
	 */
	static final byte[] SHUTDOWN_COMMAND = {0x47, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			(byte) 0xD4, 0x07, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x61, 0x64, 0x6D, 0x69, 0x6E, 0x2E, 0x24, 0x63, 0x6D,
			0x64, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x1B, 0x00, 0x00, 0x00,
			0x10, 0x73, 0x68, 0x75, 0x74, 0x64, 0x6F, 0x77, 0x6E, 0x00, 0x01, 0x00, 0x00, 0x00, 0x08, 0x66, 0x6F, 0x72, 0x63,
			0x65, 0x00, 0x01, 0x00, 0x05, 0x00, 0x00, 0x00, 0x00,};

	public static boolean sendShutdown(InetAddress hostname, int port) {
		try {
//			Thread.sleep(1000);
			Socket s = new Socket();
			s.setSoTimeout(2000);
			s.connect(new InetSocketAddress(hostname, port), 2000);
			OutputStream outputStream = s.getOutputStream();
			outputStream.write(SHUTDOWN_COMMAND);
			outputStream.flush();
			s.close();
			Thread.sleep(100);
			return true;
		} catch (IOException iox) {
			_logger.log(Level.WARNING, "sendShutdown", iox);
		} catch (InterruptedException ix) {
			_logger.log(Level.WARNING, "sendShutdown", ix);
		}
		return false;
	}

}
