/*
 * SonarLint CLI
 * Copyright (C) 2016 SonarSource
 * sonarqube@googlegroups.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonarlint.cli.report;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonar.runner.api.Issue;

import java.nio.file.Path;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.mock;

public class HtmlReportTest {
  private HtmlReport html;

  @Rule
  public TemporaryFolder temp = new TemporaryFolder();
  private Path reportFile;
  private SourceProvider sources;

  @Before
  public void setUp() {
    reportFile = temp.getRoot().toPath().resolve("report.html");
    sources = mock(SourceProvider.class);
    html = new HtmlReport(reportFile, sources);
  }

  @Test
  public void testHtml() {
    html.execute("project", new Date(), createTestIssues());
  }

  private static List<Issue> createTestIssues() {
    List<Issue> issues = new LinkedList<>();

    return issues;
  }
}
