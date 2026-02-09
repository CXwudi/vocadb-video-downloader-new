package mikufan.cx.vvd.commonkt.vocadb.api.model

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.databind.JsonNode

/**
 * Minimal PV contract used by downloader and extractor.
 */
data class PVContract(
  val id: Int? = null,
  val pvId: String? = null,
  val service: PVService? = null,
  val pvType: PVType? = null,
  val url: String? = null,
  val name: String? = null,
  @param:JsonAnySetter
  @get:JsonAnyGetter
  val additionalProperties: Map<String, JsonNode> = emptyMap()
)
