package de.flapdoodle.embed.mongo.runtime;

import java.util.List;

import de.flapdoodle.embed.mongo.config.IMongoCmdOptions;
import de.flapdoodle.embed.mongo.config.IMongoConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Feature;


public class AbstractMongo {

	protected static <T extends IMongoConfig> void applyDefaultOptions(T config, List<String> ret) {
		ret.add("--nohttpinterface");
		if (config.version().enabled(Feature.SYNC_DELAY)) {
			applySyncDelay(ret, config.cmdOptions());
		}
	}

	private static void applySyncDelay(List<String> ret, IMongoCmdOptions cmdOptions) {
		Integer syncDelay=cmdOptions.syncDelay();
		if (syncDelay!=null) {
			ret.add("--syncdelay="+syncDelay);
		}
	}

	protected static void applyNet(Net net, List<String> ret) {
		ret.add("--port");
		ret.add("" + net.getPort());
		if (net.isIpv6()) {
			ret.add("--ipv6");
		}
		if (net.getBindIp()!=null) {
			ret.add("--bind_ip");
			ret.add(net.getBindIp());
		}
	}

}
