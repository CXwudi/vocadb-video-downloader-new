# Spring Boot 4.x Upgrade Plan (Issue #117)

## Summary
Upgrade the multi-module Maven build from Spring Boot 3.5.10 to the latest Spring Boot 4.0.x GA, with an early Kotest compatibility spike. If Kotest breaks, stop and resolve issue #118 first.

## Public APIs / Interfaces
No intentional public API changes. Watch for possible JSON serialization differences due to Jackson 3 changes (Spring Boot 4 uses Jackson 3 via its BOM).

## Plan Steps (each with a checkpoint)

1. Baseline and prerequisites
- Confirm Java 21 is in use and review key migration requirements (Spring Boot 4 requires Java 21 and Spring Framework 7).
- Checkpoint: `./mvnw -v` shows Java 21; review migration guide sections relevant to Spring Framework 7 changes.

2. Kotest compatibility spike (gating)
- Temporarily bump `spring-boot.version` to latest 4.0.x GA and run a minimal Kotest + Spring test to verify the SpringExtension still works.
- If it fails due to Kotest/Spring integration, stop here and proceed with #118 instead.
- Checkpoint: `./mvnw -pl vvd-downloader -Dtest=MainServiceTest test` (or another Kotest + Spring test) passes. If it fails for Kotest/Spring, stop and report.

3. Full Spring Boot 4 upgrade
- Keep the root `spring-boot.version` at 4.0.x GA.
- Ensure plugin alignment (spring-boot-maven-plugin uses the property already).
- Review 3rd-party dependency compatibility with Spring Framework 7 (springmockk, easy-batch, tika, log4j2, kotlin libs).
- Checkpoint: `./mvnw -DskipTests install` succeeds without dependency resolution errors.

4. Jackson 3 migration fixes
- Review Jackson usage (ObjectMapper, JsonAnySetter, JsonNode) and fix any behavior or API changes surfaced by compilation/tests.
- Focus on serialization/deserialization of API contract models and JSON handling in downloader/extractor/taskproducer modules.
- Checkpoint: targeted JSON-related tests pass (for example, `vvd-taskproducer` Jackson tests).

5. Config property migration
- Add `spring-boot-properties-migrator` temporarily to surface renamed/removed properties, update configs, then remove the migrator dependency once clean.
- Checkpoint: apps start without migrator warnings.

6. Full test and smoke run
- Run the full test suite across modules.
- Smoke-run the main Spring Boot apps with sample config to confirm startup and basic flows.
- Checkpoint: `./mvnw test` passes; each app starts cleanly.

## Test Cases and Scenarios
- Kotest + SpringExtension compatibility test (gating).
- Jackson-specific tests (JsonAnySetter, JsonNode mapping).
- Full module test suites for `vvd-downloader`, `vvd-extractor`, `vvd-taskproducer`.
- Application startup smoke tests for each Spring Boot app module.

## Assumptions and Defaults
- Use latest Spring Boot 4.0.x GA at implementation time.
- Java 21 is already set and will be the runtime baseline.
- Kotest is a hard gate; failure triggers a stop and work on #118 before continuing.
- No intentional public API changes; any JSON serialization changes will be treated as defects and fixed.
