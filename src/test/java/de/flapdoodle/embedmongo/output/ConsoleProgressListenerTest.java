package de.flapdoodle.embedmongo.output;

import junit.framework.TestCase;


public class ConsoleProgressListenerTest extends TestCase {
	public void testListener() {
		ConsoleProgressListener listener = new ConsoleProgressListener();
		listener.progress("Test", 0);
		listener.progress("Test", 10);
		listener.progress("Test", 10);
		listener.progress("Test", 100);
	}
}
