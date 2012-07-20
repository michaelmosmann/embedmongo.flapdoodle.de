package de.flapdoodle.embedmongo;

import de.flapdoodle.embedmongo.config.RuntimeConfig;
import de.flapdoodle.embedmongo.distribution.BitSize;
import de.flapdoodle.embedmongo.distribution.Distribution;
import de.flapdoodle.embedmongo.distribution.GenericVersion;
import de.flapdoodle.embedmongo.distribution.Platform;
import de.flapdoodle.embedmongo.extract.UUIDTempNaming;
import de.flapdoodle.embedmongo.output.IProgressListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: m.joehren
 * Date: 16.07.12
 * Time: 22:10
 * To change this template use File | Settings | File Templates.
 */
public class DownloaderTest {

	private static final int LISTEN_PORT = 17171;
	Server server = null;

	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();
	private RuntimeConfig rc;
	private IProgressListener pl;

	@Before
	public void setUp() throws Exception {
		// start the jetty container
		server = new Server(LISTEN_PORT);
		File myTmpDir = tempDir.newFolder();
		File osxDir = new File(myTmpDir, "osx");
		osxDir.mkdir();
		File tempFile = new File(osxDir, "mongodb-osx-x86_64-3.1.1.tgz");
		tempFile.createNewFile();
		ResourceHandler publicDocs = new ResourceHandler();
		publicDocs.setResourceBase(myTmpDir.getCanonicalPath());

		HandlerList hl = new HandlerList();
		hl.setHandlers(new Handler[]{publicDocs});
		server.setHandler(hl);

		server.start();

		while (true) {
			if (server.isRunning()) {
				break;
			}
			Thread.sleep(100);
		}
		rc = mock(RuntimeConfig.class);
		pl = mock(IProgressListener.class);
	}

	@After
	public void tearDown() throws Exception {
		server.stop();
	}

	//@Test
	public void testDownload() throws Exception {
		initRuntime();
		Distribution d = new Distribution(new GenericVersion("3.1.1"), Platform.detect(), BitSize.B64);
		File f = Downloader.download(rc, d);
	}

	private void initRuntime() {
		when(rc.getDefaultfileNaming()).thenReturn(new UUIDTempNaming());
		when(rc.getDownloadPath()).thenReturn("http://localhost:" + LISTEN_PORT + "/");
		when(rc.getProgressListener()).thenReturn(pl);
	}

	@Test(expected = Exception.class)
	public void testDownloadShouldThrowExceptionForUnknownVersion() throws Exception {
		initRuntime();
		Distribution d = new Distribution(new GenericVersion("3013.1.1"), Platform.detect(), BitSize.B64);
		File f = Downloader.download(rc, d);
	}
}
