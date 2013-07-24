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

import de.flapdoodle.embed.process.builder.AbstractBuilder;
import de.flapdoodle.embed.process.builder.IProperty;
import de.flapdoodle.embed.process.builder.TypedProperty;
import de.flapdoodle.embed.process.distribution.IVersion;

public abstract class AbstractMongoConfigBuilder<T extends IMongoConfig> extends AbstractBuilder<T> {

	protected static final TypedProperty<IVersion> VERSION = TypedProperty.with("Version", IVersion.class);
	protected static final TypedProperty<Timeout> TIMEOUT = TypedProperty.with("Timeout", Timeout.class);
	protected static final TypedProperty<Net> NET = TypedProperty.with("Net", Net.class);

	
	public AbstractMongoConfigBuilder() throws UnknownHostException, IOException  {
		timeout().setDefault(new Timeout());
		net().setDefault(new Net());
	}
	
	protected IProperty<IVersion> version() {
		return property(VERSION);
	}

	protected IProperty<Timeout> timeout() {
		return property(TIMEOUT);
	}

	protected IProperty<Net> net() {
		return property(NET);
	}

	static class ImmutableMongoConfig implements IMongoConfig {

		private final IVersion _version;
		private final Timeout _timeout;
		private final Net _net;

		public ImmutableMongoConfig(IVersion version, Net net, Timeout timeout) {
			super();
			_version = version;
			_net = net;
			_timeout = timeout;
		}

		@Override
		public IVersion version() {
			return _version;
		}

		@Override
		public Timeout timeout() {
			return _timeout;
		}

		@Override
		public Net net() {
			return _net;
		}

	}
}
