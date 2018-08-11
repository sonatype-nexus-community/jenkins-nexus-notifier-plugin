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

import org.sonatype.nexus.ci.jenkins.config.BitbucketConfiguration
import org.sonatype.nexus.ci.jenkins.config.NotifierConfiguration

import com.cloudbees.plugins.credentials.CredentialsMatchers
import com.cloudbees.plugins.credentials.common.StandardCredentials
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials
import com.google.common.collect.Lists
import hudson.util.Secret
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Specification

class BitbucketClientFactoryTest
    extends Specification
{
  @Rule
  public JenkinsRule jenkinsRule = new JenkinsRule()

  def 'requires global Bitbucket configuration'() {
    setup:
      invalidConfig()

    when:
      BitbucketClientFactory.getBitbucketClient()

    then:
      IllegalArgumentException ex = thrown()
      ex.message == 'Bitbucket Notifier must be configured in Global Configuration to be used'

    where:
      invalidConfig << [
          { NotifierConfiguration.notifierConfiguration.bitbucketConfigs = null },
          { NotifierConfiguration.notifierConfiguration.bitbucketConfigs = Lists.newArrayList() }
      ]
  }

  def 'requires global Bitbucket serverUrl and credentialsId'() {
    setup:
      invalidConfig()

    when:
      BitbucketClientFactory.getBitbucketClient()

    then:
      thrown(NullPointerException.class)

    where:
      invalidConfig << [
          {
            def bitbucketConfig = new BitbucketConfiguration(null, null)
            NotifierConfiguration.notifierConfiguration.bitbucketConfigs = Lists.asList(bitbucketConfig)
          },
          {
            def bitbucketConfig = new BitbucketConfiguration('http://server.com', null)
            NotifierConfiguration.notifierConfiguration.bitbucketConfigs = Lists.asList(bitbucketConfig)
          },
          {
            def bitbucketConfig = new BitbucketConfiguration(null, 'credentialsId')
            NotifierConfiguration.notifierConfiguration.bitbucketConfigs = Lists.asList(bitbucketConfig)
          }
      ]
  }

  def 'requires global credentialsId non empty'() {
    setup:
      def bitbucketConfig = new BitbucketConfiguration('http://server.com', '')
      NotifierConfiguration.notifierConfiguration.bitbucketConfigs = Lists.asList(bitbucketConfig)

    when:
      BitbucketClientFactory.getBitbucketClient()

    then:
      thrown(IllegalArgumentException.class)
  }

  def 'requires credentials to be present for credentialsId'() {
    setup:
      GroovyMock(CredentialsMatchers.class, global: true)
      CredentialsMatchers.firstOrNull(_, _) >> null

      def bitbucketConfig = new BitbucketConfiguration('http://server.com', 'credentialsId')
      NotifierConfiguration.notifierConfiguration.bitbucketConfigs = Lists.asList(bitbucketConfig)

    when:
      BitbucketClientFactory.getBitbucketClient()

    then:
      IllegalArgumentException ex = thrown()
      ex.message == 'No credentials were found for credentials ID credentialsId'
  }

  def 'creates Bitbucket client'() {
    setup:
      StandardCredentials credentials = Mock(StandardUsernamePasswordCredentials)
      GroovyMock(CredentialsMatchers.class, global: true)
      credentials.getUsername() >> 'username'
      credentials.getPassword() >> new Secret('password')
      CredentialsMatchers.firstOrNull(_, _) >> credentials

      def bitbucketConfig = new BitbucketConfiguration('http://server.com', 'credentialsId')
      NotifierConfiguration.notifierConfiguration.bitbucketConfigs = Lists.asList(bitbucketConfig)

    when:
      def bitbucketClient = BitbucketClientFactory.getBitbucketClient()

    then:
      1 * CredentialsMatchers.withId('credentialsId')

    and:
      bitbucketClient != null
      bitbucketClient.username == 'username'
      bitbucketClient.password == 'password'
  }

  def 'creates Bitbucket client with override credentials'() {
    setup:
      StandardCredentials credentials = Mock(StandardUsernamePasswordCredentials)
      GroovyMock(CredentialsMatchers.class, global: true)
      credentials.getUsername() >> 'username'
      credentials.getPassword() >> new Secret('password')
      CredentialsMatchers.firstOrNull(_, _) >> credentials

      def bitbucketConfig = new BitbucketConfiguration('http://server.com', 'credentialsId')
      NotifierConfiguration.notifierConfiguration.bitbucketConfigs = Lists.asList(bitbucketConfig)

    when:
      def bitbucketClient = BitbucketClientFactory.getBitbucketClient('overrideId')

    then: 'the override credentials id is used'
      1 * CredentialsMatchers.withId('overrideId')

    and:
      bitbucketClient != null
      bitbucketClient.username == 'username'
      bitbucketClient.password == 'password'
  }
}
