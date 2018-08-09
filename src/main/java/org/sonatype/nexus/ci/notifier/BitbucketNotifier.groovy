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

import hudson.AbortException
import hudson.model.Run
import hudson.model.TaskListener

class BitbucketNotifier
{
  final TaskListener listener

  BitbucketNotifier(@Nonnull final TaskListener listener) {
    this.listener = listener
  }

  void send(@Nonnull final Run run, final Object applicationPolicyEvaluation)
  {
    def logger = listener.logger
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
  }
}
