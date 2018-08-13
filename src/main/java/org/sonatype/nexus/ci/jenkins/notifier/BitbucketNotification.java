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
package org.sonatype.nexus.ci.jenkins.notifier;

import org.sonatype.nexus.ci.jenkins.config.BitbucketConfiguration;
import org.sonatype.nexus.ci.jenkins.config.NotifierConfiguration;
import org.sonatype.nexus.ci.jenkins.util.FormUtil;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Job;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import static com.google.common.base.Preconditions.checkArgument;

public class BitbucketNotification
    implements Describable<BitbucketNotification>
{
  private boolean sendBitbucketNotification;

  private String projectKey;

  private String repositorySlug;

  private String commitHash;

  private String jobCredentialsId;

  public boolean getSendBitbucketNotification() {
    return sendBitbucketNotification;
  }

  public String getProjectKey() {
    return projectKey;
  }

  public String getRepositorySlug() {
    return repositorySlug;
  }

  public String getCommitHash() {
    return commitHash;
  }

  public String getJobCredentialsId() {
    return jobCredentialsId;
  }

  @DataBoundConstructor
  public BitbucketNotification(final boolean sendBitbucketNotification, final String projectKey,
                               final String repositorySlug, final String commitHash, final String jobCredentialsId)
  {
    this.sendBitbucketNotification = sendBitbucketNotification;
    this.projectKey = projectKey;
    this.repositorySlug = repositorySlug;
    this.commitHash = commitHash;
    this.jobCredentialsId = jobCredentialsId;
  }

  @Override
  public Descriptor<BitbucketNotification> getDescriptor() {
    return Jenkins.get().getDescriptorOrDie(this.getClass());
  }

  @Extension
  @Symbol("nexusBitbucketNotification")
  public static final class DescriptorImpl
      extends Descriptor<BitbucketNotification>
  {
    @Override
    public String getDisplayName() {
      return Messages.BitbucketNotification_DisplayName();
    }

    public FormValidation doCheckProjectKey(@QueryParameter String projectKey) {
      return FormUtil.validateNotEmpty(projectKey, Messages.BitbucketNotification_ProjectKeyRequired());
    }

    public FormValidation doCheckRepositorySlug(@QueryParameter String repositorySlug) {
      return FormUtil.validateNotEmpty(repositorySlug, Messages.BitbucketNotification_RepositorySlugRequired());
    }

    public FormValidation doCheckCommitHash(@QueryParameter String commitHash) {
      return FormUtil.validateNotEmpty(commitHash, Messages.BitbucketNotification_CommitHashRequired());
    }

    public ListBoxModel doFillJobCredentialsIdItems(@AncestorInPath final Job job) {
      NotifierConfiguration configuration = NotifierConfiguration.getNotifierConfiguration();
      checkArgument(configuration != null, Messages.BitbucketClientFactory_NoConfiguration());
      checkArgument(configuration.getBitbucketConfigs() != null, Messages.BitbucketClientFactory_NoConfiguration());
      checkArgument(configuration.getBitbucketConfigs().size() > 0, Messages.BitbucketClientFactory_NoConfiguration());

      BitbucketConfiguration bitbucketConfiguration = configuration.getBitbucketConfigs().get(0);
      return FormUtil.newCredentialsItemsListBoxModel(bitbucketConfiguration.getServerUrl(),
          bitbucketConfiguration.getCredentialsId(), job);
    }
  }
}
