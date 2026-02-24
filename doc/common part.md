# Common documentation for all modules

This is the shared common documentation for all Maven modules as mentioned
in the root `README.md`.

## Prerequisite

All 3 modules require the following:

1. Java 25 or above.

Some modules have additional prerequisites. Please refer to the
`README.md` in each module for details.

## How to compile

1. Clone this repository.

2. Build a specific module:

   ```sh
   ./mvnw clean package -pl <the module folder> -am -DskipTests=true
   ```

   Example:

   ```sh
   ./mvnw clean package -pl vvd-taskproducer -am -DskipTests=true
   ```

   Build all modules:

   ```sh
   ./mvnw clean package -DskipTests=true
   ```

   Parameters:

   - `-pl <the module folder>`: build the specific module.
   - `-am`: build all dependent modules.
   - `-DskipTests=true`: skip the test cases.

3. The Uber jar for each module will be generated in the `target` folder
   in each `<the module folder>` folder. Example: the Uber jar for
   `vvd-taskproducer` will be generated in `vvd-taskproducer/target`.

## How to test

A [docker image](../docker/env-setup.Dockerfile) is provided for testing.
It is used in both local and CI environments.

Make sure Docker and Docker Compose are available in your environment.

To run tests:

1. Build the test image:

   ```sh
   docker compose -f "docker/docker-compose.base.yml" \
     -f "docker/docker-compose.test-all.yml" build
   ```

2. Run all tests:

   ```sh
   docker compose -f "docker/docker-compose.base.yml" \
     -f "docker/docker-compose.test-all.yml" up --exit-code-from base
   ```

   It by default runs `./mvnw clean verify` and mounts the repo plus your
   local Maven cache.

3. Run tests for a single module:

   ```sh
   docker compose -f "docker/docker-compose.base.yml" \
     -f "docker/docker-compose.test-all.yml" run --rm base \
     ./mvnw clean verify -pl <module folder> -am
   ```

4. Optional cleanup:

   ```sh
   docker compose -f "docker/docker-compose.base.yml" \
     -f "docker/docker-compose.test-all.yml" down
   ```

Remote debugging: run tests with
`docker/docker-compose.debug-test-all.yml` or add the following to your
Maven command:

```sh
-Dmaven.surefire.debug="\
-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:8000"
```

Then attach your debugger to `localhost:8000`.

## How to run

The Uber jar is runnable using `java -jar <jar file>` command. However,
each module requires some configurations to run.

Each module is implemented as a Spring Boot CLI application (without the
web environment). The CLI part is implemented using
[Spring Boot Configuration][spring-boot-config].

So all available arguments and configurations are stored in the
`application.yml` file in the `src/main/resources` folder. Each
`application.yml` file is well documented. So feel free to check out
each module's `application.yml` file and use it as a reference.

## How to specify configurations

The configuration can be specified in any way supported by Spring Boot
Configuration, including but not limited to command lines, environment
variables, external `application.yml` or `application.properties` files,
etc. Refer to the [Documentation][spring-boot-config] for details.

The recommended way to run this project is to import an external YAML
file using `--spring.config.import=file:<relative path to your YAML file>`
in the command line. You can first copy the `application.yml` file from
the `src/main/resources` folder, modify it, and then import it using the
command line. For example, to run `vvd-taskproducer` with your new
`my-config.yml` in the current folder, run:

```sh
java -jar <vvd-taskproducer uber jar> \
  --spring.config.import=file:my-config.yml
```

If you want, you can also temporarily override some configurations using
command line arguments. For example, to run `vvd-taskproducer` with the
configuration in `my-config.yml` in the current folder and override the
`io.output-directory` property to `some-another-folder/`, run:

```sh
java -jar <vvd-taskproducer uber jar> \
  --spring.config.import=file:my-config.yml \
  --io.output-directory=some-another-folder/
```

<!-- markdownlint-disable-next-line MD013 -->
[spring-boot-config]: https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config
