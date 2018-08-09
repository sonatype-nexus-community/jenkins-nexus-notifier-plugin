import org.sonatype.nexus.ci.notifier.BitBucketClient
import org.sonatype.nexus.ci.notifier.PolicyEvaluationResult
import org.sonatype.nexus.ci.notifier.PolicyEvaluationResult.BuildStatus

import groovy.json.JsonOutput
import spock.lang.Specification

class BitbucketClientTest
    extends Specification
{
  def 'creates card for real'() {
    setup:
      def client = new BitBucketClient('localhost:7990', 'jcava', 'password')
      def result = new PolicyEvaluationResult('int', 'mini-java-maven-app', '2ef71f840d1688b0eee0226c758456adccb66fd0',
          BuildStatus.FAIL, 5, 1, 2, 3,
          'https://policy.s/assets/index.html#/reports/webgoat/67a5be43062a40b8a739dc638b40bf91')
      def resp = client.putCard(result)
      def json = JsonOutput.toJson(resp)

    expect:
      json != null
      json.length() > 0
  }
}
