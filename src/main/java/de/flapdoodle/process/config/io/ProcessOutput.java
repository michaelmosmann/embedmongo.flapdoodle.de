/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
 *
 * with contributions from
 * 	konstantin-ba@github,Archimedes Trajano (trajano@github)
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
package de.flapdoodle.process.config.io;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.flapdoodle.process.io.IStreamProcessor;
import de.flapdoodle.process.io.Processors;


public class ProcessOutput {

	protected final IStreamProcessor mongodOutput;
	protected final IStreamProcessor mongodError;
	protected final IStreamProcessor commandsOutput;

	public ProcessOutput(IStreamProcessor mongodOutput, IStreamProcessor mongodError,
			IStreamProcessor commandsOutput) {
		this.mongodOutput = mongodOutput;
		this.mongodError = mongodError;
		this.commandsOutput = commandsOutput;
	}
	
	public IStreamProcessor getMongodOutput() {
		return mongodOutput;
	}

	public IStreamProcessor getMongodError() {
		return mongodError;
	}

	public IStreamProcessor getCommandsOutput() {
		return commandsOutput;
	}

	public static ProcessOutput getDefaultInstance(String label) {
		return new ProcessOutput(Processors.namedConsole("["+label+" output]"),
				Processors.namedConsole("["+label+" error]"), Processors.console());
	}

	public static ProcessOutput getInstance(String label, Logger logger) {
		return new ProcessOutput(Processors.named("["+label+" output]", Processors.logTo(logger, Level.INFO)),
				Processors.named("["+label+" error]", Processors.logTo(logger, Level.SEVERE)),
				Processors.logTo(logger, Level.FINE));
	}

}
