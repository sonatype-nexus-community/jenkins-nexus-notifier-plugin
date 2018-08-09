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

import hudson.AbortException
import hudson.model.Action
import hudson.model.Run
import hudson.model.TaskListener
import spock.lang.Specification

class BitbucketNotifierTest
    extends Specification
{
  def mockLogger = Mock(PrintStream)
  def mockListener = Mock(TaskListener)

  BitbucketNotifier bitbucketNotifier

  def setup() {
    mockListener.getLogger() >> mockLogger
    bitbucketNotifier = new BitbucketNotifier(mockListener)
  }

  def 'throws abort exception when PolicyEvaluationHealthAction not available'() {
    when:
      def run = Mock(Run)
      run.getActions() >> new ArrayList<Action>()
      bitbucketNotifier.send(run, null)

    then:
      AbortException ex = thrown()
      ex.getMessage() ==
          'No policy evaluation results found. Run the Nexus Policy Evaluation before Bitbucket Notifier or pass ' +
          'evaluation results as parameter.'
  }

  def 'throws abort exception when ApplicationPolicyEvaluation not valid type'() {
    when:
      def run = Mock(Run)
      run.getActions() >> new ArrayList<Action>()
      bitbucketNotifier.send(run, new Object())

    then:
      AbortException ex = thrown()
      ex.getMessage() ==
          'The object passed to the Bitbucket Notifier is not valid. Please pass result of Nexus Policy Evaluation to' +
          ' the Bitbucket Notifier.'
  }
}
