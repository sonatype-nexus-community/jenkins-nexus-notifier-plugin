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
package org.sonatype.nexus.ci.model

class PolicyEvaluationHealthAction
{
  String reportLink

  int affectedComponentCount

  int criticalComponentCount

  int severeComponentCount

  int moderateComponentCount

  static boolean assignableFrom(Object action) {
    return action.hasProperty('reportLink') && action.hasProperty('affectedComponentCount') &&
        action.hasProperty('criticalComponentCount') && action.hasProperty('severeComponentCount') &&
        action.hasProperty('moderateComponentCount')
  }

  static PolicyEvaluationHealthAction build(Object action) {
    return new PolicyEvaluationHealthAction(
        reportLink: action.reportLink,
        affectedComponentCount: action.affectedComponentCount,
        criticalComponentCount: action.criticalComponentCount,
        severeComponentCount: action.severeComponentCount,
        moderateComponentCount: action.moderateComponentCount
    )
  }
}
