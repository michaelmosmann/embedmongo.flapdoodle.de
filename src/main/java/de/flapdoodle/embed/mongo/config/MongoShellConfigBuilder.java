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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.distribution.IFeatureAwareVersion;
import de.flapdoodle.embed.process.builder.TypedProperty;

public class MongoShellConfigBuilder extends AbstractMongoConfigBuilder<IMongoShellConfig> {

	protected static final TypedProperty<String> JS_SCRIPT = TypedProperty.with("scriptName", String.class);
	protected static final TypedProperty<List> JS_SCRIPT_PARAMETERS = TypedProperty.with("parameters", List.class);

	public MongoShellConfigBuilder() throws UnknownHostException, IOException {
		property(PID_FILE).setDefault("mongo.pid");
	}

	public MongoShellConfigBuilder version(IFeatureAwareVersion version) {
		version().set(version);
		return this;
	}

	public MongoShellConfigBuilder timeout(Timeout timeout) {
		timeout().set(timeout);
		return this;
	}

	public MongoShellConfigBuilder net(Net net) {
		net().set(net);
		return this;
	}

	public MongoShellConfigBuilder cmdOptions(IMongoCmdOptions cmdOptions) {
		cmdOptions().set(cmdOptions);
		return this;
	}

	public MongoShellConfigBuilder scriptName(String scriptName) {
		set(JS_SCRIPT, scriptName);
		return this;
	}
	
	public MongoShellConfigBuilder parameters(String... parameters) {
		return parameters(Arrays.asList(parameters));
	}
	
	public MongoShellConfigBuilder parameters(List<String> parameters) {
		set(JS_SCRIPT_PARAMETERS, parameters);
		return this;
	}

	@Override
	public IMongoShellConfig build() {
		IFeatureAwareVersion version = version().get();
		Net net = net().get();
		Timeout timeout = timeout().get();
		IMongoCmdOptions cmdOptions=get(CMD_OPTIONS);
		String pidFile = get(PID_FILE);
		
		String name = get(JS_SCRIPT,null);
		List<String> parameters = get(JS_SCRIPT_PARAMETERS,new ArrayList<String>());
		if ((name==null) && (parameters.isEmpty())) {
			throw new RuntimeException("you must set parameters or scriptName");
		}

		return new ImmutableMongoShellConfig(version, net, timeout, cmdOptions, pidFile, name, parameters);
	}

	static class ImmutableMongoShellConfig extends ImmutableMongoConfig implements IMongoShellConfig {

		private final String _name;
		private final List<String> _parameters;

		public ImmutableMongoShellConfig(IFeatureAwareVersion version, Net net, Timeout timeout, IMongoCmdOptions cmdOptions, String pidFile, String scriptName, List<String> parameters) {
			super(new SupportConfig(Command.Mongo), version, net, timeout,cmdOptions,pidFile);
			this._name = scriptName;
			this._parameters = Collections.unmodifiableList(new ArrayList<String>(parameters));
		}

		@Override
		public List<String> getScriptParameters() {
			return _parameters;
		}

		@Override
		public String getScriptName() {
			return _name;
		}

	}
}
