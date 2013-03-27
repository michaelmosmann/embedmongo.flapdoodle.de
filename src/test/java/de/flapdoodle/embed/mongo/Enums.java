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

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

public class Enums {

	private Enums() {
		// no instance
	}

	public static <T extends Enum<T>> Collection<T> values(Class<T> type) {
		return Arrays.asList(type.getEnumConstants());
	}

	public static <T extends Enum<T>> Collection<T> filter(Collection<T> source, Predicate<? super T> predicate) {
		return Collections2.filter(source, predicate);
	}

	public static <T extends Enum<T>> Collection<T> unique(Collection<T> source, Comparator<T> comparator) {
		List<T> ret = Lists.newArrayList();

		if (!source.isEmpty()) {
			List<T> copy = Lists.newArrayList(source);
			Collections.sort(copy, comparator);

			T last = copy.get(0);
			ret.add(last);

			for (T v : copy) {
				if (comparator.compare(v, last) != 0) {
					ret.add(v);
					last = v;
				}
			}
		}
		return ret;
	}

	static class NotDeprecated<T extends Enum<T>> implements Predicate<T> {

		Set<T> deprecatedValues = new HashSet<T>();

		public NotDeprecated(Class<T> type) {
			Field[] fields = type.getDeclaredFields();
			for (Field f : fields) {
				if ((f.isEnumConstant()) && (f.getAnnotation(Deprecated.class) != null)) {
					try {
						deprecatedValues.add((T) f.get(null));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		public boolean apply(T input) {
			return !deprecatedValues.contains(input);
		}

	}
}
