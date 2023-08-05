# Common documentation for all modules

## Prerequisite

All 3 modules require the following:

1. Java 17 or above

Some modules have additional prerequisites. Please refer to the corresponding module's `README.md` for details.

## How to compile

1. Clone this repository
2. Run `./mvnw clean package -pl <the module folder> -am -DskipTests=true` to build an Uber jar for the specific module. For example, to build `vvd-taskproducer`, run `./mvnw clean package -pl vvd-taskproducer -am -DskipTests=true`. To build all modules, run `./mvnw clean package -DskipTests=true`. The meaning of the parameters are:
    - `-pl <the module folder>`: build the specific module
    - `-am`: build all dependent modules
    - `-DskipTests=true`: skip the test cases
3. The Uber jar for each module will be generated in the `target` folder in each `<the module folder>` folder. For example, the uber jar for `vvd-taskproducer` will be generated in `vvd-taskproducer/target` folder.

## How to run

The Uber jar is runnable using `java -jar <jar file>` command. However, each module requires some configurations to run.

Each module is implemented as a Spring Boot CLI application (without the web environment). The CLI part is implemented using [Spring Boot Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config).

So all available arguments/configurations are stored in the `application.yml` file in the `src/main/resources` folder. Each `application.yml` file is well documented. So feel free to check out each module's `application.yml` file and use it as a reference.

## How to specify configurations

The configuration can be specified in anyways supported by Spring Boot Configuration including but not limited to command lines, environment variables, external `application.yml`/`application.properties` files, etc. Refer to the [Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config) of Spring Boot Configuration for details.

The recommended way to run this project is to import external YAML file using `--spring.config.import=file:<relative path to your YAML file>` command line. You can first copy the `application.yml` file from the `src/main/resources` folder, modify it and then import it using the command line. For example, to run `vvd-taskproducer` with your new `my-config.yml` in the current folder, run `java -jar <vvd-taskproducer uber jar> --spring.config.import=file:my-config.yml`.

If you want, you can also temporarily override some configurations using command line arguments. For example, to run `vvd-taskproducer` with the configuration in `my-config.yml` in the current folder and override the `io.output-directory` property to `some-another-folder/`, run `java -jar <vvd-taskproducer uber jar> --spring.config.import=file:my-config.yml --io.output-directory=some-another-folder/`.
