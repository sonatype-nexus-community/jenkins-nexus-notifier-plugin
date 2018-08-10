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
package org.sonatype.nexus.ci.jenkins.model

import hudson.model.Action
import spock.lang.Specification

import static org.sonatype.nexus.ci.jenkins.model.PolicyEvaluationHealthAction.assignableFrom
import static org.sonatype.nexus.ci.jenkins.model.PolicyEvaluationHealthAction.build

class PolicyEvaluationHealthActionTest
    extends Specification
{
  def assignableAction = new PolicyEvaluationHealthAction(
      reportLink: 'http://foo',
      affectedComponentCount: 1,
      criticalComponentCount: 2,
      severeComponentCount: 3,
      moderateComponentCount: 5
  )

  def 'it determines assignable objects'() {
    when:
      def isAssignable = assignableFrom(assignableAction)

    then:
      isAssignable
  }

  def 'it determines unassignable objects'() {
    when:
      def isAssignable = assignableFrom(unAssignableAction)

    then:
      !isAssignable

    where:
      unAssignableAction << [
          new Object(),
          new Action() {
            @Override
            String getIconFileName() {
              return null
            }

            @Override
            String getDisplayName() {
              return null
            }

            @Override
            String getUrlName() {
              return null
            }
          },
          [
              reportLink            : 'http://foo',
              affectedComponentCount: 1,
              criticalComponentCount: 2,
              severeComponentCount  : 3,
              moderateComponentCount: 5
          ]
      ]
  }

  def 'it builds Policy Evaluation Health Action'() {
    when:
      def action = build(assignableAction)

    then:
      action.reportLink == assignableAction.reportLink
      action.criticalComponentCount == assignableAction.criticalComponentCount
      action.severeComponentCount == assignableAction.severeComponentCount
      action.moderateComponentCount == assignableAction.moderateComponentCount
      action.affectedComponentCount == assignableAction.affectedComponentCount
  }
}
