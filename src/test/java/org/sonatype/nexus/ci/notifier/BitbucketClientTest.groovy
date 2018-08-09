package org.sonatype.nexus.ci.notifier

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
import org.sonatype.nexus.ci.notifier.PolicyEvaluationResult.BuildStatus

import groovy.json.JsonOutput
import spock.lang.Ignore
import spock.lang.Specification

class BitbucketClientTest
    extends Specification
{
  @Ignore
  def 'creates card for real'() {
    setup:
      def client = new BitBucketClient('localhost:7990', 'jcava', 'password')
      def result = new PolicyEvaluationResult('int', 'mini-java-maven-app', '2ef71f840d1688b0eee0226c758456adccb66fd0',
          BuildStatus.PASS, 5, 1, 2, 3,
          'https://policy.s/assets/index.html#/reports/webgoat/67a5be43062a40b8a739dc638b40bf91')
      def resp = client.putCard(result)
      def json = JsonOutput.toJson(resp)

    expect:
      json != null
      json.length() > 0
  }
}
