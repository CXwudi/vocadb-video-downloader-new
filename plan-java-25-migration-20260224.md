# Java 25 Migration Plan

## Goal

Upgrade project build to Java 25 with true bytecode target 25, update CI

to use JDK 25, and validate via Docker-based tests.

## Plan

1. Update CI JDK to 25 and align any build-time Java references.
   Verification: CI workflow uses `actions/setup-java` with
   `java-version: '25'` and no remaining Java 21 references in CI config.

2. Resolve Java 25 compile breakages surfaced in Docker (currently
   Lombok getters missing in `VSongLabel`).
   Verification: `./mvnw -DskipTests=true clean verify -pl vvd-common -am`
   succeeds inside Docker.

3. Validate full build and tests using the documented Docker test flow.
   Verification: `docker compose -f "docker/docker-compose.base.yml" \
   -f "docker/docker-compose.test-all.yml" up --exit-code-from base`
   succeeds.

4. Update documentation to reflect Java 25 requirement.
   Verification: `README.md` and `doc/common part.md` state Java 25 or
   above.

5. Re-setup Lombok (option 1) with pinned version, run delombok Maven
   plugin, then remove Lombok.
   Verification: delombok output committed, Lombok dependency removed,
   and build succeeds.

## References

### Must Read

- `doc/common part.md`: Canonical Docker build/test instructions for
  verification.

### Optional Read

- `pom.xml`: Java and Kotlin target versions.
- `docker/env-setup.Dockerfile`: JDK used in Docker test image.
- `.github/workflows/test.yml`: CI JDK setup to update to 25.

## Contexts

- After switching to Java 25, Docker test compile fails in `vvd-common`
  due to missing Lombok-generated getters in:
  `vvd-common/src/main/java/mikufan/cx/vvd/common/label/VSongLabel.java`.
  Missing getters: `getPvFileName`, `getAudioFileName`,
  `getThumbnailFileName`.
- User decision: true bytecode target 25 and update CI to JDK 25.

## Status (2026-02-24)

- [x] CI updated to JDK 25.
- [x] Maven `java.version` set to 25.
- [x] Docker test image uses Temurin 25.
- [x] Delombok applied to vvd-common and Lombok removed from build.
- [x] vvd-common verified with
  `./mvnw -DskipTests=true clean verify -pl vvd-common -am`.
- [x] Docker test flow succeeded.
