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
package org.sonatype.nexus.ci.notifier.BitbucketNotification

import org.sonatype.nexus.ci.notifier.BitbucketNotification

def f = namespace(lib.FormTagLib)
def typedDescriptor = (BitbucketNotification.DescriptorImpl) descriptor

f.section(title: typedDescriptor.displayName) {
  f.entry(title: _('Send Bitbucket Notification'), field: 'sendBitbucketNotification') {
    f.checkbox()
  }

  f.entry(title: _('Bitbucket Project Key'), field: 'projectKey') {
    f.textbox(clazz: 'required')
  }

  f.entry(title: _('Bitbucket Repository Slug'), field: 'repositorySlug') {
    f.textbox(clazz: 'required')
  }

  f.entry(title: _('Bitbucket Commit Hash'), field: 'commitHash') {
    f.textbox(clazz: 'required')
  }
}
