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
package de.flapdoodle.embed.mongo.config.processlistener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public abstract class ProcessListener {
	
	private ProcessListener() {
		// no instance
	}
	
	public static IMongoProcessListener join(IMongoProcessListener ... listener) {
		return new ProcessListenerCollection(Arrays.asList(listener));
	}

	public static IMongoProcessListener join(List<IMongoProcessListener> listener) {
		return new ProcessListenerCollection(listener);
	}

	static class ProcessListenerCollection implements IMongoProcessListener {

		private final List<IMongoProcessListener> _listener;

		public ProcessListenerCollection(List<IMongoProcessListener> listener) {
			_listener = new ArrayList<IMongoProcessListener>(listener);
		}

		@Override
		public void onBeforeProcessStart(File dbDir, boolean dbDirIsTemp) {
			for (IMongoProcessListener listener : _listener) {
				listener.onBeforeProcessStart(dbDir, dbDirIsTemp);
			}
		}

		@Override
		public void onAfterProcessStop(File dbDir, boolean dbDirIsTemp) {
			for (IMongoProcessListener listener : _listener) {
				listener.onAfterProcessStop(dbDir, dbDirIsTemp);
			}
		}
		
	}
}
