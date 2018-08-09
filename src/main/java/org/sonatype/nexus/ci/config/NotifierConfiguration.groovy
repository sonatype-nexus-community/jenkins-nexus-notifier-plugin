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
package org.sonatype.nexus.ci.config

import javax.annotation.Nullable

import hudson.Extension
import hudson.model.Descriptor
import jenkins.model.GlobalConfiguration
import net.sf.json.JSONObject
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.StaplerRequest

@Extension
class NotifierConfiguration
    extends GlobalConfiguration
{
  List<BitbucketConfiguration> bitbucketConfigs

  NotifierConfiguration() {
    load()
  }

  @DataBoundConstructor
  NotifierConfiguration(final List<BitbucketConfiguration> bitbucketConfigs) {
    this.bitbucketConfigs = bitbucketConfigs ?: []
  }

  @Override
  boolean configure(final StaplerRequest req, final JSONObject json) throws Descriptor.FormException {
    def notifierConfiguration = req.bindJSON(NotifierConfiguration, json)
    this.bitbucketConfigs = notifierConfiguration.bitbucketConfigs
    save()
    return true
  }

  @Override
  String getDisplayName() {
    return Messages.NotifierConfiguration_DisplayName()
  }

  static @Nullable NotifierConfiguration getNotifierConfiguration() {
    return (NotifierConfiguration) all().get(NotifierConfiguration)
  }
}
