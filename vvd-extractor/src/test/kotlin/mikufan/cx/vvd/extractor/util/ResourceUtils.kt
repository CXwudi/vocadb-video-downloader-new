package mikufan.cx.vvd.extractor.util

import org.springframework.util.ResourceUtils

/**
 * @date 2022-05-21
 * @author CX无敌
 */

fun loadResourceAsString(resourceName: String): String {
  val stream = ResourceUtils::class.java.classLoader.getResourceAsStream(resourceName)
  if (stream != null) {
    return stream.bufferedReader().use { it.readText() }
  } else {
    throw IllegalArgumentException("resource not found: $resourceName")
  }
}
