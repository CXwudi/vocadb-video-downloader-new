package mikufan.cx.vvd.downloader.util

import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.moveTo

/**
 * some util functions for path
 */

fun Path.renameWithSameExtension(newFileNameWithoutDot: String): Path {
  val sibling = this.resolveSibling(newFileNameWithoutDot + this.extension)
  this.moveTo(sibling, overwrite = true)
  return sibling
}
