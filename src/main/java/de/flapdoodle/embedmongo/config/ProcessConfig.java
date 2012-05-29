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
package de.flapdoodle.embedmongo.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.flapdoodle.embedmongo.io.IBlockProcessor;

public class ProcessConfig {

	private final List<String> _commandLine;
	private final IBlockProcessor _output;
	private final IBlockProcessor _error;

	public ProcessConfig(List<String> commandLine, IBlockProcessor output, IBlockProcessor error) {
		_commandLine = new ArrayList<String>(commandLine);
		_output = output;
		_error = error;
	}

	public ProcessConfig(List<String> commandLine, IBlockProcessor output) {
		this(commandLine, output, null);
	}

	public List<String> getCommandLine() {
		return Collections.unmodifiableList(_commandLine);
	}

	public IBlockProcessor getOutput() {
		return _output;
	}

	public IBlockProcessor getError() {
		return _error;
	}
}
