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
package de.flapdoodle.embed.mongo;

import de.flapdoodle.embed.process.config.store.FileSet;
import de.flapdoodle.embed.process.config.store.FileType;
import de.flapdoodle.embed.process.config.store.IPackageResolver;
import de.flapdoodle.embed.process.distribution.*;

import java.util.logging.Logger;

/**
 *
 */
public class Paths implements IPackageResolver {

	private static Logger logger = Logger.getLogger(Paths.class.getName());
	private final Command command;

	public Paths(Command command) {
		this.command=command;
	}
	
	@Override
	public FileSet getFileSet(Distribution distribution) {
		String executableFileName;
		switch (distribution.getPlatform()) {
			case Linux:
			case OS_X:
			case Solaris:
			case FreeBSD:
				executableFileName = command.commandName();
				break;
			case Windows:
				executableFileName = command.commandName()+".exe";
				break;
			default:
				throw new IllegalArgumentException("Unknown Platform " + distribution.getPlatform());
		}
		return FileSet.builder().addEntry(FileType.Executable, executableFileName).build();
	}
	
	//CHECKSTYLE:OFF
	@Override
	public ArchiveType getArchiveType(Distribution distribution) {
		ArchiveType archiveType;
		switch (distribution.getPlatform()) {
			case Linux:
			case OS_X:
			case Solaris:
			case FreeBSD:
				archiveType = ArchiveType.TGZ;
				break;
			case Windows:
				archiveType = ArchiveType.ZIP;
				break;
			default:
				throw new IllegalArgumentException("Unknown Platform " + distribution.getPlatform());
		}
		return archiveType;
	}

	@Override
	public String getPath(Distribution distribution) {
		String sversion = getVersionPart(distribution.getVersion());

		ArchiveType archiveType = getArchiveType(distribution);
		String sarchiveType;
		switch (archiveType) {
			case TGZ:
				sarchiveType = "tgz";
				break;
			case ZIP:
				sarchiveType = "zip";
				break;
			default:
				throw new IllegalArgumentException("Unknown ArchiveType " + archiveType);
		}

		String splatform;
		switch (distribution.getPlatform()) {
			case Linux:
				splatform = "linux";
				break;
			case Windows:
				splatform = "win32";
				break;
			case OS_X:
				splatform = "osx";
				break;
			case Solaris:
				splatform = "sunos5";
				break;
			case FreeBSD:
				splatform = "freebsd";
				break;
			default:
				throw new IllegalArgumentException("Unknown Platform " + distribution.getPlatform());
		}

		String sbitSize;
		switch (distribution.getBitsize()) {
			case B32:
				switch (distribution.getPlatform()) {
					case Linux:
						sbitSize = "i686";
						break;
					case Windows:
						sbitSize = "i386";
						break;
					case OS_X:
						sbitSize = "i386";
						break;
					default:
						throw new IllegalArgumentException("Platform " + distribution.getPlatform() + " not supported yet on 32Bit Platform");
				}
				break;
			case B64:
				sbitSize = "x86_64";
				break;
			default:
				throw new IllegalArgumentException("Unknown BitSize " + distribution.getBitsize());
		}
		
		if ((distribution.getBitsize()==BitSize.B64) && (distribution.getPlatform()==Platform.Windows)) {
			if (useWindows2008PlusVersion()) {
				sversion="2008plus-"+sversion;
			}
		}

		return splatform + "/mongodb-" + splatform + "-" + sbitSize + "-" + sversion + "." + sarchiveType;
	}

	protected boolean useWindows2008PlusVersion() {
		// Windows Server 2008 R2  or Windows 7
		String osName = System.getProperty("os.name");
		if (osName.contains("Windows Server 2008 R2")) return true;
		return osName.contains("Windows 7");
	}

	protected static String getVersionPart(IVersion version) {
		return version.asInDownloadPath();
	}

}
