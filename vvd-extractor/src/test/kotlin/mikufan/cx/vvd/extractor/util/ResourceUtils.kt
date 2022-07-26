package mikufan.cx.vvd.extractor.util

import org.springframework.util.ResourceUtils
import java.nio.file.Path
import java.nio.file.Paths

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

fun getResourceAsPath(resourceName: String): Path {
  return ResourceUtils::class.java.classLoader.getResource(resourceName)?.let {
    Paths.get(it.toURI())
  } ?: throw IllegalArgumentException("resource not found: $resourceName")
}
