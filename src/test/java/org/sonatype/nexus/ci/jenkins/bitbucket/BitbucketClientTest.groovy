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

import org.sonatype.nexus.ci.jenkins.http.SonatypeHTTPBuilder

import groovy.json.JsonOutput
import spock.lang.Ignore
import spock.lang.Specification

import static org.sonatype.nexus.ci.jenkins.bitbucket.PolicyEvaluationResult.BuildStatus.FAIL
import static org.sonatype.nexus.ci.jenkins.bitbucket.PolicyEvaluationResult.BuildStatus.PASS

class BitbucketClientTest
    extends Specification
{
  def http
  def client

  def setup() {
    http = Mock(SonatypeHTTPBuilder)
    client = new BitbucketClient('https://bitbucket:7990', 'username', 'password')
    client.http = http
  }

  def 'put card has correct url'() {
    def url

    when:
      client.putCard(result)

    then:
      1 * http.put(_, _, _) >> { args -> url = args[0]}

    and:
      url == 'https://bitbucket:7990/rest/insights/1.0/projects/int/repos/repo/commits/abcdefg/reports/sonatype-nexus-iq'

    where:
      result = getFailPolicyEvaluationResult()
  }

  def 'put card has correct headers'() {
    def headers

    when:
      client.putCard(result)

    then:
      1 * http.put(_, _, _) >> { args -> headers = args[2]}

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
      1 * http.put(_, _, _) >> { args -> body = args[1]}

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
      client.putCard(getSuccessPolicyEvaluationResult())

    then:
      1 * http.put(_, _, _) >> { args -> body = args[1]}

    and:
      body['title'] == 'Nexus Lifecycle'
  }

  def 'put card has correct build status'() {
    def body

    when:
      client.putCard(result)

    then:
      1 * http.put(_, _, _) >> { args -> body = args[1]}

    and:
      body['result'] == status

    where:
      result                             | status
      getSuccessPolicyEvaluationResult() | PASS
      getFailPolicyEvaluationResult()    | FAIL
  }

  def 'put card has correct vendor info'() {
    def body

    when:
      client.putCard(result)

    then:
      1 * http.put(_, _, _) >> { args -> body = args[1]}

    and:
      body['vendor'] == 'Sonatype'
      body['link'] == 'https://host/report'
      body['logoUrl'] == 'http://cdn.sonatype.com/brand/logo/nexus-iq-64-no-background.png'

    where:
      result = getFailPolicyEvaluationResult()
  }

  def 'put card has correct report summary info'() {
    def body

    when:
      client.putCard(result)

    then:
      1 * http.put(_, _, _) >> { args -> body = args[1]}

    and: 'components affected'
      body['data'][0]['title'] == 'Components Affected'
      body['data'][0]['value'] == 4

    and: 'critical'
      body['data'][1]['title'] == 'Critical'
      body['data'][1]['value'] == 1

    and: 'severe'
      body['data'][2]['title'] == 'Severe'
      body['data'][2]['value'] == 2

    and: 'moderate'
      body['data'][3]['title'] == 'Moderate'
      body['data'][3]['value'] == 3

    where:
      result = getFailPolicyEvaluationResult()
  }

  def 'put card has correct report url'() {
    def body

    when:
      client.putCard(result)

    then:
      1 * http.put(_, _, _) >> { args -> body = args[1]}

    and: 'report link'
      body['data'][4]['title'] == 'View full report'
      body['data'][4]['type'] == 'LINK'
      body['data'][4]['value']['href'] == 'https://host/report'

    where:
      result = getFailPolicyEvaluationResult()
  }

  @Ignore
  def 'helper test to verify interaction with Bitbucket Server'() {
    setup:
      def client = new BitbucketClient('http://localhost:7990', 'jcava', 'password')
      def result = new PolicyEvaluationResult('int', 'mini-java-maven-app', '2ef71f840d1688b0eee0226c758456adccb66fd0',
          PASS, 5, 1, 2, 3,
          'https://policy.s/assets/index.html#/reports/webgoat/67a5be43062a40b8a739dc638b40bf91')
      def resp = client.putCard(result)
      def json = JsonOutput.toJson(resp)

    expect:
      json != null
      json.length() > 0
  }

  private PolicyEvaluationResult getFailPolicyEvaluationResult() {
    new PolicyEvaluationResult('int', 'repo', 'abcdefg', FAIL, 4, 1, 2, 3, 'https://host/report')
  }

  private PolicyEvaluationResult getSuccessPolicyEvaluationResult() {
    new PolicyEvaluationResult('int', 'repo', 'abcdefg', PASS, 0, 0, 0, 0, 'https://host/report')
  }
}
