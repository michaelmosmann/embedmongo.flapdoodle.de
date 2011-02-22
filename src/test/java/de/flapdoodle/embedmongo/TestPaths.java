package de.flapdoodle.embedmongo;

import junit.framework.TestCase;

import de.flapdoodle.embedmongo.distribution.BitSize;
import de.flapdoodle.embedmongo.distribution.Distribution;
import de.flapdoodle.embedmongo.distribution.Platform;
import de.flapdoodle.embedmongo.distribution.Version;


public class TestPaths extends TestCase {

	public void testPaths() {
		checkPath(new Distribution(Version.V1_6_5, Platform.Windows, BitSize.B32), "win32/mongodb-win32-i386-1.6.5.zip");
		checkPath(new Distribution(Version.V1_6_5, Platform.Windows, BitSize.B64), "win32/mongodb-win32-x86_64-1.6.5.zip");
		checkPath(new Distribution(Version.V1_7_6, Platform.Linux, BitSize.B32), "linux/mongodb-linux-i686-1.7.6.tgz");
		checkPath(new Distribution(Version.V1_7_6, Platform.Linux, BitSize.B64), "linux/mongodb-linux-x86_64-1.7.6.tgz");
	}

	private void checkPath(Distribution distribution, String match) {
		assertEquals(""+distribution, match,Paths.getPath(distribution));
	}

}
