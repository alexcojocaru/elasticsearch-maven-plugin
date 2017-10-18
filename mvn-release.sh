#   Copyright 2017 data.world, Inc.
#
#   Licensed under the Apache License, Version 2.0 (the "License");
#   you may not use this file except in compliance with the License.
#   You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.

#!/bin/bash
set -o errexit -o nounset

do_release() {
    # These variables are passed as build parameters to CircleCI
    : ${MVN_RELEASE_VER}
    : ${MVN_RELEASE_TAG}
    : ${MVN_RELEASE_DEV_VER}
    : ${MVN_RELEASE_USER_EMAIL}
    : ${MVN_RELEASE_USER_NAME}

    git config user.email "${MVN_RELEASE_USER_EMAIL}"
    git config user.name "${MVN_RELEASE_USER_NAME}"

    mvn -B -Dtag=${MVN_RELEASE_TAG} release:clean release:prepare \
               -DreleaseVersion=${MVN_RELEASE_VER} \
               -DdevelopmentVersion=${MVN_RELEASE_DEV_VER} \
               -DscmCommentPrefix='[maven-release-plugin] [skip ci]'

    mvn -B -s settings.xml release:perform

    mvn release:clean
}

#If the environment has a Maven release version set, let's do a release
if [[ -v MVN_RELEASE_VER ]]; then
  do_release
fi
