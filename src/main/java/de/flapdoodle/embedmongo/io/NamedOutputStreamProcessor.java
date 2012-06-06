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
package de.flapdoodle.embedmongo.io;


public class NamedOutputStreamProcessor implements IStreamProcessor {


    private final IStreamProcessor _destination;
    private final String _name;

    public NamedOutputStreamProcessor(String name, IStreamProcessor destination) {
        _name = name;
        _destination = destination;
    }

    @Override
    public void process(String block) {
        _destination.process(block.replace("\n", "\n" + _name + " "));
//		int idx=block.indexOf('\n');
//		if (idx!=-1) {
//			_destination.process(block.substring(0,idx+1));
//			_destination.process(_name+" ");
//			_destination.process(block.substring(idx+1));
//		} else {
//			_destination.process(block);
//		}
    }

    @Override
    public void onProcessed() {
        _destination.onProcessed();

    }


}
