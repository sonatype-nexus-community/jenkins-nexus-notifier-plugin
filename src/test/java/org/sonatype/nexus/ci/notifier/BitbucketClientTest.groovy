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

import org.sonatype.nexus.ci.notifier.PolicyEvaluationResult.BuildStatus

import groovy.json.JsonOutput
import spock.lang.Ignore
import spock.lang.Specification

class BitbucketClientTest
    extends Specification
{
  def http
  def client

  def setup() {
    http = Mock(HttpClient)
    client = new BitBucketClient('https://bitbucket', 'username', 'password')
    client.http = http
  }

  def 'put card has correct url'() {
    def url

    when:
      client.putCard(result)

    then:
      1 * http.putCard(_, _, _) >> { args -> url = args[0]}

    and:
      url == 'https://bitbucket/rest/insights/1.0/projects/int/repos/repo/commits/abcdefg/cards/NEXUS'

    where:
      result = getFailPolicyEvaluationResult()
  }

  def 'put card has correct headers'() {
    def headers

    when:
      client.putCard(result)

    then:
      1 * http.putCard(_, _, _) >> { args -> headers = args[2]}

    and:
      headers == ['User-Agent':'nexus-jenkins-notifier', Authorization:'Basic dXNlcm5hbWU6cGFzc3dvcmQ=']

    where:
      result = getFailPolicyEvaluationResult()
  }

  def 'put card has correct details'() {
    def body

    when:
      client.putCard(result)

    then:
      1 * http.putCard(_, _, _) >> { args -> body = args[1]}

    and:
      body['details'] == details

    where:
      result                             | details
      getSuccessPolicyEvaluationResult() | 'No Policy Violations Found'
      getFailPolicyEvaluationResult()    | 'Policy Violations Found'
  }

  def 'put card has correct title'() {
    def body

    when:
      client.putCard(result)

    then:
      1 * http.putCard(_, _, _) >> { args -> body = args[1]}

    and:
      body['title'] == title

    where:
      result                             | title
      getSuccessPolicyEvaluationResult() | 'No Policy Violations Found'
      getFailPolicyEvaluationResult()    | 'Policy Violations Found'
  }

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

  private PolicyEvaluationResult getFailPolicyEvaluationResult() {
    new PolicyEvaluationResult('int', 'repo', 'abcdefg', BuildStatus.FAIL, 4, 1, 2, 3, 'https://host/report')
  }

  private PolicyEvaluationResult getSuccessPolicyEvaluationResult() {
    new PolicyEvaluationResult('int', 'repo', 'abcdefg', BuildStatus.PASS, 2, 0, 0, 2, 'https://host/report')
  }
}
