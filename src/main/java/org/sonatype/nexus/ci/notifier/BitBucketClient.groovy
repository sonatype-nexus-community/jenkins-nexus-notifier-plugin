package org.sonatype.nexus.ci.notifier

import org.sonatype.nexus.ci.notifier.PolicyEvaluationResult.BuildStatus

import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method

import static groovyx.net.http.ContentType.JSON

class BitBucketClient
{
  static String VENDOR_LINK = 'https://www.sonatype.com'

  static String USER_AGENT = 'nexus-jenkins-notifier'

  static String LOGO_URL = 'https://avatars0.githubusercontent.com/u/44938?s=200&v=4'

  String serverUrl

  String username

  String password

  BitBucketClient(String serverUrl, String username, String password) {
    this.serverUrl = serverUrl
    this.username = username
    this.password = password
  }

  def putCard(PolicyEvaluationResult result) {
    putCard(result.projectKey, result.repositorySlug, result.commitHash, result.buildStatus, result.componentsAffected,
        result.critical, result.severe, result.moderate, result.reportUrl)
  }

  def putCard(projectKey, repositorySlug, commitHash, buildStatus, componentsAffected, critical, severe, moderate,
              reportUrl)
  {
    def http = new HTTPBuilder(
        "http://${serverUrl}/rest/insights/1.0/projects/${projectKey}/repos/${repositorySlug}/commits/" +
            "${commitHash}/cards/NEXUS")
    return http.request(Method.PUT, JSON) {
      req ->
        body = [
            data       : [
                [
                    title: 'Components Affected',
                    value: componentsAffected
                ],
                [
                    title: 'Critical',
                    value: critical
                ],
                [
                    title: 'Severe',
                    value: severe
                ],
                [
                    title: 'Moderate',
                    value: moderate
                ],
                [
                    title: 'View full report',
                    type : 'LINK',
                    value: [
                        linktext: 'Report',
                        href    : reportUrl
                    ]
                ]
            ],
            details    : buildStatus == BuildStatus.FAIL ? 'Nexus Platform Plugin found policy violations.' : 'Success!',
            title      : buildStatus == BuildStatus.FAIL ? 'Policy Violations Found' : 'No Policy Violations Found',
            vendor     : 'Nexus Jenkins Notifier',
            createdDate: System.currentTimeMillis(),
            link       : VENDOR_LINK,
            logoUrl    : LOGO_URL,
            result     : buildStatus
        ]

        headers = [
            'User-Agent' : USER_AGENT,
            Authorization: 'Basic ' + ("${username}:${password}").bytes.encodeBase64()
        ]
    }
  }
}
