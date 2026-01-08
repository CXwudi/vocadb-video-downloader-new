package mikufan.cx.vvd.taskproducer.jackson

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * Verify that @JsonAnySetter on constructor parameter works with mutableMapOf() default.
 * This test confirms the pattern we want to use for the new data classes.
 */
class JsonAnySetterOnConstructorTest {

  private val objectMapper: ObjectMapper = jacksonObjectMapper()

  /**
   * Data class with @JsonAnySetter on constructor parameter with mutableMapOf() default.
   * This is the pattern we plan to use.
   */
  data class WithMutableMapDefault(
    val id: Int,
    val name: String? = null,
    @param:JsonAnySetter
    @get:JsonAnyGetter
    val additionalProperties: Map<String, JsonNode> = mutableMapOf()
  )

  /**
   * Data class with @JsonAnySetter but with emptyMap() default.
   *
   * FINDING: Jackson-kotlin-module creates a NEW LinkedHashMap for @param:JsonAnySetter
   * regardless of the default value, so emptyMap() also works!
   */
  data class WithEmptyMapDefault(
    val id: Int,
    val name: String? = null,
    @param:JsonAnySetter
    @get:JsonAnyGetter
    val additionalProperties: Map<String, JsonNode> = emptyMap()
  )

  /**
   * Data class without @JsonAnySetter - verifies default value is used when no annotation.
   */
  data class WithoutJsonAnySetter(
    val id: Int,
    val name: String? = null,
    val additionalProperties: Map<String, JsonNode> = emptyMap()
  )

  @Test
  fun `should capture unknown fields with mutableMapOf default`() {
    val json = """{"id": 1, "name": "test", "unknownField": "unknownValue", "anotherField": 42}"""

    val result = objectMapper.readValue(json, WithMutableMapDefault::class.java)

    assertEquals(1, result.id)
    assertEquals("test", result.name)
    assertEquals(2, result.additionalProperties.size)
    assertNotNull(result.additionalProperties["unknownField"])
    assertNotNull(result.additionalProperties["anotherField"])
    assertEquals("unknownValue", result.additionalProperties["unknownField"]?.asText())
    assertEquals(42, result.additionalProperties["anotherField"]?.asInt())
  }

  @Test
  fun `should serialize back with unknown fields using mutableMapOf default`() {
    val json = """{"id": 1, "name": "test", "unknownField": "unknownValue"}"""

    val result = objectMapper.readValue(json, WithMutableMapDefault::class.java)
    val serialized = objectMapper.writeValueAsString(result)
    val reparsed = objectMapper.readTree(serialized)

    assertEquals(1, reparsed["id"].asInt())
    assertEquals("test", reparsed["name"].asText())
    assertEquals("unknownValue", reparsed["unknownField"].asText())
  }

  @Test
  fun `should also capture unknown fields with emptyMap default - jackson creates new map`() {
    // FINDING: Jackson-kotlin-module creates a NEW LinkedHashMap for @param:JsonAnySetter
    // regardless of the default value specified in Kotlin
    val json = """{"id": 1, "name": "test", "unknownField": "unknownValue", "anotherField": 42}"""

    val result = objectMapper.readValue(json, WithEmptyMapDefault::class.java)

    assertEquals(1, result.id)
    assertEquals("test", result.name)
    // This WORKS! Jackson ignores the default and creates a new mutable map
    assertEquals(2, result.additionalProperties.size)
    assertNotNull(result.additionalProperties["unknownField"])
    assertNotNull(result.additionalProperties["anotherField"])
    assertEquals("unknownValue", result.additionalProperties["unknownField"]?.asText())
    assertEquals(42, result.additionalProperties["anotherField"]?.asInt())
  }

  @Test
  fun `should use emptyMap default when created programmatically without deserialization`() {
    // Verify that the default value is used when constructing programmatically
    val instance = WithEmptyMapDefault(id = 1, name = "test")

    assertEquals(0, instance.additionalProperties.size)
    assertEquals(emptyMap<String, JsonNode>(), instance.additionalProperties)
  }

  @Test
  fun `should throw UnrecognizedPropertyException without JsonAnySetter when JSON has unknown fields`() {
    // Without @JsonAnySetter, unknown fields cause UnrecognizedPropertyException
    // This confirms that @param:JsonAnySetter IS required to capture unknown fields
    val json = """{"id": 1, "name": "test", "unknownField": "unknownValue"}"""

    assertThrows(UnrecognizedPropertyException::class.java) {
      objectMapper.readValue(json, WithoutJsonAnySetter::class.java)
    }
  }
}