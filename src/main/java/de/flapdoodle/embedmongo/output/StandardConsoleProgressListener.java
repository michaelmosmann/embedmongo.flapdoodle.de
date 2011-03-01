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

package de.flapdoodle.embedmongo.output;

public class StandardConsoleProgressListener implements IProgressListener {

	String _lastLabel = null;
	int _lastPercent = -1;
//	int _countEquals = 0;

	@Override
	public void progress(String label, int percent) {
		if (!label.equals(_lastLabel)) {
			System.out.print(label);
			System.out.print(" ");
		}
		if (percent == _lastPercent) {
//			_countEquals++;
//			if (_countEquals >= 10) {
//				System.out.print(".");
//				_countEquals = 0;
//			}
		} else {
//			_countEquals=0;
			System.out.print(percent);
			System.out.print("% ");
		}
		_lastLabel = label;
		_lastPercent = percent;
	}

	@Override
	public void done(String label) {
		System.out.println(label + " DONE");
	}
	
	@Override
	public void start(String label) {
		System.out.println(label + " START");		
	}
	
	@Override
	public void info(String label, String message) {
		System.out.println(label + " "+message);
	}
}
