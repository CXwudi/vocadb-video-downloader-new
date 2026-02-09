# Plan: Remove vocadb-openapi-client-java (Issue #113) - Updated

## Decisions (from user)

1) Refactor call sites to immutability. New Kotlin models will be immutable and updated via `copy()`; `Parameters` classes will keep `var` to store new instances.
2) Unify `PVServicesEnum` and `PVService` into a single enum that includes `NOTHING` (valid VocaDB value). Remove conversion helpers.

## Overview

Replace the generated `vocadb-openapi-client-java` dependency with:
1) Spring `RestClient` in `vvd-taskproducer`
2) Minimal custom Kotlin models with only required fields in `vvd-commonkt`
3) `@JsonAnyGetter` / `@param:JsonAnySetter` to capture unknown fields (because `fail-on-unknown-properties: true`)
4) `@JsonSetter(nulls = Nulls.AS_EMPTY)` for list fields that may be `null` in fixtures (e.g., `pvs`, `albums`, `artists`, and list results)

## Phase 1: New Models (vvd-commonkt)

### 1.1 Enums (unified)

**File:** `vvd-commonkt/src/main/kotlin/mikufan/cx/vvd/commonkt/vocadb/api/model/Enums.kt`

- `PVService` with `NOTHING`, plus all values used by VocaDB.
- `PVType` (Original/Reprint/Other).
- `ArtistCategories` wrapper (parses comma-separated string into `Set<Constant>`).
- `SongOptionalFields` helper with `of(vararg)` for query params.

Notes:
- Use `@JsonCreator` and `@JsonValue` for proper serialization.
- `NOTHING` must be accepted and serialized as `"Nothing"`.

### 1.2 Immutable Data Classes

**Files (new):**
- `vvd-commonkt/src/main/kotlin/mikufan/cx/vvd/commonkt/vocadb/api/model/PVContract.kt`
- `vvd-commonkt/src/main/kotlin/mikufan/cx/vvd/commonkt/vocadb/api/model/SongForApiContract.kt`
- `vvd-commonkt/src/main/kotlin/mikufan/cx/vvd/commonkt/vocadb/api/model/ArtistForSongContract.kt`
- `vvd-commonkt/src/main/kotlin/mikufan/cx/vvd/commonkt/vocadb/api/model/AlbumForApiContract.kt`
- `vvd-commonkt/src/main/kotlin/mikufan/cx/vvd/commonkt/vocadb/api/model/ApiResponses.kt`

Design:
- `data class` with `val` fields, default values, and `copy()` for updates.
- Prefer nullable fields with defaults to preserve old behavior (existing tests create empty instances).
- Add `@JsonSetter(nulls = Nulls.AS_EMPTY)` for list fields where stored JSON may use explicit `null`.
- Capture unknown fields:
  ```kotlin
  data class SongForApiContract(
    val id: Int? = null,
    val name: String? = null,
    val defaultName: String? = null,
    val artistString: String? = null,
    val pvs: List<PVContract> = emptyList(),
    val albums: List<AlbumForApiContract> = emptyList(),
    val artists: List<ArtistForSongContract> = emptyList(),
    val publishDate: LocalDateTime? = null,
    val songType: String? = null,
    val status: String? = null,
    @param:JsonAnySetter
    @get:JsonAnyGetter
    val additionalProperties: Map<String, JsonNode> = emptyMap()
  )
  ```

### 1.3 Compatibility Notes for Immutability

- Replace all property mutations like `song.artists = ...` with `song.copy(...)`.
- `Parameters` classes will store the updated instance in `var songForApiContract`.

## Phase 2: VocaDB RestClient (vvd-taskproducer)

**Files (new):**
- `vvd-taskproducer/src/main/kotlin/mikufan/cx/vvd/taskproducer/config/VocaDbClient.kt`
- `vvd-taskproducer/src/main/kotlin/mikufan/cx/vvd/taskproducer/config/VocaDbClientConfig.kt`

Endpoints needed:
- `GET /api/songLists/{listId}/songs`
- `GET /api/songs/{id}`

Notes:
- Configure base URL and user agent from `SystemConfig`.
- Use `RestClient` from `spring-web`.

## Phase 3: Replace PVServices Conversion (do with module call-site updates)

**File:** `vvd-commonkt/src/main/kotlin/mikufan/cx/vvd/commonkt/vocadb/PVServicesAndPVServiceConvertion.kt`

- Replace with:
  ```kotlin
  typealias PVServicesEnum = PVService
  ```
- Remove `toPVService()` / `toPVServicesEnum()` and update call sites to use `PVService` directly.
- Do this together with each module’s call-site updates to keep compilation green.

## Phase 4: Update vvd-taskproducer

### 4.1 ApiConfig

**File:** `vvd-taskproducer/src/main/kotlin/mikufan/cx/vvd/taskproducer/config/ApiConfig.kt`

- Remove `ApiClient`, `SongApi`, `SongListApi` beans.
- Import `VocaDbClientConfig` (or define a `VocaDbClient` bean that uses it).

### 4.2 ListReader

**File:** `vvd-taskproducer/src/main/kotlin/mikufan/cx/vvd/taskproducer/component/ListReader.kt`

- Replace `SongListApi.apiSongListsListIdSongsGet()` with `VocaDbClient.getSongListSongs()`.
- Build optional fields via `SongOptionalFields.of(...)`.

### 4.3 ArtistFieldFixer

**File:** `vvd-taskproducer/src/main/kotlin/mikufan/cx/vvd/taskproducer/component/ArtistFieldFixer.kt`

- Replace `SongApi.apiSongsIdGet()` with `VocaDbClient.getSongById()`.
- Update to immutable updates:
  - `song = song.copy(artists = artists, artistString = newArtistStr)`
  - Assign back to `record.payload.parameters.songForApiContract`.

### 4.4 Models

**File:** `vvd-taskproducer/src/main/kotlin/mikufan/cx/vvd/taskproducer/model/Models.kt`

- Update import to new `SongForApiContract`.

## Phase 5: Update vvd-downloader

### 5.1 Models and Components

**Files:**
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/model/Models.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/component/SongInfoLoader.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/component/PvTasksDecider.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/component/DownloadManager.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/component/downloader/base/BaseDownloader.kt`

Adjust for new model package and immutable types (no property mutation).

### 5.2 Config and Validation

**Files:**
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/config/preference/SupportedPvServiceValidation.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/config/preference/Preference.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/config/enablement/Enablement.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/component/downloader/EnabledDownloaders.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/config/downloader/DownloaderBaseCondition.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/config/downloader/NicoNicoConditions.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/config/downloader/YoutubeConditions.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/config/downloader/BilibiliConditions.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/config/downloader/SoundCloudConditions.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/component/downloader/implementation/NicoNicoYtDlDownloader.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/component/downloader/implementation/YoutubeYtDlDownloader.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/component/downloader/implementation/BilibiliYtDlDownloader.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/component/downloader/implementation/SoundCloudYtDlDownloader.kt`

Use unified `PVService` (with `NOTHING`) everywhere.

## Phase 6: Update vvd-extractor

**Files:**
- `vvd-extractor/src/main/kotlin/mikufan/cx/vvd/extractor/model/Models.kt`
- `vvd-extractor/src/main/kotlin/mikufan/cx/vvd/extractor/component/SongInfoLoader.kt`
- `vvd-extractor/src/main/kotlin/mikufan/cx/vvd/extractor/component/FinalRenamer.kt`
- `vvd-extractor/src/main/kotlin/mikufan/cx/vvd/extractor/component/tagger/impl/MkaAudioTagger.kt`

Adjust for new model package, and ensure null safety on fields used by Mka tagger.

## Phase 7: Dependencies

### 7.1 Parent

**File:** `pom.xml`

- Remove `com.github.VocaDB:vocadb-openapi-client-java` from dependencyManagement.

### 7.2 vvd-common

**File:** `vvd-common/pom.xml`

- Remove `vocadb-openapi-client-java` dependency.

### 7.3 vvd-commonkt

**File:** `vvd-commonkt/pom.xml`

- Add:
  - `org.springframework.boot:spring-boot-starter-json`
- Remove:
  - `org.springframework:spring-web`

### 7.4 vvd-taskproducer

**File:** `vvd-taskproducer/pom.xml`

- Add `org.springframework:spring-web` for `RestClient`.

## Phase 8: Tests

Update imports and immutable construction in all tests that use the old models:

**vvd-taskproducer tests**
- `vvd-taskproducer/src/test/kotlin/mikufan/cx/vvd/taskproducer/config/ApiConfigTest.kt`
- `vvd-taskproducer/src/test/kotlin/mikufan/cx/vvd/taskproducer/component/BeforeWriteValidatorTest.kt`
- `vvd-taskproducer/src/test/kotlin/mikufan/cx/vvd/taskproducer/component/LabelSaverTest.kt`
- `vvd-taskproducer/src/test/kotlin/mikufan/cx/vvd/taskproducer/component/ArtistFieldFixerTest.kt`
- `vvd-taskproducer/src/test/kotlin/mikufan/cx/vvd/taskproducer/component/ErrorRecordTest.kt`

**vvd-downloader tests**
- `vvd-downloader/src/test/kotlin/mikufan/cx/vvd/downloader/component/PvTasksDeciderTest.kt`
- `vvd-downloader/src/test/kotlin/mikufan/cx/vvd/downloader/component/downloader/base/BaseDownloaderTest.kt`
- `vvd-downloader/src/test/kotlin/mikufan/cx/vvd/downloader/component/downloader/base/BaseYtDlDownloaderTest.kt`
- `vvd-downloader/src/test/kotlin/mikufan/cx/vvd/downloader/component/downloader/base/BaseCliDownloaderTest.kt`
- `vvd-downloader/src/test/kotlin/mikufan/cx/vvd/downloader/config/enablement/EnablementConfigTest.kt`

**vvd-extractor tests**
- `vvd-extractor/src/test/kotlin/mikufan/cx/vvd/extractor/component/FinalRenamerTest.kt`
- `vvd-extractor/src/test/kotlin/mikufan/cx/vvd/extractor/component/extractor/impl/AudioExtractorImplTest.kt`
- `vvd-extractor/src/test/kotlin/mikufan/cx/vvd/extractor/component/tagger/base/AudioTaggerImplTest.kt`
- `vvd-extractor/src/test/kotlin/mikufan/cx/vvd/extractor/component/extractor/base/BaseAudioExtractorTest.kt`

Use constructors + `copy()` instead of mutable setters.

## Phase 9: Cleanup

- Remove `api-client-to-remove/` after migration succeeds.

## Complete File List (every file requiring modification)

**New files**
- `vvd-commonkt/src/main/kotlin/mikufan/cx/vvd/commonkt/vocadb/api/model/Enums.kt`
- `vvd-commonkt/src/main/kotlin/mikufan/cx/vvd/commonkt/vocadb/api/model/PVContract.kt`
- `vvd-commonkt/src/main/kotlin/mikufan/cx/vvd/commonkt/vocadb/api/model/SongForApiContract.kt`
- `vvd-commonkt/src/main/kotlin/mikufan/cx/vvd/commonkt/vocadb/api/model/ArtistForSongContract.kt`
- `vvd-commonkt/src/main/kotlin/mikufan/cx/vvd/commonkt/vocadb/api/model/AlbumForApiContract.kt`
- `vvd-commonkt/src/main/kotlin/mikufan/cx/vvd/commonkt/vocadb/api/model/ApiResponses.kt`
- `vvd-taskproducer/src/main/kotlin/mikufan/cx/vvd/taskproducer/config/VocaDbClient.kt`
- `vvd-taskproducer/src/main/kotlin/mikufan/cx/vvd/taskproducer/config/VocaDbClientConfig.kt`

**Modified files**
- `pom.xml`
- `vvd-common/pom.xml`
- `vvd-commonkt/pom.xml`
- `vvd-taskproducer/pom.xml`

- `vvd-commonkt/src/main/kotlin/mikufan/cx/vvd/commonkt/vocadb/PVServicesAndPVServiceConvertion.kt`
- `vvd-commonkt/src/main/kotlin/mikufan/cx/vvd/commonkt/naming/FileNameUtil.kt`

- `vvd-taskproducer/src/main/kotlin/mikufan/cx/vvd/taskproducer/config/ApiConfig.kt`
- `vvd-taskproducer/src/main/kotlin/mikufan/cx/vvd/taskproducer/component/ListReader.kt`
- `vvd-taskproducer/src/main/kotlin/mikufan/cx/vvd/taskproducer/component/ArtistFieldFixer.kt`
- `vvd-taskproducer/src/main/kotlin/mikufan/cx/vvd/taskproducer/model/Models.kt`

- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/model/Models.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/component/SongInfoLoader.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/component/PvTasksDecider.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/component/DownloadManager.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/component/downloader/base/BaseDownloader.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/config/preference/SupportedPvServiceValidation.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/config/preference/Preference.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/config/enablement/Enablement.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/component/downloader/EnabledDownloaders.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/config/downloader/DownloaderBaseCondition.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/config/downloader/NicoNicoConditions.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/config/downloader/YoutubeConditions.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/config/downloader/BilibiliConditions.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/config/downloader/SoundCloudConditions.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/component/downloader/implementation/NicoNicoYtDlDownloader.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/component/downloader/implementation/YoutubeYtDlDownloader.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/component/downloader/implementation/BilibiliYtDlDownloader.kt`
- `vvd-downloader/src/main/kotlin/mikufan/cx/vvd/downloader/component/downloader/implementation/SoundCloudYtDlDownloader.kt`

- `vvd-extractor/src/main/kotlin/mikufan/cx/vvd/extractor/model/Models.kt`
- `vvd-extractor/src/main/kotlin/mikufan/cx/vvd/extractor/component/SongInfoLoader.kt`
- `vvd-extractor/src/main/kotlin/mikufan/cx/vvd/extractor/component/FinalRenamer.kt`
- `vvd-extractor/src/main/kotlin/mikufan/cx/vvd/extractor/component/tagger/impl/MkaAudioTagger.kt`

- `vvd-taskproducer/src/test/kotlin/mikufan/cx/vvd/taskproducer/config/ApiConfigTest.kt`
- `vvd-taskproducer/src/test/kotlin/mikufan/cx/vvd/taskproducer/component/BeforeWriteValidatorTest.kt`
- `vvd-taskproducer/src/test/kotlin/mikufan/cx/vvd/taskproducer/component/LabelSaverTest.kt`
- `vvd-taskproducer/src/test/kotlin/mikufan/cx/vvd/taskproducer/component/ArtistFieldFixerTest.kt`
- `vvd-taskproducer/src/test/kotlin/mikufan/cx/vvd/taskproducer/component/ErrorRecordTest.kt`

- `vvd-downloader/src/test/kotlin/mikufan/cx/vvd/downloader/component/PvTasksDeciderTest.kt`
- `vvd-downloader/src/test/kotlin/mikufan/cx/vvd/downloader/component/downloader/base/BaseDownloaderTest.kt`
- `vvd-downloader/src/test/kotlin/mikufan/cx/vvd/downloader/component/downloader/base/BaseYtDlDownloaderTest.kt`
- `vvd-downloader/src/test/kotlin/mikufan/cx/vvd/downloader/component/downloader/base/BaseCliDownloaderTest.kt`
- `vvd-downloader/src/test/kotlin/mikufan/cx/vvd/downloader/config/enablement/EnablementConfigTest.kt`

- `vvd-extractor/src/test/kotlin/mikufan/cx/vvd/extractor/component/FinalRenamerTest.kt`
- `vvd-extractor/src/test/kotlin/mikufan/cx/vvd/extractor/component/extractor/impl/AudioExtractorImplTest.kt`
- `vvd-extractor/src/test/kotlin/mikufan/cx/vvd/extractor/component/tagger/base/AudioTaggerImplTest.kt`
- `vvd-extractor/src/test/kotlin/mikufan/cx/vvd/extractor/component/extractor/base/BaseAudioExtractorTest.kt`

## Verification

1) `mvn clean compile`
2) `mvn test`
3) Run vvd-taskproducer against a real VocaDB list to verify API calls
4) Ensure JSON fixtures still deserialize without `UnrecognizedPropertyException`
