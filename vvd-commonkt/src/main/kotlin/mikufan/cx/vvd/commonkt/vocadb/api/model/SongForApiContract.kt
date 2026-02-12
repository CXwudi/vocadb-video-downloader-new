package mikufan.cx.vvd.commonkt.vocadb.api.model

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.databind.JsonNode
import java.time.LocalDateTime

/**
 * Minimal song contract used across task producer, downloader, and extractor.
 */
data class SongForApiContract(
  val id: Int? = null,
  val name: String? = null,
  val defaultName: String? = null,
  val artistString: String? = null,
  @param:JsonSetter(nulls = Nulls.AS_EMPTY)
  val pvs: List<PVContract> = emptyList(),
  @param:JsonSetter(nulls = Nulls.AS_EMPTY)
  val albums: List<AlbumForApiContract> = emptyList(),
  @param:JsonSetter(nulls = Nulls.AS_EMPTY)
  val artists: List<ArtistForSongContract> = emptyList(),
  val publishDate: LocalDateTime? = null,
  val songType: String? = null,
  val status: String? = null,
  @param:JsonAnySetter
  @get:JsonAnyGetter
  val additionalProperties: Map<String, JsonNode> = emptyMap()
)
