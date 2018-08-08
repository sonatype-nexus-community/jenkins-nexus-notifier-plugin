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

import com.sonatype.nexus.api.iq.ApplicationPolicyEvaluation;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import static org.sonatype.nexus.ci.util.FormUtil.validateNotEmpty;

public class BitbucketNotifierStep
    extends Builder
    implements SimpleBuildStep
{
  private String projectKey;
  private String repositorySlug;
  private String commitHash;
  private ApplicationPolicyEvaluation applicationPolicyEvaluation;

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
  public void setApplicationPolicyEvaluation(final ApplicationPolicyEvaluation applicationPolicyEvaluation) {
    this.applicationPolicyEvaluation = applicationPolicyEvaluation;
  }

  public ApplicationPolicyEvaluation getApplicationPolicyEvaluation() {
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
    new BitbucketNotifier(listener).send(run, applicationPolicyEvaluation);
  }

  @Extension
  @Symbol("nexusIqBitbucketNotifier")
  public static final class DescriptorImpl
      extends BuildStepDescriptor<Builder>
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
      return validateNotEmpty(projectKey, Messages.BitbucketNotifierStep_ProjectKeyRequired());
    }

    public FormValidation doCheckRepositorySlug(@QueryParameter String repositorySlug) {
      return validateNotEmpty(repositorySlug, Messages.BitbucketNotifierStep_RepositorySlugRequired());
    }

    public FormValidation doCheckCommitHash(@QueryParameter String commitHash) {
      return validateNotEmpty(commitHash, Messages.BitbucketNotifierStep_CommitHashRequired());
    }
  }
}
