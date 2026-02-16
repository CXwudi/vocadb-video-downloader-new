package mikufan.cx.vvd.commonkt.vocadb.api.model

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import tools.jackson.databind.JsonNode

/**
 * Song entry with ordering information inside a list response.
 */
data class SongInListForApiContract(
  val notes: String? = null,
  val order: Int? = null,
  val song: SongForApiContract? = null,
  @param:JsonAnySetter
  @get:JsonAnyGetter
  val additionalProperties: Map<String, JsonNode> = emptyMap()
)

/**
 * Partial list response wrapper for song list queries.
 */
data class SongInListForApiContractPartialFindResult(
  @JsonSetter(nulls = Nulls.AS_EMPTY)
  val items: List<SongInListForApiContract> = emptyList(),
  val term: String? = null,
  val totalCount: Int? = null,
  @param:JsonAnySetter
  @get:JsonAnyGetter
  val additionalProperties: Map<String, JsonNode> = emptyMap()
)

