package org.sonatype.nexus.ci.notifier

class PolicyEvaluationResult
{
  enum BuildStatus {
    PASS, FAIL
  }

  String projectKey

  String repositorySlug

  String commitHash

  BuildStatus buildStatus

  int componentsAffected

  int critical

  int severe

  int moderate

  String reportUrl

  PolicyEvaluationResult(String projectKey, String repositorySlug, String commitHash, BuildStatus buildStatus,
                         int componentsAffected, int critical, int severe, int moderate, String reportUrl)
  {
    this.projectKey = projectKey
    this.repositorySlug = repositorySlug
    this.commitHash = commitHash
    this.buildStatus = buildStatus
    this.componentsAffected = componentsAffected
    this.critical = critical
    this.severe = severe
    this.moderate = moderate
    this.reportUrl = reportUrl
  }
}
