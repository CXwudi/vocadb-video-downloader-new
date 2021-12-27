package mikufan.cx.vvd.commonkt.naming

import java.nio.file.Paths

/**
 * A wrapper of [String] that hold the valid filename.
 *
 * Validation of filename is done on creation
 */
@JvmInline
value class SongProperFileName(
  private val properFileName: String
) {
  init {
    Paths.get(properFileName) // a check to make sure the filename is valid, otherwise an exception is throw
  }

  override fun toString(): String {
    return properFileName
  }
}
