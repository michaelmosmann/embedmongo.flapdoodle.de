/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
 *
 * with contributions from
 * 	konstantin-ba@github,Archimedes Trajano (trajano@github)
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
package de.flapdoodle.embed.mongo.runtime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.flapdoodle.embed.mongo.config.MongosConfig;
import de.flapdoodle.embed.mongo.config.SupportConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.runtime.NUMA;

/**
 *
 */
public class Mongos {

	private static Logger logger = Logger.getLogger(Mongos.class.getName());

	/**
	 * Binary sample of shutdown command
	 */
	static final byte[] SHUTDOWN_COMMAND = {0x47, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			(byte) 0xD4, 0x07, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x61, 0x64, 0x6D, 0x69, 0x6E, 0x2E, 0x24, 0x63, 0x6D,
			0x64, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x1B, 0x00, 0x00,
			0x00, 0x10, 0x73, 0x68, 0x75, 0x74, 0x64, 0x6F, 0x77, 0x6E, 0x00, 0x01, 0x00, 0x00, 0x00, 0x08, 0x66, 0x6F,
			0x72, 0x63, 0x65, 0x00, 0x01, 0x00, 0x05, 0x00, 0x00, 0x00, 0x00};
	public static final int SOCKET_TIMEOUT = 2000;
	public static final int CONNECT_TIMEOUT = 2000;
	public static final int BYTE_BUFFER_LENGTH = 512;
	public static final int WAITING_TIME_SHUTDOWN_IN_MS = 100;

	public static boolean sendShutdown(InetAddress hostname, int port) {
		if (!hostname.isLoopbackAddress()) {
			logger.log(Level.WARNING,
					"" + "---------------------------------------\n" + "Your localhost (" + hostname.getHostAddress()
							+ ") is not a loopback adress\n"
							+ "We can NOT send shutdown to mongod, because it is denied from remote."
							+ "---------------------------------------\n");
			return false;
		}

		boolean tryToReadErrorResponse = false;

		final Socket s = new Socket();
		try {
			s.setSoTimeout(SOCKET_TIMEOUT);
			s.connect(new InetSocketAddress(hostname, port), CONNECT_TIMEOUT);
			OutputStream outputStream = s.getOutputStream();
			outputStream.write(SHUTDOWN_COMMAND);
			outputStream.flush();

			tryToReadErrorResponse = true;
			InputStream inputStream = s.getInputStream();
			if (inputStream.read(new byte[BYTE_BUFFER_LENGTH]) != -1) {
				logger.severe("Got some response, should be an error message");
				return false;
			}
			return true;
		} catch (IOException iox) {
			logger.log(Level.WARNING, "sendShutdown", iox);
			if (tryToReadErrorResponse) {
				return true;
			}
		} finally {
			try {
				s.close();
				Thread.sleep(WAITING_TIME_SHUTDOWN_IN_MS);
			} catch (InterruptedException ix) {
				logger.log(Level.WARNING, "sendShutdown", ix);
			} catch (IOException iox) {
				logger.log(Level.WARNING, "sendShutdown", iox);
			}
		}
		return false;
	}

	public static int getMongosProcessId(String output, int defaultValue) {
		Pattern pattern = Pattern.compile("MongoDB starting : pid=([1234567890]+) port", Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(output);
		if (matcher.find()) {
			String value = matcher.group(1);
			return Integer.valueOf(value);
		}
		return defaultValue;
	}

	public static List<String> getCommandLine(MongosConfig config, File mongosExecutable, File dbDir)
			throws UnknownHostException {
		List<String> ret = new ArrayList<String>();
		ret.addAll(Arrays.asList(mongosExecutable.getAbsolutePath(), "-v", "--port", "" + config.getPort(),
				"--nohttpinterface", 
				"--chunkSize", "1"));
		if (config.isIpv6()) {
			ret.add("--ipv6");
		}
		if (config.getBindIp()!=null) {
			ret.add("--bind_ip");
			ret.add(config.getBindIp());
		}
		if (config.getConfigDB()!=null) {
			ret.add("--configdb");
			ret.add(config.getConfigDB());
		}
		return ret;
	}

	public static List<String> enhanceCommandLinePlattformSpecific(Distribution distribution, List<String> commands) {
		if (NUMA.isNUMA(SupportConfig.getInstance(),distribution.getPlatform())) {
			switch (distribution.getPlatform()) {
				case Linux:
					List<String> ret = new ArrayList<String>();
					ret.add("numactl");
					ret.add("--interleave=all");
					ret.addAll(commands);
					return ret;
				default:
					logger.warning("NUMA Plattform detected, but not supported.");
			}
		}
		return commands;
	}

}
