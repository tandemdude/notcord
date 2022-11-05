workdir=${PWD##*/}
if [ "$workdir" != "notcord" ]; then
  echo "Commands must be run from the repository root"
  exit 1
fi

pushd frontend || exit 1
yarn dev
