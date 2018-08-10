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
package org.sonatype.nexus.ci.jenkins.config.BitbucketConfiguration

import org.sonatype.nexus.ci.jenkins.config.Messages
import org.sonatype.nexus.ci.jenkins.config.BitbucketConfiguration

def f = namespace(lib.FormTagLib)
def c = namespace(lib.CredentialsTagLib)

def typedDescriptor = (BitbucketConfiguration.DescriptorImpl) descriptor

f.section(title: typedDescriptor.displayName) {
  f.entry(title: _(Messages.Configuration_ServerUrl()), field: 'serverUrl') {
    f.textbox(clazz: 'required')
  }

  f.entry(title: _(Messages.Configuration_Credentials()), field: 'credentialsId') {
    c.select(context: app, includeUser: false, expressionAllowed: false)
  }
}
