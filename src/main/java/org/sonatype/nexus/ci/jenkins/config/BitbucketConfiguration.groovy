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

import org.sonatype.nexus.ci.jenkins.util.FormUtil

import hudson.Extension
import hudson.model.Describable
import hudson.model.Descriptor
import hudson.util.FormValidation
import hudson.util.FormValidation.Kind
import hudson.util.ListBoxModel
import jenkins.model.Jenkins
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.QueryParameter

class BitbucketConfiguration
    implements Describable<BitbucketConfiguration>
{
  String serverUrl
  String credentialsId

  @DataBoundConstructor
  BitbucketConfiguration(final String serverUrl, final String credentialsId) {
    this.serverUrl = serverUrl
    this.credentialsId = credentialsId
  }

  @Override
  Descriptor<BitbucketConfiguration> getDescriptor() {
    return Jenkins.get().getDescriptorOrDie(this.getClass())
  }

  @Extension
  static class DescriptorImpl
      extends Descriptor<BitbucketConfiguration>
  {
    @Override
    String getDisplayName() {
      Messages.BitbucketConfiguration_DisplayName()
    }

    FormValidation doCheckServerUrl(@QueryParameter String value) {
      def validation = FormUtil.validateUrl(value)
      if (validation.kind == Kind.OK) {
        validation = FormUtil.validateNotEmpty(value, Messages.Configuration_ServerUrlRequired())
      }
      return validation
    }

    ListBoxModel doFillCredentialsIdItems(@QueryParameter String serverUrl,
                                          @QueryParameter String credentialsId) {
      return FormUtil.newCredentialsItemsListBoxModel(serverUrl, credentialsId, null)
    }
  }
}
