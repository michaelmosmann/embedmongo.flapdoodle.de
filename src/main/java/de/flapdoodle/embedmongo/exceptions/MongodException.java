/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
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
package de.flapdoodle.embedmongo.exceptions;

import de.flapdoodle.embedmongo.distribution.Distribution;

public class MongodException extends RuntimeException {

	private final Distribution _distribution;

	public MongodException(Distribution distribution) {
		super();
		_distribution = distribution;
	}

	public MongodException(String message, Distribution distribution, Throwable cause) {
		super(message, cause);
		_distribution = distribution;
	}

	public MongodException(String message, Distribution distribution) {
		super(message);
		_distribution = distribution;
	}

	public MongodException(Distribution distribution, Throwable cause) {
		super(cause);
		_distribution = distribution;
	}
	
	
	public Distribution withDistribution() {
		return _distribution;
	}
}
