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
import hudson.FilePath
import hudson.Launcher
import hudson.model.Action
import hudson.model.Run
import hudson.model.TaskListener
import spock.lang.Specification

class NotifierStepTest
    extends Specification
{
  def mockLogger = Mock(PrintStream)
  def mockListener = Mock(TaskListener)

  NotifierStep notifierStep

  def setup() {
    mockListener.getLogger() >> mockLogger
    notifierStep = new NotifierStep()
  }

  def 'throws abort exception when PolicyEvaluationHealthAction not available'() {
    setup:
      def tempWorkspace = File.createTempDir()
    when:
      def run = Mock(Run)
      run.getActions() >> new ArrayList<Action>()
      notifierStep.perform(run, new FilePath(tempWorkspace), Mock(Launcher), mockListener)

    then:
      AbortException ex = thrown()
      ex.getMessage() ==
          'No policy evaluation results found. Run the Nexus Policy Evaluation before Nexus Notifier.'

    cleanup:
      tempWorkspace.delete()
  }
}
