/**
 * Copyright (C) 2011
 * Michael Mosmann <michael@mosmann.de>
 * Martin Jöhren <m.joehren@googlemail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.embedmongo.runtime;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Network {

	private static final Logger _logger = Logger.getLogger(Network.class.getName());

	private Network() {
		throw new IllegalAccessError("singleton");
	}

	public static boolean localhostIsIPv6() throws UnknownHostException {
		InetAddress addr = InetAddress.getLocalHost();
		byte[] ipAddr = addr.getAddress();
		if (ipAddr.length > 4)
			return true;
		return false;
	}

	public static InetAddress getLocalHost() throws UnknownHostException {
		InetAddress ret = InetAddress.getLocalHost();
		if (!ret.isLoopbackAddress()) {
			ret = InetAddress.getByName("localhost");
			if (!ret.isLoopbackAddress()) {
				_logger.severe("" + ret.getHostAddress() + " is not a loopback address");
			}
		}
		//		_logger.log(Level.SEVERE,"LoopbackAddress: "+ret.isLoopbackAddress());
		//		_logger.log(Level.SEVERE,"LinkLocalAddress: "+ret.isLinkLocalAddress());
		//		_logger.log(Level.SEVERE,"AnyLocalAddress: "+ret.isAnyLocalAddress());
		//		ret=InetAddress.getByAddress(new byte[] {(byte)192,(byte)168,(byte)192,(byte)251});
		return ret;
	}

//	public static InetAddress getLocalHostIPv4() throws UnknownHostException {
//		return InetAddress.getByAddress(new byte[] {127, 0, 0, 1});
//	}

//	public static boolean isLocalHostNotDefaultIPv4() {
//		try {
//			return getLocalHost().equals(getLocalHostIPv4());
//		} catch (UnknownHostException ux) {
//			return false;
//		}
//	}
}
