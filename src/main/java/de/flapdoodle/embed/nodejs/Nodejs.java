package de.flapdoodle.embed.nodejs;

import java.io.IOException;

import de.flapdoodle.process.distribution.IVersion;

public class Nodejs {

	private Nodejs() {
		// singleton
	}

	public static void call(IVersion version, String filename, String workingDirectory) throws IOException {
		NodejsProcess node = null;
		NodejsConfig nodejsConfig = new NodejsConfig(version, filename, workingDirectory);

		NodejsStarter runtime = new NodejsStarter(new NodejsRuntimeConfig());

		try {
			NodejsExecutable nodeExecutable = runtime.prepare(nodejsConfig);
			node = nodeExecutable.start();
		} finally {
			if (node != null)
				node.stop();
		}
	}
}
