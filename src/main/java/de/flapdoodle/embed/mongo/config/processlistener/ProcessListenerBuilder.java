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
import java.util.List;

import de.flapdoodle.embed.process.builder.AbstractBuilder;


public class ProcessListenerBuilder extends AbstractBuilder<IMongoProcessListener> {

	List<IMongoProcessListener> _listener=new ArrayList<IMongoProcessListener>();
	
	public ProcessListenerBuilder add(IMongoProcessListener listener) {
		_listener.add(listener);
		return this;
	}
	
	public ProcessListenerBuilder copyDbFilesBeforeStopInto(File destination) {
		return add(new CopyDbFilesFromDirBeforeProcessStop(destination));
	}
	
	public ProcessListenerBuilder copyFilesIntoDbDirBeforeStarFrom(File source) {
		return add(new CopyDbFilesIntoDirBeforeProcessStart(source));
	}
	
	@Override
	public IMongoProcessListener build() {
		return ProcessListener.join(_listener);
	}
}
