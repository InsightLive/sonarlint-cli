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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.sonar.runner.api.Issue;
import org.sonarlint.cli.util.Function;
import org.sonarlint.cli.util.Util;

public class ReportSummary {

  private final IssueVariation total = new IssueVariation();

  private final Map<IssueCategory, CategoryReport> reportByCategory = new LinkedHashMap<>();
  private final Map<String, IssueVariation> totalByRuleKey = new LinkedHashMap<>();
  private final Map<String, IssueVariation> totalBySeverity = new LinkedHashMap<>();

  ReportSummary() {
  }

  public void addIssue(Issue issue) {
    Severity severity = Severity.create(issue.getSeverity());
    IssueCategory category = new IssueCategory(issue.getRuleKey(), severity);

    IssueVariation byRuleKey = Util.getOrCreate(totalByRuleKey, issue.getRuleKey(), issueVariationFactory);
    CategoryReport byCategory = getOrCreate(reportByCategory, category);
    IssueVariation bySeverity = Util.getOrCreate(totalBySeverity, issue.getSeverity(), issueVariationFactory);

    total.incrementCountInCurrentAnalysis();
    byCategory.getTotal().incrementCountInCurrentAnalysis();
    byRuleKey.incrementCountInCurrentAnalysis();
    bySeverity.incrementCountInCurrentAnalysis();

    if (issue.isNew()) {
      total.incrementNewIssuesCount();
      byCategory.getTotal().incrementNewIssuesCount();
      byRuleKey.incrementNewIssuesCount();
      bySeverity.incrementNewIssuesCount();
    }

    if (issue.getResolution() != null) {
      total.incrementResolvedIssuesCount();
      byCategory.getTotal().incrementResolvedIssuesCount();
      byRuleKey.incrementResolvedIssuesCount();
      bySeverity.incrementResolvedIssuesCount();
    }
  }

  public IssueVariation getTotal() {
    return total;
  }

  public Map<String, IssueVariation> getTotalBySeverity() {
    return totalBySeverity;
  }

  public Map<String, IssueVariation> getTotalByRuleKey() {
    return totalByRuleKey;
  }

  private CategoryReport getOrCreate(Map<IssueCategory, CategoryReport> m, IssueCategory key) {
    CategoryReport report = m.get(key);
    if (report != null) {
      return report;
    }

    report = new CategoryReport(key);
    m.put(key, report);
    return report;
  }

  public List<CategoryReport> getCategoryReports() {
    List<CategoryReport> result = new ArrayList<>(reportByCategory.values());
    Collections.sort(result, new CategoryReportComparator());
    return result;
  }

  // waiting for Java 8..
  private static Function<IssueVariation> issueVariationFactory = new Function<IssueVariation>() {
    @Override
    public IssueVariation call() {
      return new IssueVariation();
    }
  };
}
