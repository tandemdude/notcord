#
# Copyright 2022 tandemdude
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

workdir=${PWD##*/}
if [ "$workdir" != "notcord" ]; then
  echo "Commands must be run from the repository root"
  exit 1
fi

pushd services || exit 1
pushd notcord-authorizer-server || exit 1

if [[ ! "${NC_WORKER_ID}" =~ ^[0-9]+$ ]]; then
  echo "NC_WORKER_ID not set or invalid. Defaulting to 0"
  export NC_WORKER_ID=0
fi
if [[ ! "${NC_PROCESS_ID}" =~ ^[0-9]+$ ]]; then
  echo "NC_PROCESS_ID not set or invalid. Defaulting to 0"
  export NC_PROCESS_ID=0
fi

npx tailwindcss -i ./src/main/resources/static/input.css -o ./src/main/resources/static/output.css && ../mvnw clean package spring-boot:run
