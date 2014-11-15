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
package de.flapdoodle.embed.mongo.distribution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import de.flapdoodle.embed.process.distribution.GenericVersion;

public class VersionsTest {

	@Test
	public void testEquals() {
		assertEquals(Versions.withFeatures(new GenericVersion("2.6.5")), Versions.withFeatures(new GenericVersion("2.6.5")));
		assertEquals(Versions.withFeatures(new GenericVersion("2.6.5"), Feature.TEXT_SEARCH), Versions.withFeatures(new GenericVersion("2.6.5"), Feature.TEXT_SEARCH));
		assertNotEquals(Versions.withFeatures(new GenericVersion("2.6.5")), Versions.withFeatures(new GenericVersion("2.6.5"), Feature.TEXT_SEARCH));
	}

}
