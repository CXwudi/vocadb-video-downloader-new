package mikufan.cx.vvd.commonkt.naming

import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.moveTo

/**
 * some util functions for path
 */

fun Path.renameWithSameExtension(newFileNameWithoutDot: String, overwrite: Boolean = false): Path {
  val sibling = this.resolveSibling("$newFileNameWithoutDot.${this.extension}")
  this.moveTo(sibling, overwrite)
  return sibling
}
