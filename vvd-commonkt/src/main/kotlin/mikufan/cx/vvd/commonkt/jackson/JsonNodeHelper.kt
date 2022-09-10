package mikufan.cx.vvd.commonkt.jackson

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSubclassOf

/**
 * to support delegation to JsonNode from a data class.
 *
 * however, delegation only works for String, primitive types,
 * and another data class that takes the [JsonNodeKt] as the only constructor parameter
 *
 * delegation to [Iterable] is not supported
 * @date 2022-09-10
 * @author CX无敌
 */
class JsonNodeKt @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(val jsonNode: JsonNode) {
  inline operator fun <reified V> getValue(thisRef: Any?, property: KProperty<*>): V {
    return if (listOf(
        String::class,
        Int::class,
        Long::class,
        Short::class,
        Byte::class,
        Double::class,
        Float::class,
        Boolean::class
      ).any { it == V::class }
    ) {
      jsonNode.get(property.name).asValue()
    } else if (V::class.isSubclassOf(Iterable::class)) {
      throw UnsupportedOperationException("can not support collection due to technical limitation")
    } else {
      V::class.java.getDeclaredConstructor(JsonNodeKt::class.java).newInstance(jsonNode.get(property.name))
    }
  }

  inline operator fun <reified V> setValue(thisRef: Any?, property: KProperty<*>, value: V) {
    if (value is JsonNodeKt) {
      jsonNode[property.name] = value.jsonNode
    } else if (value is Iterable<*>) {
      throw UnsupportedOperationException("can not support collection due to technical limitation")
    }
    jsonNode[property.name] = value
  }
}

inline fun <reified V> JsonNode.asValue(): V {
  if (V::class == String::class) {
    return this.asText() as V
  } else if (V::class == Int::class) {
    return this.asInt() as V
  } else if (V::class == Long::class) {
    return this.asLong() as V
  } else if (V::class == Short::class) {
    return this.shortValue() as V
  } else if (V::class == Byte::class) {
    return this.shortValue().toByte() as V
  } else if (V::class == Boolean::class) {
    return this.asBoolean() as V
  } else if (V::class == Double::class) {
    return this.asDouble() as V
  } else if (V::class == Float::class) {
    return this.floatValue() as V
  } else {
    throw IllegalArgumentException("unsupported type ${V::class}")
  }
}

inline operator fun <reified K, reified V> JsonNode.set(key: K, value: V) {
  if (key is Int && this.isArray) {
    if (V::class == String::class) {
      (this as ArrayNode).set(key, value as String)
    } else if (V::class == Int::class) {
      (this as ArrayNode).set(key, value as Int)
    } else if (V::class == Long::class) {
      (this as ArrayNode).set(key, value as Long)
    } else if (V::class == Short::class) {
      (this as ArrayNode).set(key, value as Short)
    } else if (V::class == Byte::class) {
      (this as ArrayNode).set(key, value as Short) // there is no set(byte) method
    } else if (V::class == Boolean::class) {
      (this as ArrayNode).set(key, value as Boolean)
    } else if (V::class == Double::class) {
      (this as ArrayNode).set(key, value as Double)
    } else if (V::class == Float::class) {
      (this as ArrayNode).set(key, value as Float)
    } else if (V::class == JsonNode::class) {
      (this as ArrayNode).set(key, value as JsonNode)
    } else {
      throw IllegalArgumentException("unsupported type ${V::class}")
    }
  } else if (key is String && this.isObject) {
    if (V::class == String::class) {
      (this as ObjectNode).put(key, value as String)
    } else if (V::class == Int::class) {
      (this as ObjectNode).put(key, value as Int)
    } else if (V::class == Long::class) {
      (this as ObjectNode).put(key, value as Long)
    } else if (V::class == Short::class) {
      (this as ObjectNode).put(key, value as Short)
    } else if (V::class == Byte::class) {
      (this as ObjectNode).put(key, value as Short) // there is no put(byte) method
    } else if (V::class == Boolean::class) {
      (this as ObjectNode).put(key, value as Boolean)
    } else if (V::class == Double::class) {
      (this as ObjectNode).put(key, value as Double)
    } else if (V::class == Float::class) {
      (this as ObjectNode).put(key, value as Float)
    } else if (V::class == JsonNode::class) {
      (this as ObjectNode).set<JsonNode>(key, value as JsonNode)
    } else {
      throw IllegalArgumentException("unsupported type ${V::class}")
    }
  } else {
    throw IllegalArgumentException("unsupported key type ${K::class}")
  }
}
