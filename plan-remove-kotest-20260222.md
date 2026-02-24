# Plan: Remove Kotest (Issue #118)

## Goal

Remove Kotest from the build and migrate all affected tests to JUnit
Jupiter on Spring Boot 4.0.3, using AssertJ where available, while
preserving existing coverage and disabled tests.

## JUnit 6 Check (Spring Boot 4.0.3)

Spring Boot 4.0.3 documentation says `spring-boot-starter-test` brings
in JUnit Jupiter and AssertJ, and it references the JUnit 6 vintage
engine for running JUnit 4 tests. That indicates the Spring Boot 4.0.3
testing stack is aligned with JUnit 6 (Jupiter API), so the migration
should target JUnit Jupiter 6 APIs unless the BOM proves otherwise at
build time.

## Decisions

- Use JUnit Jupiter from Spring Boot 4.0.3 (JUnit 6 based on docs).
- Use AssertJ for assertions when it is included via
  `spring-boot-starter-test`.
- Use `@ParameterizedTest` and `@MethodSource` for simple loops.
- Use `@TestFactory` with `DynamicTest` for complex dynamic cases.
- Keep previously disabled Kotest tests disabled with JUnit `@Disabled`.

## Kotest Matcher Mapping (Global)

- `shouldBe` -> `assertThat(...).isEqualTo(...)`
- `shouldNotBe` -> `assertThat(...).isNotEqualTo(...)`
- `shouldContain` -> `assertThat(...).contains(...)`
- `shouldContainIgnoringCase` ->
  `assertThat(...).containsIgnoringCase(...)`
- `shouldContainExactlyInAnyOrder` ->
  `assertThat(...).containsExactlyInAnyOrder(...)`
- `shouldStartWith` -> `assertThat(...).startsWith(...)`
- `shouldEndWith` -> `assertThat(...).endsWith(...)`
- `shouldBeNull` -> `assertThat(...).isNull()`
- `shouldNotBeNull` -> `assertThat(...).isNotNull()`
- `shouldBeInstanceOf` -> `assertThat(...).isInstanceOf(...)`
- `beInstanceOf` -> `assertThat(...).isInstanceOf(...)`
- `shouldBeAfter` -> `assertThat(...).isAfter(...)`
- `shouldThrow<T>` -> `assertThrows<T>` or `assertThatThrownBy`
- `shouldNotThrow` / `shouldNotThrowAnyUnit` -> `assertDoesNotThrow`
- `fail(...)` -> `Assertions.fail(...)`

## Non-Test Files to Update

1. File:
  `pom.xml`
  Update:
  Remove Kotest dependencies from dependencyManagement. Keep
  `spring-boot-starter-test`.

1. File:
  `vvd-downloader/pom.xml`
  Update:
  Remove module-level Kotest dependencies.

1. File:
  `vvd-extractor/pom.xml`
  Update:
  Remove module-level Kotest dependencies.

## Test File Conversion Notes

1. File:
  `vvd-downloader/src/test/kotlin/mikufan/cx/vvd/downloader/`
  `util/SpringShouldSpec.kt`
  Update:
  Remove this Kotest base class. Tests should no longer extend it. If a
  shared base is still desired, replace it with a plain abstract class
  without Kotest and rely on `@SpringBootTest` meta-annotations for
  `SpringExtension`.

1. File:
  `vvd-extractor/src/test/kotlin/mikufan/cx/vvd/extractor/`
  `util/SpringShouldSpec.kt`
  Update:
  Same as downloader: delete or replace with a non-Kotest base and
  remove imports from all tests.

1. File:
  `vvd-downloader/src/test/kotlin/mikufan/cx/vvd/downloader/`
  `DownloaderApplicationTest.kt`
  Update:
  Convert `should("boot")` to a JUnit `@Test` method. Keep
  `@SpringBootTestWithTestProfile`. Use AssertJ if you want a basic
  assertion, otherwise keep as a smoke test.

1. File:
  `vvd-downloader/src/test/kotlin/mikufan/cx/vvd/downloader/service/`
  `MainServiceTest.kt`
  Update:
  Replace `xshould` with `@Test` and `@Disabled`. Wrap
  `mainService.run()` in `assertDoesNotThrow`.

1. File:
  `vvd-downloader/src/test/kotlin/mikufan/cx/vvd/downloader/config/`
  `enablement/EnablementConfigTest.kt`
  Update:
  Convert to JUnit `@Test` and replace
  `shouldContainExactlyInAnyOrder` with AssertJ.

1. File:
  `vvd-downloader/src/test/kotlin/mikufan/cx/vvd/downloader/config/`
  `downloader/NicoNicoYtDlConfigTest.kt`
  Update:
  Two classes. Use `@Test` methods. Replace `shouldThrow` with
  `assertThrows` or `assertThatThrownBy`.

1. File:
  `vvd-downloader/src/test/kotlin/mikufan/cx/vvd/downloader/component/`
  `BeforeProcessLabelValidatorTest.kt`
  Update:
  Convert to JUnit classes with `@Test` methods. Use the matcher mapping
  above. For the success loop, prefer `@ParameterizedTest` and
  `@MethodSource` that pre-reads the first 10 records in `@BeforeAll`
  using `@TestInstance(PER_CLASS)`.

1. File:
  `vvd-downloader/src/test/kotlin/mikufan/cx/vvd/downloader/component/`
  `LabelsReaderTest.kt`
  Update:
  Convert to a single `@Test` method. Keep the while loop and replace
  `shouldBe` with AssertJ.

1. File:
  `vvd-downloader/src/test/kotlin/mikufan/cx/vvd/downloader/component/`
  `SongInfoLoaderTest.kt`
  Update:
  Use `@ParameterizedTest` and `@MethodSource` that yields
  `(index, labelRec)` for the first 15 labels. Replace `shouldBe` and
  `shouldNotBe` with AssertJ.

1. File:
  `vvd-downloader/src/test/kotlin/mikufan/cx/vvd/downloader/component/`
  `PvTasksDeciderTest.kt`
  Update:
  Use `@TestFactory` dynamic tests for per-song assertions. Build the
  `testList` once with `@TestInstance(PER_CLASS)`. Replace `shouldBe`
  with AssertJ `isEqualTo` or `isZero`.

1. File:
  `vvd-downloader/src/test/kotlin/mikufan/cx/vvd/downloader/component/`
  `MimeTypeDetectionPoc.kt`
  Update:
  Keep the class `@Disabled`. Replace `xcontext` and `should` blocks
  with `@Test` methods or `@TestFactory`. Use AssertJ `isNotEqualTo`.

1. File:
  `vvd-downloader/src/test/kotlin/mikufan/cx/vvd/downloader/component/`
  `downloader/base/BaseDownloaderTest.kt`
  Update:
  Convert `context` blocks into `@Test` methods. Replace Kotest matchers
  using the mapping and replace `fail` with `Assertions.fail`.

1. File:
  `vvd-downloader/src/test/kotlin/mikufan/cx/vvd/downloader/component/`
  `downloader/base/BaseCliDownloaderTest.kt`
  Update:
  Use `@BeforeAll` to copy test files once. Convert the loop to a
  `@ParameterizedTest` with `@ValueSource`. Replace `shouldContain` with
  AssertJ and keep file existence checks with AssertJ or `assertTrue`.

1. File:
  `vvd-downloader/src/test/kotlin/mikufan/cx/vvd/downloader/component/`
  `downloader/base/BaseYtDlDownloaderTest.kt`
  Update:
  Keep class `@Disabled`. Replace `xcontext` with `@Test` methods and
  use AssertJ `assertThat(result.isSuccess).isTrue()`.

1. File:
  `vvd-extractor/src/test/kotlin/mikufan/cx/vvd/extractor/`
  `ExtractorApplicationTest.kt`
  Update:
  Convert to `@Test` method and remove unused Kotest imports. Keep
  `@SpringBootTestWithTestProfile`.

1. File:
  `vvd-extractor/src/test/kotlin/mikufan/cx/vvd/extractor/service/`
  `MainServiceTest.kt`
  Update:
  Replace `xshould` with `@Test` and `@Disabled`. Wrap
  `mainService.run()` in `assertDoesNotThrow`.

1. File:
  `vvd-extractor/src/test/kotlin/mikufan/cx/vvd/extractor/component/`
  `BeforeProcessLabelValidatorTest.kt`
  Update:
  Two classes. Convert to `@Test` methods. Replace `shouldNotThrow` with
  `assertDoesNotThrow` and `shouldThrow` with `assertThrows` or
  `assertThatThrownBy`, then assert the message with
  `containsIgnoringCase`.

1. File:
  `vvd-extractor/src/test/kotlin/mikufan/cx/vvd/extractor/component/`
  `util/MediaFormatCheckerTest.kt`
  Update:
  Convert each list of test cases to `@ParameterizedTest` and
  `@MethodSource` returning `Arguments`. Replace `shouldBe` with AssertJ.

1. File:
  `vvd-extractor/src/test/kotlin/mikufan/cx/vvd/extractor/component/`
  `TagRunnerCoreTest.kt`
  Update:
  Convert to JUnit `@Test` methods. Use `assertDoesNotThrow` for success
  and `assertThatThrownBy` or `assertThrows` for failure.

1. File:
  `vvd-extractor/src/test/kotlin/mikufan/cx/vvd/extractor/component/`
  `TaggerDeciderCoreTest.kt`
  Update:
  Convert each `should` to `@Test`. Replace `shouldBeInstanceOf` with
  `assertThat(...).isInstanceOf(...)` and use AssertJ for exceptions.

1. File:
  `vvd-extractor/src/test/kotlin/mikufan/cx/vvd/extractor/component/`
  `ExtractorDeciderCoreTest.kt`
  Update:
  Convert to `@Test` methods. Replace `shouldBeNull` and
  `shouldNotBeNull` with AssertJ `isNull` and `isNotNull`.

1. File:
  `vvd-extractor/src/test/kotlin/mikufan/cx/vvd/extractor/component/`
  `FinalRenamerTest.kt`
  Update:
  Convert to `@Test` methods. Replace `shouldBe` with AssertJ
  `isEqualTo`.

1. File:
  `vvd-extractor/src/test/kotlin/mikufan/cx/vvd/extractor/component/`
  `LabelsReaderTest.kt`
  Update:
  Convert to `@Test` method and replace `shouldBe` with
  `assertThat(records).hasSize(5)`.

1. File:
  `vvd-extractor/src/test/kotlin/mikufan/cx/vvd/extractor/component/`
  `LastModifiedChangerTest.kt`
  Update:
  Convert to `@Test` method and replace `shouldBeAfter` with AssertJ
  `isAfter`.

1. File:
  `vvd-extractor/src/test/kotlin/mikufan/cx/vvd/extractor/component/`
  `SongInfoLoaderTest.kt`
  Update:
  Use `@ParameterizedTest` and `@MethodSource` that yields
  `(index, labelRec)` for the first 3 labels. Replace `shouldBe` and
  `shouldNotBe` with AssertJ.

1. File:
  `vvd-extractor/src/test/kotlin/mikufan/cx/vvd/extractor/component/`
  `extractor/impl/AudioExtractorImplTest.kt`
  Update:
  Prefer three simple `@Test` methods. Replace `shouldBe` with AssertJ
  `assertThat(result.isSuccess).isTrue()`.

1. File:
  `vvd-extractor/src/test/kotlin/mikufan/cx/vvd/extractor/component/`
  `extractor/base/BaseCliAudioExtractorTest.kt`
  Update:
  Use `@BeforeAll` to copy test files once. Convert the loop to a
  `@ParameterizedTest`. Replace `shouldStartWith` with AssertJ
  `startsWith`.

1. File:
  `vvd-extractor/src/test/kotlin/mikufan/cx/vvd/extractor/component/`
  `extractor/base/BaseAudioExtractorTest.kt`
  Update:
  Convert `context` blocks into `@Test` methods. Replace `shouldEndWith`
  with AssertJ `endsWith` and `should` matcher with
  `assertThat(exception).isInstanceOf(...)`.

1. File:
  `vvd-extractor/src/test/kotlin/mikufan/cx/vvd/extractor/component/`
  `tagger/base/AudioTaggerImplTest.kt`
  Update:
  Convert each `should` into `@Test` methods or a single
  `@ParameterizedTest` table. Replace `shouldBe` with AssertJ.

## Step-by-Step Plan

1. Confirm the Spring Boot 4.0.3 testing stack baseline and align on
  JUnit Jupiter 6 and AssertJ usage.

Verification: Re-check `spring-boot-starter-test` documentation and
confirm JUnit Jupiter and AssertJ are present.

1. Convert all `vvd-downloader` tests listed above from Kotest to JUnit,
  including removal of `SpringShouldSpec` and replacement of matchers.

Verification: Run `mvn -pl vvd-downloader -DskipTests=false test` or a
subset with `-Dtest=...` if environment-sensitive tests are disabled.

1. Convert all `vvd-extractor` tests listed above from Kotest to JUnit,
  including removal of `SpringShouldSpec` and replacement of matchers.

Verification: Run `mvn -pl vvd-extractor -DskipTests=false test` or a
subset with `-Dtest=...` if environment-sensitive tests are disabled.

1. Remove Kotest dependencies from `pom.xml`, `vvd-downloader/pom.xml`,
  and `vvd-extractor/pom.xml` once no tests reference Kotest APIs.

Verification: `rg -n "kotest"` should return no matches.

1. Run a full test pass or CI-equivalent command to ensure no JUnit 6
  deprecation warnings remain.

Verification: `mvn test` and review output for deprecation warnings.

## Open Questions and Assumptions

- Assumption: The Spring Boot 4.0.3 BOM uses JUnit 6 (Jupiter API). If a
  different JUnit platform version is pinned, adjust APIs accordingly.
- Assumption: Test resources referenced in existing tests remain
  unchanged, so data-driven expectations (counts, file names) still
  hold.

## References

### Must Read

| File | Why |
| --- | --- |
| `pom.xml` | Kotest removal and test stack |
| `vvd-downloader/pom.xml` | Remove Kotest deps |
| `vvd-extractor/pom.xml` | Remove Kotest deps |

### Optional Read

| File | Why |
| --- | --- |
| None | Not required |

## Contexts

Issue #118 requests removing Kotest. Spring Boot 4.0.3 docs indicate
`spring-boot-starter-test` includes JUnit Jupiter and AssertJ, and it
references JUnit 6 vintage support, so the migration targets JUnit
Jupiter 6 and AssertJ. Disabled Kotest tests remain disabled after
conversion.
