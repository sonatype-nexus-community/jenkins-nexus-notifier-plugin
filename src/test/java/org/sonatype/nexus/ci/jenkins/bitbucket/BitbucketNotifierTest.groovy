/*
 * Copyright (c) 2018-present Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.nexus.ci.jenkins.bitbucket

import org.sonatype.nexus.ci.jenkins.bitbucket.PolicyEvaluationResult.BuildStatus
import org.sonatype.nexus.ci.jenkins.model.PolicyEvaluationHealthAction
import org.sonatype.nexus.ci.jenkins.notifier.BitbucketNotification

import hudson.model.Run
import hudson.model.TaskListener
import spock.lang.Specification

class BitbucketNotifierTest
    extends Specification
{
  def mockLogger = Mock(PrintStream)
  def mockListener = Mock(TaskListener)
  def mockRun = Mock(Run)

  BitbucketNotifier bitbucketNotifier

  def setup() {
    mockListener.getLogger() >> mockLogger
    mockRun.getEnvironment(_) >> [:]
    bitbucketNotifier = new BitbucketNotifier(mockRun, mockListener)
  }

  def 'send requires projectKey'() {
    when:
      bitbucketNotifier.send(true, new BitbucketNotification(true, emptyOptions, 'slug', 'commit', null), null)

    then:
      IllegalArgumentException ex = thrown()
      ex.message == 'Bitbucket Project Key is a required argument for the Bitbucket Notifier'

    where:
      emptyOptions << [ null, '' ]
  }

  def 'send requires repository slug'() {
    when:
      bitbucketNotifier.send(true, new BitbucketNotification(true, 'prokectKey', emptyOptions, 'commit', null), null)

    then:
      IllegalArgumentException ex = thrown()
      ex.message == 'Bitbucket Repository Slug is a required argument for the Bitbucket Notifier'

    where:
      emptyOptions << [ null, '' ]
  }

  def 'send requires commit hash'() {
    when:
      bitbucketNotifier.send(true, new BitbucketNotification(true, 'projectKey', 'slug', emptyOptions, null), null)

    then:
      IllegalArgumentException ex = thrown()
      ex.message == 'Bitbucket Commit Hash is a required argument for the Bitbucket Notifier'

    where:
      emptyOptions << [ null, '' ]
  }

  def 'creates Bitbucket client with job credentials override'() {
    setup:
      GroovyMock(BitbucketClientFactory.class, global: true)
      def client = Mock(BitbucketClient.class)

    when:
      bitbucketNotifier.send(true, new BitbucketNotification(true, 'projectKey', 'slug', 'commit', 'overrideId'),
          Mock(PolicyEvaluationHealthAction))

    then:
      1 * BitbucketClientFactory.getBitbucketClient('overrideId') >> client
  }

  def 'send expands notification arguments'() {
    setup:
      GroovyMock(BitbucketClientFactory.class, global: true)
      def client = Mock(BitbucketClient.class)
      BitbucketClientFactory.getBitbucketClient() >> client

    when:
      mockRun.getEnvironment(_) >> ['projectKey': 'project', 'repositorySlug': 'repo', 'commitHash': 'abcdefg']
      bitbucketNotifier = new BitbucketNotifier(mockRun, mockListener)
      bitbucketNotifier.send(true, new BitbucketNotification(true, '${projectKey}', '${repositorySlug}',
          '${commitHash}'), Mock(PolicyEvaluationHealthAction))

    then:
      1 * client.putCard(_) >> { arugments ->
        def policyEvaluationResult = arugments[0] as PolicyEvaluationResult
        assert policyEvaluationResult.projectKey == 'project'
        assert policyEvaluationResult.repositorySlug == 'repo'
        assert policyEvaluationResult.commitHash == 'abcdefg'
      }
  }

  def 'putsCard to Bitbucket client'() {
    setup:
      def policyEvaluationHealthAction = new PolicyEvaluationHealthAction(
          reportLink: 'http://report.com/link',
          affectedComponentCount: 1,
          criticalComponentCount: 2,
          severeComponentCount: 3,
          moderateComponentCount: 5
        )
      def bitbucketNotification = new BitbucketNotification(true, 'projectKey', 'repositorySlug', 'commitHash', null)
      GroovyMock(BitbucketClientFactory.class, global: true)
      def client = Mock(BitbucketClient.class)
      BitbucketClientFactory.getBitbucketClient(_) >> client

    when:
      bitbucketNotifier.send(buildPassing, bitbucketNotification, policyEvaluationHealthAction)

    then:
      1 * client.putCard(_) >> { arugments ->
        def policyEvaluationResult = arugments[0] as PolicyEvaluationResult
        assert policyEvaluationResult.projectKey == bitbucketNotification.projectKey
        assert policyEvaluationResult.repositorySlug == bitbucketNotification.repositorySlug
        assert policyEvaluationResult.commitHash == bitbucketNotification.commitHash
        assert policyEvaluationResult.buildStatus == (buildPassing ? BuildStatus.PASS : BuildStatus.FAIL)
        assert policyEvaluationResult.componentsAffected == policyEvaluationHealthAction.affectedComponentCount
        assert policyEvaluationResult.critical == policyEvaluationHealthAction.criticalComponentCount
        assert policyEvaluationResult.severe == policyEvaluationHealthAction.severeComponentCount
        assert policyEvaluationResult.moderate == policyEvaluationHealthAction.moderateComponentCount
        assert policyEvaluationResult.reportUrl == policyEvaluationHealthAction.reportLink
      }

    where:
      buildPassing << [true, false ]
  }
}
