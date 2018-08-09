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

class ApplicationPolicyEvaluation
{
  int affectedComponentCount
  int criticalComponentCount
  int severeComponentCount
  int moderateComponentCount
  String applicationCompositionReportUrl

  List<PolicyAlert> policyAlerts

  static class PolicyAlert {
    PolicyFact trigger

    static boolean assignableFrom(Object alert) {
      return alert.hasProperty('trigger') && PolicyFact.assignableFrom(alert.trigger)
    }

    static PolicyAlert build(Object alert) {
      return new PolicyAlert(
          trigger: PolicyFact.build(alert.trigger)
      )
    }

    static class PolicyFact {
      String policyName
      int threatLevel

      static boolean assignableFrom(Object fact) {
        return fact.hasProperty('policyName') && fact.hasProperty('threatLevel')
      }

      static PolicyFact build(Object fact) {
        return new PolicyFact(
            policyName: fact.policyName,
            threatLevel: fact.threatLevel
        )
      }
    }
  }

  static boolean assignableFrom(Object evaluation) {
    return evaluation.hasProperty('affectedComponentCount') && evaluation.hasProperty('criticalComponentCount') &&
        evaluation.hasProperty('severeComponentCount') && evaluation.hasProperty('moderateComponentCount') &&
        evaluation.hasProperty('applicationCompositionReportUrl') && evaluation.hasProperty('policyAlerts') &&
        evaluation.policyAlerts instanceof List &&
        ((List) evaluation.policyAlerts).every { a -> PolicyAlert.assignableFrom(a) }
  }

  static ApplicationPolicyEvaluation build(Object evaluation) {
    return new ApplicationPolicyEvaluation(
        affectedComponentCount: evaluation.affectedComponentCount,
        criticalComponentCount: evaluation.criticalComponentCount,
        severeComponentCount: evaluation.severeComponentCount,
        moderateComponentCount: evaluation.moderateComponentCount,
        applicationCompositionReportUrl: evaluation.applicationCompositionReportUrl,
        policyAlerts: ((List) evaluation.policyAlerts).each { a -> PolicyAlert.build(a) }
    )
  }
}
