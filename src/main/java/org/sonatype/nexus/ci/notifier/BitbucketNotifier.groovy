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
package org.sonatype.nexus.ci.notifier

import javax.annotation.Nonnull

import org.sonatype.nexus.ci.model.ApplicationPolicyEvaluation
import org.sonatype.nexus.ci.model.PolicyEvaluationHealthAction
import org.sonatype.nexus.ci.notifier.PolicyEvaluationResult.BuildStatus

import hudson.AbortException
import hudson.model.Run
import hudson.model.TaskListener

import static com.google.common.base.Preconditions.checkArgument

class BitbucketNotifier
{
  final PrintStream logger

  BitbucketNotifier(@Nonnull final TaskListener listener) {
    this.logger = listener.logger
  }

  void send(@Nonnull final Run run,
            final String projectKey,
            final String repositorySlug,
            final String commitHash,
            final Object applicationPolicyEvaluation)
  {
    checkArgument(projectKey != null, Messages.BitbucketNotifier_NoProjectKey())
    checkArgument(repositorySlug != null, Messages.BitbucketNotifier_NoRepositorySlug())
    checkArgument(commitHash != null, Messages.BitbucketNotifier_NoCommitHash())

    def policyEvaluationHealthAction = run.getAllActions().find({ action ->
      PolicyEvaluationHealthAction.assignableFrom(action)
    })
    if (!policyEvaluationHealthAction && !applicationPolicyEvaluation) {
      logger.println(Messages.BitbucketNotifierStep_NoPolicyAction())
      throw new AbortException(Messages.BitbucketNotifierStep_NoPolicyAction())
    }
    if (applicationPolicyEvaluation && !ApplicationPolicyEvaluation.assignableFrom(applicationPolicyEvaluation)) {
      logger.println(Messages.BitbucketNotifierStep_IllegalArgumentPolicyEvaluation())
      throw new AbortException(Messages.BitbucketNotifierStep_IllegalArgumentPolicyEvaluation())
    }

    def client = BitbucketClientFactory.bitbucketClient
    sendPolicyEvaluationHealthAction(client, projectKey, repositorySlug, commitHash,
        PolicyEvaluationHealthAction.build(policyEvaluationHealthAction))
  }

  private void sendPolicyEvaluationHealthAction(final BitbucketClient bitbucketClient,
                                                final String projectKey,
                                                final String repositorySlug,
                                                final String commitHash,
                                                final PolicyEvaluationHealthAction policyEvaluationHealthAction)
  {
    try {
      bitbucketClient.putCard(new PolicyEvaluationResult(
          projectKey,
          repositorySlug,
          commitHash,
          BuildStatus.PASS,
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
