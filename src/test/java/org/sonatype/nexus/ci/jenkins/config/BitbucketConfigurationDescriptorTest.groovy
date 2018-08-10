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
package org.sonatype.nexus.ci.jenkins.config

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
