package mikufan.cx.vvd.commonkt.naming

import mikufan.cx.vocadbapiclient.model.SongForApiContract
import mikufan.cx.vvd.common.naming.FileNamePostFix

/**
 * Contain the two basic filename naming for [SongForApiContract]
 *
 * Both method can call from [SongForApiContract]
 *
 * Other naming such as toPvFileName, toLabelFileName, etc. are extension of [SongProperFileName]
 * in order to enforce the common [SongProperFileName] to be reused
 *
 * Such extensions are all in their specific module
 *
 * @author CX无敌
 * @date 2020-12-19
 */

// fun SongForApiContract.toAudioFileName(extensionWithDot: String = ""): String {
//  return this.toProperFileName() + FileNamePostFix.AUDIO + extensionWithDot
// }

// not sure if above extensions are needed

fun SongForApiContract.toErrorFileName(): String {
  return this.toProperFileName().toString() + FileNamePostFix.ERROR + ".json"
}

fun SongForApiContract.toProperFileName(): SongProperFileName {
  val artists: List<String> = requireNotNull(artistString) { "artist string is null" }.split("feat.")
  val vocals = artists[1].trim()
  val producers = artists[0].trim()
  val songName: String = requireNotNull(defaultName) { "song name is null" }
  return SongProperFileName(removeIllegalChars(String.format("【%s】%s【%s】", vocals, songName, producers)))
}

fun removeIllegalChars(fileName: String): String {
  return fileName
    .replace("/", "-")
    .replace("\\", "-")
    .replace('"', '\'')
    .replace("? ", " ")
    .replace("* ", " ")
    .replace(": ", " ")
    .replace("?", " ")
    .replace("*", " ")
    .replace(":", " ")
}
