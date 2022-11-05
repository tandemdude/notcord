workdir=${PWD##*/}
if [ "$workdir" != "notcord" ]; then
  echo "Commands must be run from the repository root"
  exit 1
fi

pushd services || exit 1
./mvnw clean install
