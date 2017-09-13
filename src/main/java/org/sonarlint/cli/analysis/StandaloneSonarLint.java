/*
 * SonarLint CLI
 * Copyright (C) 2016-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarlint.cli.analysis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import org.codehaus.jackson.map.ObjectMapper;
import org.sonarlint.cli.report.ReportFactory;
import org.sonarsource.sonarlint.core.client.api.common.RuleDetails;
import org.sonarsource.sonarlint.core.client.api.common.analysis.AnalysisResults;
import org.sonarsource.sonarlint.core.client.api.common.analysis.ClientInputFile;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneAnalysisConfiguration;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneSonarLintEngine;
import org.sonarsource.sonarlint.core.tracking.IssueTrackable;
import org.sonarsource.sonarlint.core.tracking.Trackable;
import org.sonarsource.sonarlint.core.client.api.common.analysis.Issue;

public class StandaloneSonarLint extends SonarLint {
  private final StandaloneSonarLintEngine engine;

  public StandaloneSonarLint(StandaloneSonarLintEngine engine) {
    this.engine = engine;
  }

  @Override
  protected void doAnalysis(Map<String, String> properties, ReportFactory reportFactory, List<ClientInputFile> inputFiles, Path baseDirPath) throws IOException{
    Date start = new Date();

    IssueCollector collector = new IssueCollector();
    StandaloneAnalysisConfiguration config = new StandaloneAnalysisConfiguration(baseDirPath, baseDirPath.resolve(".sonarlint"),
      inputFiles, properties);
    AnalysisResults result = engine.analyze(config, collector);
    Collection<Trackable> trackables = collector.get().stream().map(IssueTrackable::new).collect(Collectors.toList());
    //ToDo
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    //Added code to write Issue object to json file.
    List<Issue> src = collector.get();
    List<Violation> violations = new ArrayList<Violation>();
    for (Issue issue : src) {

      String path;
      if(issue.getInputFile() == null){
        continue;
      }

      path = issue.getInputFile().getPath();

      Violation vio = new Violation();
      vio.setEndLine(issue.getEndLine());
      vio.setFilePath(path);
      vio.setMessage(issue.getMessage());
      vio.setStartLine(issue.getStartLine());
      vio.setRuleKey(issue.getRuleKey());
      vio.setRuleName(issue.getRuleName());
      vio.setSeverity(issue.getSeverity());
      vio.setEndLineOffset(issue.getEndLineOffset());
      vio.setStartLineOffset(issue.getStartLineOffset());

      violations.add(vio);
    }

    ViolationWrapper violationWrapper = new ViolationWrapper();
    violationWrapper.setViolations(violations);

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.writeValue(new File(properties.get("outputDir")), violations);
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
   // generateReports(trackables, result, reportFactory, baseDirPath.getFileName().toString(), baseDirPath, start);
  }

  @Override
  protected RuleDetails getRuleDetails(String ruleKey) {
    return engine.getRuleDetails(ruleKey);
  }

  @Override
  public void stop() {
    engine.stop();
  }
}
