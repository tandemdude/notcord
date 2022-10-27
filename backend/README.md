# Running the Server

### Pre-requisites

- Java 17+
- Maven

### Environment

There are two required environment variables that must be set in order for the server to start:

- `NC_WORKER_ID` (integer) - the ID of the worker this backend instance is running on, should be `0` if it is the only
  worker.
- `NC_PROCESS_ID` (integer) - the ID of the process containing this backend instance, if this is the only instance of
  the backend running on the worker then this should be `0`.

Other environment variables are available and can be found in the `application.yaml` file. All these variables have
defaults set in the `application.yaml` so are not required to be provided.

### Running

You should run the project using the maven wrapper during development:

```shell
./mvnw clean package spring-boot:run
```

To deploy, a `.jar` can be built using:

```shell
./mvnw clean package
``` 

This can then be passed to `java -jar <filename>.jar` in order to start the server.
