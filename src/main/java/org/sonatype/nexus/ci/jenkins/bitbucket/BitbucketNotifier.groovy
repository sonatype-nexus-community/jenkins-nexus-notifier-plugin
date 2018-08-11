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

import javax.annotation.Nonnull

import org.sonatype.nexus.ci.jenkins.bitbucket.PolicyEvaluationResult.BuildStatus
import org.sonatype.nexus.ci.jenkins.model.PolicyEvaluationHealthAction
import org.sonatype.nexus.ci.jenkins.notifier.BitbucketNotification

import hudson.AbortException
import hudson.model.Run
import hudson.model.TaskListener

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Strings.isNullOrEmpty

class BitbucketNotifier
{
  final Run run
  final TaskListener listener
  final PrintStream logger

  BitbucketNotifier(@Nonnull final Run run, @Nonnull final TaskListener listener) {
    this.run = run
    this.listener = listener
    this.logger = listener.logger
  }

  void send(final boolean buildPassing,
            final BitbucketNotification bitbucketNotification,
            final PolicyEvaluationHealthAction policyEvaluationHealthAction)
  {
    checkArgument(!isNullOrEmpty(bitbucketNotification.projectKey), Messages.BitbucketNotifier_NoProjectKey())
    checkArgument(!isNullOrEmpty(bitbucketNotification.repositorySlug), Messages.BitbucketNotifier_NoRepositorySlug())
    checkArgument(!isNullOrEmpty(bitbucketNotification.commitHash), Messages.BitbucketNotifier_NoCommitHash())

    def client = BitbucketClientFactory.getBitbucketClient(bitbucketNotification.jobCredentialsId)

    def envVars = run.getEnvironment(listener)
    def projectKey = envVars.expand(bitbucketNotification.projectKey)
    def repositorySlug = envVars.expand(bitbucketNotification.repositorySlug)
    def commitHash = envVars.expand(bitbucketNotification.commitHash)

    sendPolicyEvaluationHealthAction(client, projectKey, repositorySlug, commitHash, buildPassing,
        PolicyEvaluationHealthAction.build(policyEvaluationHealthAction))
  }

  private void sendPolicyEvaluationHealthAction(final BitbucketClient bitbucketClient,
                                                final String projectKey,
                                                final String repositorySlug,
                                                final String commitHash,
                                                final boolean buildPassing,
                                                final PolicyEvaluationHealthAction policyEvaluationHealthAction)
  {
    try {
      bitbucketClient.putCard(new PolicyEvaluationResult(
          projectKey,
          repositorySlug,
          commitHash,
          buildPassing ? BuildStatus.PASS : BuildStatus.FAIL,
          policyEvaluationHealthAction.affectedComponentCount,
          policyEvaluationHealthAction.criticalComponentCount,
          policyEvaluationHealthAction.severeComponentCount,
          policyEvaluationHealthAction.moderateComponentCount,
          policyEvaluationHealthAction.reportLink
      ))
    } catch (ex) {
      logger.println(ex.message)
      throw new AbortException(ex.message)
    }
  }
}
