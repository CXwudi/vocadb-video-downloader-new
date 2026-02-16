package mikufan.cx.vvd.commonkt.vocadb.api.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

/**
 * PV services supported by VocaDB.
 */
enum class PVService(@get:JsonValue val value: String) {
  NOTHING("Nothing"),
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

  override fun toString(): String = value

  companion object {
    /**
     * Parse PV service from VocaDB value.
     */
    @JvmStatic
    @JsonCreator
    fun fromValue(value: String): PVService {
      return entries.firstOrNull { it.value == value }
        ?: throw IllegalArgumentException("Unexpected value '$value'")
    }
  }
}

/**
 * PV type classification returned by VocaDB.
 */
enum class PVType(@get:JsonValue val value: String) {
  ORIGINAL("Original"),
  REPRINT("Reprint"),
  OTHER("Other");

  override fun toString(): String = value

  companion object {
    /**
     * Parse PV type from VocaDB value.
     */
    @JvmStatic
    @JsonCreator
    fun fromValue(value: String): PVType {
      return entries.firstOrNull { it.value == value }
        ?: throw IllegalArgumentException("Unexpected value '$value'")
    }
  }
}

/**
 * Artist category set returned by VocaDB.
 */
data class ArtistCategories(
  val enums: Set<Constant> = emptySet()
) {

  /**
   * Artist category values used by VocaDB.
   */
  enum class Constant(val value: String) {
    NOTHING("Nothing"),
    VOCALIST("Vocalist"),
    PRODUCER("Producer"),
    ANIMATOR("Animator"),
    LABEL("Label"),
    CIRCLE("Circle"),
    OTHER("Other"),
    BAND("Band"),
    ILLUSTRATOR("Illustrator"),
    SUBJECT("Subject")
  }

  /**
   * Build from raw VocaDB values (comma separated).
   */
  constructor(vararg constantNames: String) : this(parseConstants(constantNames))

  /**
   * Build from enum constants.
   */
  constructor(vararg constants: Constant?) : this(constants.filterNotNull().toSet())

  /**
   * Serialize to VocaDB value string (null when empty to omit optional fields).
   */
  @JsonValue
  fun toJson(): String? {
    return toCommaSeparated(enums) { it.value }
  }

  override fun toString(): String = toJson().orEmpty()

  companion object {
    /**
     * Parse from JSON value.
     */
    @JvmStatic
    @JsonCreator
    fun fromValue(value: String?): ArtistCategories? {
      if (value.isNullOrBlank()) {
        return null
      }
      return ArtistCategories(value)
    }

    private fun parseConstants(constantNames: Array<out String?>): Set<Constant> {
      return parseCommaSeparated(constantNames, Constant.entries.associateBy { it.value }, "ArtistCategories")
    }
  }
}

/**
 * Optional fields for VocaDB song queries.
 */
data class SongOptionalFields(
  val enums: Set<Constant> = emptySet()
) {

  /**
   * Optional field values used by VocaDB.
   */
  enum class Constant(val value: String) {
    NONE("None"),
    ADDITIONALNAMES("AdditionalNames"),
    ALBUMS("Albums"),
    ARTISTS("Artists"),
    LYRICS("Lyrics"),
    MAINPICTURE("MainPicture"),
    NAMES("Names"),
    PVS("PVs"),
    RELEASEEVENT("ReleaseEvent"),
    TAGS("Tags"),
    THUMBURL("ThumbUrl"),
    WEBLINKS("WebLinks"),
    BPM("Bpm"),
    CULTURECODES("CultureCodes")
  }

  /**
   * Build from raw VocaDB values (comma separated).
   */
  constructor(vararg constantNames: String) : this(parseConstants(constantNames))

  /**
   * Build from enum constants.
   */
  constructor(vararg constants: Constant?) : this(constants.filterNotNull().toSet())

  /**
   * Serialize to VocaDB value string (null when empty to omit optional fields).
   */
  @JsonValue
  fun toJson(): String? {
    return toCommaSeparated(enums) { it.value }
  }

  override fun toString(): String = toJson().orEmpty()

  companion object {
    /**
     * Parse from JSON value.
     */
    @JvmStatic
    @JsonCreator
    fun fromValue(value: String?): SongOptionalFields? {
      if (value.isNullOrBlank()) {
        return null
      }
      return SongOptionalFields(value)
    }

    /**
     * Create a set of optional fields.
     */
    fun of(vararg constants: Constant): SongOptionalFields = SongOptionalFields(*constants)

    private fun parseConstants(constantNames: Array<out String?>): Set<Constant> {
      return parseCommaSeparated(constantNames, Constant.entries.associateBy { it.value }, "SongOptionalFields")
    }
  }
}

// NOTE: We intentionally keep simple helpers here instead of a shared abstract base class.
// Jackson + Kotlin generics/inheritance make @JsonValue/@JsonCreator wiring and parsing maps more complex.

/**
 * Serialize a set of enum-like values to a comma-separated string.
 * Returns null when the set is empty to omit optional fields in query parameters.
 */
private fun <T> toCommaSeparated(enums: Set<T>, valueOf: (T) -> String): String? {
  return enums.takeIf { it.isNotEmpty() }?.joinToString(",") { valueOf(it) }
}

/**
 * Parse a comma-separated string or array of strings into a set of enum-like values.
 * Throws IllegalArgumentException if any value is unexpected.
 */
private fun <T> parseCommaSeparated(
  constantNames: Array<out String?>,
  mapper: Map<String, T>,
  typeName: String
): Set<T> {
  if (constantNames.isEmpty()) {
    return emptySet()
  }
  val rawNames = if (constantNames.size == 1) {
    constantNames[0]?.split(",") ?: emptyList()
  } else {
    constantNames.toList()
  }
  val parsed = mutableSetOf<T>()
  for (name in rawNames) {
    val trimmed = name?.trim()
    if (trimmed.isNullOrEmpty()) {
      continue
    }
    val constant = mapper[trimmed]
      ?: throw IllegalArgumentException("Unexpected $typeName value $trimmed")
    parsed.add(constant)
  }
  return parsed
}
