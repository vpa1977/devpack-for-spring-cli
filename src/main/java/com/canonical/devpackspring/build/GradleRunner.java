/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.canonical.devpackspring.build;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import com.canonical.devpackspring.build.gradle.GradleAdapter;
import com.canonical.devpackspring.build.gradle.TempProjectAdapter;
import com.canonical.devpackspring.rewrite.AddPluginRefactoring;
import org.jline.utils.AttributedStyle;

import org.springframework.cli.util.TerminalMessage;

public abstract class GradleRunner {

	private final AddPluginRefactoring refactoring = new AddPluginRefactoring();

	public static boolean run(Path baseDir, PluginDescriptor desc, String task, TerminalMessage message)
			throws IOException {
		OutputStream terminalStreamError = new TerminalOutputStream(message,
				new AttributedStyle().foreground(AttributedStyle.RED));

		TempProjectAdapter projectAdapter = new TempProjectAdapter(baseDir);
		GradleAdapter adapter = new GradleAdapter(message, projectAdapter.getProjectPath());
		appendPlugin(baseDir, projectAdapter.getProjectPath(), desc);

		if (task == null) {
			task = desc.defaultTask();
		}

		try {
			adapter.run(task);
			return true;
		}
		catch (RuntimeException ex) {
			try (PrintStream ps = new PrintStream(terminalStreamError)) {
				// skip runtime exception itself - it only prints
				// that task execution failed
				ps.print(ex.getCause().getMessage());
			}
			return false;
		}
		catch (InterruptedException ex) {
			try (PrintStream ps = new PrintStream(terminalStreamError)) {
				ps.print(ex.getMessage());
			}
			return false;
		}
	}

	private static void appendPlugin(Path sourceProject, Path targetProject, PluginDescriptor desc) throws IOException {
		if (Files.exists(sourceProject.resolve("build.gradle"))) {
			Files.copy(sourceProject.resolve("build.gradle"), targetProject.resolve("build.gradle"),
					StandardCopyOption.REPLACE_EXISTING);
		}
		else if (Files.exists(sourceProject.resolve("build.gradle.kts"))) {
			Files.copy(sourceProject.resolve("build.gradle.kts"), targetProject.resolve("build.gradle.kts"),
					StandardCopyOption.REPLACE_EXISTING);
		}
		else {
			throw new RuntimeException(
					String.format("Neither build.gradle nor build.gradle.kts were found in %s", sourceProject));
		}

	}

}
