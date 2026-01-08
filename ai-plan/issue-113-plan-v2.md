# Plan: Remove vocadb-openapi-client-java (Issue #113)

## Overview

Replace the auto-generated `vocadb-openapi-client-java` dependency with:
1. Spring's native `RestClient`
2. Custom Kotlin data classes with only necessary fields
3. `@JsonAnyGetter`/`@JsonAnySetter` with `Map<String, JsonNode>` for unknown fields

## Phase 1: Create Custom Data Classes in vvd-commonkt

### 1.1 Create Enums

**File:** `vvd-commonkt/src/main/kotlin/mikufan/cx/vvd/commonkt/vocadb/api/model/Enums.kt`

```kotlin
// PVService enum - replaces both PVService and PVServices.Constant
enum class PVService(@get:JsonValue val value: String) {
  NICONICODOUGA("NicoNicoDouga"),
  YOUTUBE("Youtube"),
  SOUNDCLOUD("SoundCloud"),
  VIMEO("Vimeo"),
  PIAPRO("Piapro"),
  BILIBILI("Bilibili"),
  FILE("File"),
  LOCALFILE("LocalFile"),
  CREOFUGA("Creofuga"),
  BANDCAMP("Bandcamp");

  companion object {
    @JvmStatic @JsonCreator
    fun fromValue(value: String): PVService = entries.first { it.value.equals(value, ignoreCase = true) }
  }
}

// PVType enum
enum class PVType(@get:JsonValue val value: String) {
  ORIGINAL("Original"),
  REPRINT("Reprint"),
  OTHER("Other");

  companion object {
    @JvmStatic @JsonCreator
    fun fromValue(value: String): PVType = entries.first { it.value.equals(value, ignoreCase = true) }
  }
}

// ArtistCategories - wrapper for Set<Constant> with comma-separated parsing
class ArtistCategories(val enums: Set<Constant>) {
  enum class Constant(@get:JsonValue val value: String) {
    NOTHING("Nothing"), VOCALIST("Vocalist"), PRODUCER("Producer"),
    ANIMATOR("Animator"), LABEL("Label"), CIRCLE("Circle"),
    OTHER("Other"), BAND("Band"), ILLUSTRATOR("Illustrator"), SUBJECT("Subject");
  }

  companion object {
    @JvmStatic @JsonCreator
    fun fromValue(value: String): ArtistCategories = TODO()// parse comma-separated
  }

  @JsonValue
  override fun toString(): String = enums.joinToString(",") { it.value }
}

// SongOptionalFields - for API query parameters
enum class SongOptionalFields(val value: String) {
  ALBUMS("Albums"), ARTISTS("Artists"), PVS("PVs"), TAGS("Tags"), WEBLINKS("WebLinks");

  companion object {
    fun of(vararg fields: SongOptionalFields): String = fields.joinToString(",") { it.value }
  }
}
```

### 1.2 Create Model Classes

**File:** `vvd-commonkt/src/main/kotlin/mikufan/cx/vvd/commonkt/vocadb/api/model/PVContract.kt`

Key fields: `id`, `service`, `pvId`, `pvType`, `url`, `name`, `author`, `thumbUrl`, `length`, `publishDate`, `disabled`

**File:** `vvd-commonkt/src/main/kotlin/mikufan/cx/vvd/commonkt/vocadb/api/model/SongForApiContract.kt`

Key fields: `id`, `name`, `defaultName`, `artistString`, `pvs`, `albums`, `artists`, `publishDate`, `songType`, `status`

**File:** `vvd-commonkt/src/main/kotlin/mikufan/cx/vvd/commonkt/vocadb/api/model/ArtistForSongContract.kt`

Key fields: `name`, `categories`

**File:** `vvd-commonkt/src/main/kotlin/mikufan/cx/vvd/commonkt/vocadb/api/model/AlbumForApiContract.kt`

Key fields: `id`, `name` (minimal, only used in Albums optional field)

**File:** `vvd-commonkt/src/main/kotlin/mikufan/cx/vvd/commonkt/vocadb/api/model/ApiResponses.kt`

- `PartialFindResult<T>` - wrapper with `items` and `totalCount`
- `SongInListForApiContract` - wrapper with `order` and `song`

All model classes will use `@param:JsonAnySetter` in the constructor for effective immutability (supported in Jackson 2.18.1+, Spring Boot 3.5 uses Jackson 2.19.x):

```kotlin
data class SongForApiContract(
  val id: Int,
  val name: String? = null,
  val defaultName: String? = null,
  val artistString: String? = null,
  val pvs: List<PVContract>? = null,
  val albums: List<AlbumForApiContract>? = null,
  val artists: List<ArtistForSongContract>? = null,
  val publishDate: LocalDateTime? = null,
  val songType: String? = null,
  val status: String? = null,
  // Unknown fields captured here
  @param:JsonAnySetter
  @get:JsonAnyGetter
  val additionalProperties: Map<String, JsonNode> = emptyMap()
)
```

**Pattern explanation:**
- Use `@param:JsonAnySetter` (not `@JsonAnySetter`) to explicitly target the constructor parameter in Kotlin
- Default is `emptyMap()` - Jackson-kotlin-module creates a **new** `LinkedHashMap` during deserialization regardless of the default value
- `@get:JsonAnyGetter` ensures unknown fields are serialized back to JSON
- When constructing manually (not via Jackson), `emptyMap()` is used as expected
- This pattern is verified working in Jackson 2.18.1+ (see [jackson-module-kotlin#832](https://github.com/FasterXML/jackson-module-kotlin/issues/832))
- **Verified locally** in `JsonAnySetterOnConstructorTest.kt` - both `emptyMap()` and `mutableMapOf()` work, but `emptyMap()` is cleaner

## Phase 2: Create VocaDB RestClient

**File:** `vvd-commonkt/src/main/kotlin/mikufan/cx/vvd/commonkt/vocadb/api/VocaDbClient.kt`

```kotlin
interface VocaDbClient {
  fun getSongListSongs(listId: Int, start: Int, maxResults: Int, getTotalCount: Boolean, fields: String?): PartialFindResult<SongInListForApiContract>
  fun getSongById(id: Int, fields: String?): SongForApiContract
}

class VocaDbRestClient(private val restClient: RestClient) : VocaDbClient {
  // Implementation using Spring RestClient
}
```

**File:** `vvd-commonkt/src/main/kotlin/mikufan/cx/vvd/commonkt/vocadb/api/VocaDbClientConfig.kt`

Spring configuration to create `VocaDbClient` bean with configurable `baseUrl` and `userAgent`.

## Phase 3: Update vvd-commonkt

### 3.1 Replace PVServicesAndPVServiceConvertion.kt

**File:** `vvd-commonkt/src/main/kotlin/mikufan/cx/vvd/commonkt/vocadb/PVServicesAndPVServiceConvertion.kt`

Replace with type alias for backward compatibility:
```kotlin
typealias PVServicesEnum = PVService  // Now unified
```

### 3.2 Update FileNameUtil.kt

Change import from `mikufan.cx.vocadbapiclient.model.SongForApiContract` to new package.

## Phase 4: Update vvd-taskproducer

### 4.1 Replace ApiConfig.kt

Remove `SongApi`, `SongListApi`, `ApiClient` beans. Import `VocaDbClientConfig`.

### 4.2 Update ListReader.kt

- Change from `SongListApi.apiSongListsListIdSongsGet()` to `VocaDbClient.getSongListSongs()`
- Update model imports

### 4.3 Update ArtistFieldFixer.kt

- Change from `SongApi.apiSongsIdGet()` to `VocaDbClient.getSongById()`
- Update model imports

### 4.4 Update Models.kt

Update `SongForApiContract` import.

## Phase 5: Update vvd-downloader

Update imports in these files:
- `Models.kt` - SongForApiContract
- `SongInfoLoader.kt` - SongForApiContract
- `PvTasksDecider.kt` - PVContract, PVService, PVType
- `BaseDownloader.kt` - PVContract
- `SupportedPvServiceValidation.kt` - PVService (was PVServices.Constant)
- `Preference.kt`, `Enablement.kt` - PVServicesEnum

## Phase 6: Update vvd-extractor

Update imports in:
- `Models.kt`
- `SongInfoLoader.kt`
- `FinalRenamer.kt`

## Phase 7: Update pom.xml Files

### 7.1 Parent pom.xml

Remove from `<dependencyManagement>`:
```xml
<dependency>
  <groupId>com.github.VocaDB</groupId>
  <artifactId>vocadb-openapi-client-java</artifactId>
  <version>1.2.2</version>
</dependency>
```

### 7.2 vvd-common/pom.xml

Remove dependency declaration.

### 7.3 vvd-commonkt/pom.xml

Add if not present:
```xml
<dependency>
  <groupId>com.fasterxml.jackson.core</groupId>
  <artifactId>jackson-databind</artifactId>
</dependency>
```

## Phase 8: Update Tests

Update imports in 30+ test files across all modules. JSON test fixtures should remain compatible due to `@JsonIgnoreProperties(ignoreUnknown = true)` and `additionalProperties`.

## Critical Files to Modify

| Module | File | Change |
|--------|------|--------|
| vvd-commonkt | `vocadb/api/model/*.kt` | **NEW** - Create data classes |
| vvd-commonkt | `vocadb/api/VocaDbClient.kt` | **NEW** - Create REST client |
| vvd-commonkt | `vocadb/PVServicesAndPVServiceConvertion.kt` | Simplify to type alias |
| vvd-commonkt | `naming/FileNameUtil.kt` | Update imports |
| vvd-taskproducer | `config/ApiConfig.kt` | Replace with VocaDbClient |
| vvd-taskproducer | `component/ListReader.kt` | Use new client |
| vvd-taskproducer | `component/ArtistFieldFixer.kt` | Use new client |
| vvd-downloader | `component/PvTasksDecider.kt` | Update model imports |
| vvd-downloader | `config/preference/*.kt` | Update enum imports |
| Parent | `pom.xml` | Remove dependency |
| vvd-common | `pom.xml` | Remove dependency |

## Verification

1. **Build:** `mvn clean compile` - Ensure all modules compile
2. **Tests:** `mvn test` - Run all unit tests
3. **Integration:** Run vvd-taskproducer against a real VocaDB list to verify API calls work
4. **JSON Compatibility:** Verify existing JSON test fixtures deserialize correctly into new models

## Migration Order

1. Create new classes in vvd-commonkt (non-breaking)
2. Update vvd-taskproducer (the only API caller)
3. Update vvd-downloader model usages
4. Update vvd-extractor model usages
5. Remove old dependency from pom.xml files
6. Delete api-client-to-remove directory
7. Run full test suite