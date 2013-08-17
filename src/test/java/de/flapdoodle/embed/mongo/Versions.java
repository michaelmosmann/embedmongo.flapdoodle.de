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
package de.flapdoodle.embed.mongo;

import java.util.Collection;
import java.util.Comparator;

import de.flapdoodle.embed.mongo.distribution.IFeatureAwareVersion;
import de.flapdoodle.embed.process.distribution.IVersion;

public class Versions {

	private Versions() {
		// no instance
	}

	public static <T extends Enum<T> & IFeatureAwareVersion> Collection<T> testableVersions(Class<T> type) {
		return Enums.unique(Enums.filter(Enums.values(type), new Enums.NotDeprecated<T>(type)), new IVersionComparator<T>());
	}

	static class IVersionComparator<T extends Enum<T> & IVersion> implements Comparator<T> {

		@Override
		public int compare(T o1, T o2) {
			return o1.asInDownloadPath().compareTo(o2.asInDownloadPath());
		}

	}
}
