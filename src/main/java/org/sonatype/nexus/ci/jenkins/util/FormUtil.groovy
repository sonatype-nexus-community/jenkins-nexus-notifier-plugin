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
package org.sonatype.nexus.ci.jenkins.util

import com.cloudbees.plugins.credentials.CredentialsProvider
import com.cloudbees.plugins.credentials.common.StandardCertificateCredentials
import com.cloudbees.plugins.credentials.common.StandardCredentials
import com.cloudbees.plugins.credentials.common.StandardListBoxModel
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials
import hudson.model.Item
import hudson.security.ACL
import hudson.util.FormValidation
import hudson.util.ListBoxModel
import jenkins.model.Jenkins

import static com.cloudbees.plugins.credentials.CredentialsMatchers.anyOf
import static com.cloudbees.plugins.credentials.CredentialsMatchers.instanceOf
import static com.cloudbees.plugins.credentials.domains.URIRequirementBuilder.fromUri

class FormUtil
{
  static FormValidation validateUrl(String url) {
    try {
      if (url) {
        new URL(url)
      }

      return FormValidation.ok()
    }
    catch (MalformedURLException e) {
      return FormValidation.error('Malformed url (%s)', e.getMessage())
    }
  }

  static FormValidation validateNotEmpty(String value, String error) {
    if (!value) {
      return FormValidation.error(error)
    }
    return FormValidation.ok()
  }

  static ListBoxModel newCredentialsItemsListBoxModel(final String serverUrl,
                                                      final String credentialsId,
                                                      final Item ancestor)
  {
    // Ref: https://github.com/jenkinsci/credentials-plugin/blob/master/docs/consumer.adoc
    boolean noContextNotAdmin = ancestor == null && !Jenkins.get().hasPermission(Jenkins.ADMINISTER)
    boolean contextNoPerm = ancestor != null && !ancestor.hasPermission(Item.EXTENDED_READ) &&
        !ancestor.hasPermission(CredentialsProvider.USE_ITEM)

    if (noContextNotAdmin || contextNoPerm || !serverUrl) {
      return new StandardListBoxModel().includeCurrentValue(credentialsId)
    }

    //noinspection GroovyAssignabilityCheck
    return new StandardListBoxModel()
        .includeEmptyValue()
        .includeMatchingAs(ACL.SYSTEM,
        ancestor ?: Jenkins.get(),
        StandardCredentials,
        fromUri(serverUrl).build(),
        anyOf(instanceOf(StandardUsernamePasswordCredentials)))
  }
}
