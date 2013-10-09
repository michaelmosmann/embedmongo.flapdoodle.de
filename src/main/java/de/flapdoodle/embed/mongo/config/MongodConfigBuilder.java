/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
 *
 * with contributions from
 * 	konstantin-ba@github,Archimedes Trajano	(trajano@github)
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
package de.flapdoodle.embed.mongo.config;

import java.io.IOException;
import java.net.UnknownHostException;

import de.flapdoodle.embed.mongo.config.processlistener.IMongoProcessListener;
import de.flapdoodle.embed.mongo.config.processlistener.NoopProcessListener;
import de.flapdoodle.embed.mongo.distribution.IFeatureAwareVersion;
import de.flapdoodle.embed.process.builder.TypedProperty;
import de.flapdoodle.embed.process.distribution.IVersion;

public class MongodConfigBuilder extends AbstractMongoConfigBuilder<IMongodConfig> {

	protected static final TypedProperty<Storage> REPLICATION = TypedProperty.with("Replication", Storage.class);
	protected static final TypedProperty<Boolean> CONFIG_SERVER = TypedProperty.with("ConfigServer", Boolean.class);
	protected static final TypedProperty<IMongoProcessListener> PROCESS_LISTENER = TypedProperty.with("ProcessListener", IMongoProcessListener.class);

	public MongodConfigBuilder() throws UnknownHostException, IOException {
		super();
		property(REPLICATION).setDefault(new Storage());
		property(CONFIG_SERVER).setDefault(false);
		property(PROCESS_LISTENER).setDefault(new NoopProcessListener());
		property(PID_FILE).setDefault("mongod.pid");
	}

	public MongodConfigBuilder version(IFeatureAwareVersion version) {
		version().set(version);
		return this;
	}
	
	public MongodConfigBuilder timeout(Timeout timeout) {
		timeout().set(timeout);
		return this;
	}
	
	public MongodConfigBuilder net(Net net) {
		net().set(net);
		return this;
	}

	public MongodConfigBuilder cmdOptions(IMongoCmdOptions cmdOptions) {
		cmdOptions().set(cmdOptions);
		return this;
	}
	
	public MongodConfigBuilder replication(Storage replication) {
		set(REPLICATION,replication);
		return this;
	}
	
	public MongodConfigBuilder configServer(boolean configServer) {
		set(CONFIG_SERVER,configServer);
		return this;
	}
	
	public MongodConfigBuilder processListener(IMongoProcessListener processListener) {
		set(PROCESS_LISTENER,processListener);
		return this;
	}
	
	public MongodConfigBuilder pidFile(String pidFile) {
        pidFile().set(pidFile);
        return this;
    }
	
	@Override
	public IMongodConfig build() {
		IFeatureAwareVersion version=version().get();
		Net net=net().get();
		Timeout timeout=timeout().get();
		Storage replication=get(REPLICATION);
		boolean configServer=get(CONFIG_SERVER);
		IMongoCmdOptions cmdOptions=get(CMD_OPTIONS);
		IMongoProcessListener processListener=get(PROCESS_LISTENER);
		String pidFile=get(PID_FILE);
		
		return new ImmutableMongodConfig(version, net, timeout, cmdOptions, pidFile, replication, configServer,processListener);
	}

	static class ImmutableMongodConfig extends ImmutableMongoConfig implements IMongodConfig {

		private final Storage _replication;
		private final boolean _configServer;
		private final IMongoProcessListener _processListener;

		public ImmutableMongodConfig(IFeatureAwareVersion version, Net net, Timeout timeout, IMongoCmdOptions cmdOptions, String pidFile, Storage replication,boolean configServer, IMongoProcessListener processListener) {
			super(version, net, timeout,cmdOptions,pidFile);
			_replication = replication;
			_configServer = configServer;
			_processListener = processListener;
		}

		@Override
		public Storage replication() {
			return _replication;
		}

		@Override
		public boolean isConfigServer() {
			return _configServer;
		}

		@Override
		public IMongoProcessListener processListener() {
			return _processListener;
		}
	}
}
