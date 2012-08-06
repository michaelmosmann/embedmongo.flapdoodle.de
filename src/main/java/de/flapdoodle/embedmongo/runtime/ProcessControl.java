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
package de.flapdoodle.embedmongo.runtime;

import de.flapdoodle.embedmongo.collections.Collections;
import de.flapdoodle.embedmongo.config.ProcessConfig;
import de.flapdoodle.embedmongo.distribution.Platform;
import de.flapdoodle.embedmongo.io.IStreamProcessor;
import de.flapdoodle.embedmongo.io.Processors;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class ProcessControl {

	private static Logger logger = Logger.getLogger(ProcessControl.class.getName());
	public static final int SLEEPT_TIMEOUT = 10;

	private Process process;

	private InputStreamReader reader;
	private InputStreamReader error;

	private Integer pid;

	public ProcessControl(Process process) {
		this.process = process;
		reader = new InputStreamReader(this.process.getInputStream());
		error = new InputStreamReader(this.process.getErrorStream());
		pid = getProcessID();
	}

	public Reader getReader() {
		return reader;
	}

	public InputStreamReader getError() {
		return error;
	}

	public int stop() {
		closeIOAndDestroy();
		return waitForProcessGotKilled();
	}

	private void closeIOAndDestroy() {
		if (process != null) {
			try {
				// streams need to be closed, otherwise process may block
				// see http://kylecartmell.com/?p=9
				process.getErrorStream().close();
				process.getInputStream().close();
				process.getOutputStream().close();

			} catch (IOException e) {
				logger.severe(e.getMessage());
			} finally {
				process.destroy();
			}
			reader = null;
		}
	}

	//CHECKSTYLE:OFF

	/**
	 * It may happen in tests, that the process is currently using some files in
	 * the temp directory, e.g. journal files (journal/j._0) and got killed at
	 * that time, so it takes a bit longer to kill the process. So we just wait
	 * for a second (in 10 ms steps) that the process got really killed.
	 */
	private int waitForProcessGotKilled() {
		final ProcessState state = new ProcessState();

		final Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			public void run() {
				try {
					state.returnCode = process.waitFor();
				} catch (InterruptedException e) {
					logger.severe(e.getMessage());
				} finally {
					state.setKilled(true);
					timer.cancel();
				}
			}
		}, 0, 10);
		// wait for max. 1 second that process got killed

		int countDown = 100;
		while (!state.isKilled() && (countDown-- > 0))
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				logger.severe(e.getMessage());
			}
		if (!state.isKilled()) {
			timer.cancel();
			String message = "\n\n" + "----------------------------------------------------\n"
					+ "Something bad happend. We couldn't kill mongod process, and tried a lot.\n"
					+ "If you want this problem solved you can help us if you open a new issue on github.\n" + "\n"
					+ "Follow this link:\n" + "https://github.com/flapdoodle-oss/embedmongo.flapdoodle.de/issues\n" +
					"\n"
					+ "Thank you:)\n" + "----------------------------------------------------\n\n";
			throw new IllegalStateException("Couldn't kill mongod process!" + message);
		}
		return state.returnCode;
	}

	//CHECKSTYLE:ON
	public static ProcessControl fromCommandLine(List<String> commandLine, boolean redirectErrorStream)
			throws IOException {
		ProcessBuilder processBuilder = new ProcessBuilder(commandLine);
		if (redirectErrorStream)
			processBuilder.redirectErrorStream();
		return new ProcessControl(processBuilder.start());
	}

	public static boolean executeCommandLine(String label, ProcessConfig processConfig) {
		boolean ret = false;

		List<String> commandLine = processConfig.getCommandLine();
		try {
			ProcessControl process = fromCommandLine(processConfig.getCommandLine(), processConfig.getError() == null);
			Processors.connect(process.getReader(), processConfig.getOutput());
			Thread.sleep(SLEEPT_TIMEOUT);
			ret = process.stop() == 0;
			logger.info("execSuccess: " + ret + " " + commandLine);
			return ret;
		} catch (IOException e) {
			logger.log(Level.SEVERE, "" + commandLine, e);
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "" + commandLine, e);
		}
		return false;
	}

	public static boolean killProcess(Platform platform, IStreamProcessor output, int pid) {
		if ((platform == Platform.Linux) || (platform == Platform.OS_X)) {
			return executeCommandLine("[kill process]",
					new ProcessConfig(Collections.newArrayList("kill", "-2", "" + pid), output));
		}
		return false;
	}

	public static boolean tryKillProcess(Platform platform, IStreamProcessor output, int pid) {
		if (platform == Platform.Windows) {
			return executeCommandLine("[taskkill process]",
					new ProcessConfig(Collections.newArrayList("taskkill", "/F", "/pid", "" + pid), output));
		}
		return false;
	}

	private Integer getProcessID() {
		Class<?> clazz = process.getClass();
		try {
			if (clazz.getName().equals("java.lang.UNIXProcess")) {
				Field pidField = clazz.getDeclaredField("pid");
				pidField.setAccessible(true);
				Object value = pidField.get(process);
				if (value instanceof Integer) {
					return (Integer) value;
				}
			}
		} catch (SecurityException sx) {
			sx.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 *
	 */
	static class ProcessState {

		protected int returnCode;

		public boolean isKilled() {
			return killed;
		}

		public void setKilled(boolean killed) {
			this.killed = killed;
		}

		private boolean killed = false;
	}
}
