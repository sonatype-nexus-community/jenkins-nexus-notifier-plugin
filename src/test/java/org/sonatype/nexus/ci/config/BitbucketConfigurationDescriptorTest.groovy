package org.sonatype.nexus.ci.config

import hudson.util.FormValidation.Kind
import spock.lang.Specification

class BitbucketConfigurationDescriptorTest
    extends Specification
{
  BitbucketConfiguration.DescriptorImpl descriptor = new BitbucketConfiguration.DescriptorImpl()

  def 'it validates server url'() {
    when:
      "validating $url"
      def validation = descriptor.doCheckServerUrl(url)

    then:
      "it returns $result with message $message"
      validation.kind == result
      validation.renderHtml() == message

    where:
      url               | result     | message
      ''                | Kind.ERROR | 'Server URL is a required field'
      null              | Kind.ERROR | 'Server URL is a required field'
      'somestring'      | Kind.ERROR | 'Malformed url (no protocol: somestring)'
      'http://foo.com/' | Kind.OK    | '<div/>'
  }
}
