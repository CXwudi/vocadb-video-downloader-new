package mikufan.cx.vvd.commonkt.naming

import mikufan.cx.vvd.commonkt.vocadb.api.model.SongForApiContract
import mikufan.cx.vvd.common.naming.FileNamePostFix

/**
 * Contain the basic filename naming for [SongForApiContract]
 *
 * Other naming such as toPvFileName, toLabelFileName, etc. are extension of [SongProperFileName]
 * in order to enforce the common [SongProperFileName] to be reused
 *
 * Such extensions are all in their specific module
 *
 * @author CX无敌
 * @date 2020-12-19
 */

fun SongForApiContract.toErrorFileName(): String {
  return toProperFileName().toString() + FileNamePostFix.ERROR + ".json"
}

/**
 * Take a [SongForApiContract] and generate its proper filename that is confliction-free
 *
 * By default, all filename generation should be drived from this name
 */
fun SongForApiContract.toProperFileName(): SongProperFileName {
  return buildSongProperFileName(artistString, defaultName, id)
}

/**
 * Build a consistent filename structure from common song fields.
 */
private fun buildSongProperFileName(
  artistString: String?,
  defaultName: String?,
  id: Int?
): SongProperFileName {
  val artists = requireNotNull(artistString) { "artist string is null" }.split("feat.")
  require(artists.size >= 2) { "artist string missing 'feat.': $artistString" }
  val vocals = artists[1].trim()
  val producers = artists[0].trim()
  val songName: String = requireNotNull(defaultName) { "song name is null" }
  val vocadbId = requireNotNull(id) { "id is null" }
  return SongProperFileName(removeIllegalChars(String.format("【%s】%s【%s】[%s]", vocals, songName, producers, vocadbId)))
}

fun removeIllegalChars(fileName: String): String {
  return fileName
    .replace("/", "-")
    .replace("\\", "-")
    .replace('"', '\'')
    .replace("? ", " ")
    .replace("* ", " ")
    .replace(": ", " ")
    .replace(Regex("[\\\\/:*?\"<>|]"), " ")
}
