/*
 * Copyright (c) 2018-present Sonatype, Inc. All rights reserved.
 * Includes the third-party code listed at http://links.sonatype.com/products/nexus/attributions.
 * "Sonatype" is a trademark of Sonatype, Inc.
 */
@Library(['ci-pipeline-library', 'jenkins-shared']) _

String apiToken = null
withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'integrations-github-api',
                  usernameVariable: 'GITHUB_API_USERNAME', passwordVariable: 'GITHUB_API_PASSWORD']]) {
  apiToken = env.GITHUB_API_PASSWORD
}

GitHub gitHub = new GitHub(this, 'sonatype/jenkins-nexus-notifier-plugin', apiToken)
Closure postHandler = {
  currentBuild, env ->
    def commitId = OsTools.runSafe(this, 'git rev-parse HEAD')
    if (currentBuild.currentResult == 'SUCCESS') {
      gitHub.statusUpdate commitId, 'success', 'CI', 'CI Passed'
    }
    else {
      gitHub.statusUpdate commitId, 'failure', 'CI', 'CI Failed'
    }
}

Closure iqPolicyEvaluation = {
  stage ->
    def commitId = OsTools.runSafe(this, 'git rev-parse HEAD')
    gitHub.statusUpdate commitId, 'pending', 'analysis', 'Nexus Lifecycle Analysis Running'

    try {
      def evaluation = nexusPolicyEvaluation failBuildOnNetworkError: false,
          iqApplication: 'nexus-jenkins-notifier-plugin',
          iqScanPatterns: [[scanPattern: 'nexus-jenkins-notifier-plugin.hpi']],
          iqStage: 'build',
          jobCredentialsId: ''
      gitHub.statusUpdate commitId, 'success', 'analysis', 'Nexus Lifecycle Analysis Succeeded', "${evaluation.applicationCompositionReportUrl}"
    }
    catch (error) {
      def evaluation = error.policyEvaluation
      gitHub.statusUpdate commitId, 'failure', 'analysis', 'Nexus Lifecycle Analysis Failed', "${evaluation.applicationCompositionReportUrl}"
      throw error
    }
}

mavenSnapshotPipeline(
    notificationSender: postHandler,
    iqPolicyEvaluation: iqPolicyEvaluation
)
