# Running Backend Services

## Pre-requisites

- Java 19+
- Maven

## Environment

There are two required environment variables that must be set in order for the server to start:

- `NC_WORKER_ID` (integer) - the ID of the worker this backend instance is running on, should be `0` if it is the only
  worker.
- `NC_PROCESS_ID` (integer) - the ID of the process containing this backend instance, if this is the only instance of
  the backend running on the worker then this should be `0`.

Other environment variables are available and can be found in the `application.yaml` file for each respective service.
Most of these variables have defaults set in the `application.yaml` so are not required to be provided.

> :warning: **If running using the CLI tool then the above two variables will be set to `0` by default if they do not
already exist**

## Running

It is highly recommended that you run the services using the provided CLI tool. The commands below can be used to
run each individual service. Note that the commands **must** be run from the root of the directory, not in this
directory.

#### Authorizer

Note that the authorizer requires `tailwindcss` to be installed before you can run the project. This should be installed
by first running `npm install` in the `notcord-authorizer-server` directory before attempting to run the below command.

```shell
./notcord run authorizer
```

#### REST Backend

```shell
./notcord run rest
```
