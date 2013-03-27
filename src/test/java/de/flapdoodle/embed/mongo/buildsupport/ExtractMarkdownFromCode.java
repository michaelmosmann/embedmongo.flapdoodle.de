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
package de.flapdoodle.embed.mongo.buildsupport;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class ExtractMarkdownFromCode {

	public static void main(String[] args) {
		String sourceFileName = args[0];
		String destFileName = args[1];

		try {
			String markDown = extractMarkdown(sourceFileName);
			Files.write(markDown, new File(destFileName), Charsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("could not extract markdown", e);
		}
	}

	private static String extractMarkdown(String sourceFileName) throws IOException {
		List<String> lines = Files.readLines(new File(sourceFileName), Charsets.UTF_8);
		Collection<String> result = compactEmptyLines(surroundHeadlineWithEmptyLines(uncommentThreeDots(shiftOneTabLeft(stripNonCodeLines(freeHeader(startWithFirstHeader(resolveIncludes(
				sourceFileName, lines))))))));
		StringBuilder sb = new StringBuilder();
		for (String line : result) {
			sb.append(line);
			sb.append("\n");
		}

		return sb.toString();
	}

	private static Collection<String> resolveIncludes(String sourceFileName, Collection<String> lines) throws IOException {
		List<String> ret = Lists.newArrayList();
		String includeTag = "// @include";

		for (String line : lines) {
			int includeDefIdx = line.indexOf(includeTag);
			if (includeDefIdx != -1) {
				String fileName = line.substring(includeDefIdx + includeTag.length()).trim();
				String basePath = sourceFileName.substring(0, sourceFileName.lastIndexOf('/') + 1);
				String includeFilename = basePath + fileName;
				List<String> includedLines = Files.readLines(new File(includeFilename), Charsets.UTF_8);
				Collection<String> shiftedLines = Collections2.transform(includedLines, new Function<String, String>() {

					@Override
					public String apply(String input) {
						return "\t\t" + input;
					}
				});
				ret.addAll(shiftedLines);
			} else {
				ret.add(line);
			}
		}
		return ret;
	}

	private static Collection<String> surroundHeadlineWithEmptyLines(Collection<String> lines) {
		List<String> ret=Lists.newArrayList();
		for (String line : lines) {
			if (isHeader(line)) {
				ret.add("");
				ret.add(line);
				ret.add("");
			} else {
				ret.add(line);
			}
		}
		return ret;
	}
	
	private static Collection<String> uncommentThreeDots(Collection<String> lines) {
		return Collections2.transform(lines, new Function<String, String>() {

			@Override
			public String apply(String input) {
				return input.replace("// ...", "...");
			}
		});
	}
	
	private static Collection<String> compactEmptyLines(Collection<String> lines) {
		return Collections2.filter(Collections2.transform(lines, new Function<String, String>() {

			@Override
			public String apply(String input) {
				if (input.trim().isEmpty()) {
					return "";
				}
				return input;
			}
		}),new Predicate<String>() {
			boolean lastOneWasEmpty=false;
			@Override
			public boolean apply(String input) {
				boolean addThisLine=true;
				
				if (input.isEmpty()) {
					if (lastOneWasEmpty) addThisLine=false;
					lastOneWasEmpty=true;
				} else {
					lastOneWasEmpty=false;
				}
				return addThisLine;
			}
		});
	}

	private static Collection<String> shiftOneTabLeft(Collection<String> lines) {
		return Collections2.transform(lines, new Function<String, String>() {

			@Override
			public String apply(String input) {
				if ((!input.isEmpty()) && (input.charAt(0) == '\t'))
					return input.substring(1);
				return input;
			}
		});
	}

	private static Collection<String> stripNonCodeLines(Collection<String> lines) {
		return Collections2.filter(lines, new Predicate<String>() {

			boolean codeStarted = false;

			@Override
			public boolean apply(String input) {
				boolean includeCurrentLine = true;
				if (!codeStarted) {
					if (input.contains("// ->")) {
						codeStarted = true;
						includeCurrentLine = false;
					}
				} else {
					if (input.contains("// <-")) {
						codeStarted = false;
						includeCurrentLine = false;
					}
				}
				return (codeStarted && includeCurrentLine) | isHeader(input);
			}
		});
	}

	private static Collection<String> freeHeader(Collection<String> lines) {
		return Collections2.transform(lines, new Function<String, String>() {

			@Override
			public String apply(String input) {
				return input.replace("// ###", "###");
			}
		});
	}

	private static Collection<String> startWithFirstHeader(Collection<String> lines) {
		Collection<String> withFirstHeader = Collections2.filter(lines, new Predicate<String>() {

			boolean hit = false;

			@Override
			public boolean apply(String input) {
				hit = hit | isHeader(input);
				return hit;
			}

		});
		return withFirstHeader;
	}

	private static boolean isHeader(String input) {
		return input.contains("###");
	}
}
