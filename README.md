<!--

    Copyright (c) 2018-present Sonatype, Inc. All rights reserved.

    This program is licensed to you under the Apache License Version 2.0,
    and you may not use this file except in compliance with the Apache License Version 2.0.
    You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.

    Unless required by applicable law or agreed to in writing,
    software distributed under the Apache License Version 2.0 is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.

-->
# Nexus Notifier Plugin

A Jenkins plugin to consume results of the [Nexus Platform Plugin](https://plugins.jenkins.io/nexus-jenkins-plugin) and
publish them to various services. 

## Notifiers

### Bitbucket Server Code Insights

A notifier for Code Insights, a feature of Bitbucket Server EAP. Nexus Notifier for Code Insights allows
policy evaluation information to show up in Bitbucket Server EAP alongside code and Pull Requests. Use Nexus Lifecycle
policy evaluations to determine whether new code should be merged to master. More information can be seen in the video
below.

[![Nexus Notifier for Bitbucket Server](https://img.youtube.com/vi/-_sSmoq_6ow/0.jpg)](https://www.youtube.com/watch?v=-_sSmoq_6ow)

## Plugin Usage

### Configuration

Notifier destinations need to be first configured in the Global Configuration. The Freestyle UI and Pipeline Syntax
Generator will only display configuration options for destinations configured in the Global Configuration.

### Freestyle

The Nexus Notifier can be used in Freestyle builds as a Post Build action. The Nexus Platform Plugin "Invoke Nexus
Policy Evaluation" step must be run during the build to use the Nexus Notifier action.

### Pipeline

The Pipeline Syntax Generator can be used to generate pipeline steps for the Nexus Notifier.

## Development

Development to this plugin is encouraged. Please see the [Sonatype Code Style](https://github.com/sonatype/codestyle)
repository for acceptable code styles their IDE settings.

## LICENSE

    Copyright (c) 2018-present Sonatype, Inc. All rights reserved.

    This program is licensed to you under the Apache License Version 2.0,
    and you may not use this file except in compliance with the Apache License Version 2.0.
    You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.

    Unless required by applicable law or agreed to in writing,
    software distributed under the Apache License Version 2.0 is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
