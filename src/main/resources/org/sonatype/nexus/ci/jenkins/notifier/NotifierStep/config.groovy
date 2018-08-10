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
package org.sonatype.nexus.ci.jenkins.notifier.NotifierStep

import org.sonatype.nexus.ci.jenkins.config.NotifierConfiguration
import org.sonatype.nexus.ci.jenkins.notifier.NotifierStep
import org.sonatype.nexus.ci.jenkins.notifier.Messages

import jenkins.model.Jenkins

def f = namespace(lib.FormTagLib)
def typedDescriptor = (NotifierStep.DescriptorImpl) descriptor
def l = namespace(lib.LayoutTagLib)

l.css(src: "${rootURL}/plugin/nexus-jenkins-plugin/css/nexus.css")

def notifierConfiguration = NotifierConfiguration.getNotifierConfiguration()
def hasBitbucket = notifierConfiguration != null && notifierConfiguration.bitbucketConfigs != null &&
    notifierConfiguration.bitbucketConfigs.size() > 0

f.section(title: typedDescriptor.displayName) {
  if (hasBitbucket) {
    f.property(field: 'bitbucketNotification')
  }
  if (!hasBitbucket) {
    tr {
      td(class: 'setting-leftspace') {}
      td {}
      td(class: 'nexus-jenkins-error') {
        div {
          h3(Messages.NotifierStep_NoNotifiers())
          div {
            yield Messages.NotifierStep_AddNotifiers()
            a(href: Jenkins.get().rootUrl + "/configure", "Configure System")
          }
        }
      }
    }
  }
}
