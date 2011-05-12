/**
 * Copyright (C) 2011 Michael Mosmann <michael@mosmann.de>
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

package de.flapdoodle.embedmongo.distribution;

import junit.framework.TestCase;


public class DistributionTest extends TestCase {
	
	public void testNothing() {
		
	}
	
	public void NOtestDetection() {
		assertNotNull("Linux32",Distribution.detectFor(Version.V1_6_5));
		assertNotNull("Linux32",Distribution.detectFor(Version.V1_8_1));
		assertNotNull("Linux32",Distribution.detectFor(Version.V1_9_0));
	}
}
