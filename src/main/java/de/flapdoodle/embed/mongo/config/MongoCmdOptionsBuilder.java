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

import de.flapdoodle.embed.process.builder.AbstractBuilder;
import de.flapdoodle.embed.process.builder.TypedProperty;


public class MongoCmdOptionsBuilder extends AbstractBuilder<IMongoCmdOptions> {

	protected static final TypedProperty<Integer> SYNC_DELAY = TypedProperty.with("syncDelay", Integer.class);
	protected static final TypedProperty<Boolean> VERBOSE = TypedProperty.with("verbose", Boolean.class);

	
	public MongoCmdOptionsBuilder() {
		property(SYNC_DELAY).setDefault(0);
		property(VERBOSE).setDefault(false);
	}
	
	public MongoCmdOptionsBuilder syncDeplay(int deplay) {
		set(SYNC_DELAY, deplay);
		return this;
	}

	public MongoCmdOptionsBuilder verbose(boolean verbose) {
		set(VERBOSE, verbose);
		return this;
	}

	public MongoCmdOptionsBuilder defaultSyncDeplay() {
		set(SYNC_DELAY, null);
		return this;
	}
	
	@Override
	public IMongoCmdOptions build() {
		Integer syncDelay=get(SYNC_DELAY, null);
		boolean verbose=get(VERBOSE);
		return new MongoCmdOptions(syncDelay,verbose);
	}

	static class MongoCmdOptions implements IMongoCmdOptions {

		private final Integer _syncDelay;
		private final boolean _verbose;

		
		public MongoCmdOptions(Integer syncDelay,boolean verbose) {
			_syncDelay = syncDelay;
			_verbose = verbose;
		}
		
		@Override
		public Integer syncDelay() {
			return _syncDelay;
		}
		
		@Override
		public boolean isVerbose() {
			return _verbose;
		}
		
	}
}
