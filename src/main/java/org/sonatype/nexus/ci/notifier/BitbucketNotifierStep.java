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
package org.sonatype.nexus.ci.notifier;

import javax.annotation.Nonnull;

import org.sonatype.nexus.ci.util.FormUtil;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

public class BitbucketNotifierStep
    extends Notifier
    implements SimpleBuildStep
{
  private String projectKey;
  private String repositorySlug;
  private String commitHash;
  private Object applicationPolicyEvaluation;

  public String getProjectKey() {
    return projectKey;
  }

  public String getRepositorySlug() {
    return repositorySlug;
  }

  public String getCommitHash() {
    return commitHash;
  }

  @DataBoundSetter
  public void setApplicationPolicyEvaluation(final Object applicationPolicyEvaluation) {
    this.applicationPolicyEvaluation = applicationPolicyEvaluation;
  }

  public Object getApplicationPolicyEvaluation() {
    return applicationPolicyEvaluation;
  }

  @DataBoundConstructor
  public BitbucketNotifierStep(
      final String projectKey,
      final String repositorySlug,
      final String commitHash)
  {
    this.projectKey = projectKey;
    this.repositorySlug = repositorySlug;
    this.commitHash = commitHash;
  }

  @Override
  public void perform(@Nonnull final Run run,
               @Nonnull final FilePath workspace,
               @Nonnull final Launcher launcher,
               @Nonnull final TaskListener listener)
  {
    new BitbucketNotifier(listener).send(run, projectKey, repositorySlug, commitHash, applicationPolicyEvaluation);
  }

  @Extension
  @Symbol("nexusIqBitbucketNotifier")
  public static final class DescriptorImpl
      extends BuildStepDescriptor<Publisher>
  {
    @Override
    public boolean isApplicable(final Class<? extends AbstractProject> jobType) {
      return true;
    }

    @Override
    public String getDisplayName() {
      return Messages.BitbucketNotifierStep_DisplayName();
    }

    public FormValidation doCheckProjectKey(@QueryParameter String projectKey) {
      return FormUtil.validateNotEmpty(projectKey, Messages.BitbucketNotifierStep_ProjectKeyRequired());
    }

    public FormValidation doCheckRepositorySlug(@QueryParameter String repositorySlug) {
      return FormUtil.validateNotEmpty(repositorySlug, Messages.BitbucketNotifierStep_RepositorySlugRequired());
    }

    public FormValidation doCheckCommitHash(@QueryParameter String commitHash) {
      return FormUtil.validateNotEmpty(commitHash, Messages.BitbucketNotifierStep_CommitHashRequired());
    }
  }
}
