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

import hudson.util.FormValidation.Kind
import spock.lang.Specification

class BitbucketNotifierDescriptorTest
    extends Specification
{
  BitbucketNotifierStep.DescriptorImpl descriptor = new BitbucketNotifierStep.DescriptorImpl()

  def 'it validates project key'() {
    when:
      "validating $key"
      def validation = descriptor.doCheckProjectKey(key)

    then:
      "it returns $result with message $message"
      validation.kind == result
      validation.renderHtml() == message

    where:
      key   | result     | message
      ''    | Kind.ERROR | 'Project Key is a required field'
      null  | Kind.ERROR | 'Project Key is a required field'
      'KEY' | Kind.OK    | '<div/>'
  }

  def 'it validates repository slug'() {
    when:
      "validating $slug"
      def validation = descriptor.doCheckRepositorySlug(slug)

    then:
      "it returns $result with message $message"
      validation.kind == result
      validation.renderHtml() == message

    where:
      slug              | result     | message
      ''                | Kind.ERROR | 'Repository Slug is a required field'
      null              | Kind.ERROR | 'Repository Slug is a required field'
      'repository-slug' | Kind.OK    | '<div/>'
  }

  def 'it validates commit hash'() {
    when:
      "validating $hash"
      def validation = descriptor.doCheckCommitHash(hash)

    then:
      "it returns $result with message $message"
      validation.kind == result
      validation.renderHtml() == message

    where:
      hash   | result     | message
      ''     | Kind.ERROR | 'Commit Hash is a required field'
      null   | Kind.ERROR | 'Commit Hash is a required field'
      'hash' | Kind.OK    | '<div/>'
  }
}
