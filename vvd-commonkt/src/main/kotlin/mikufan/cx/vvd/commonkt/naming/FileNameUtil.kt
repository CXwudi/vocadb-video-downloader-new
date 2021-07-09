package mikufan.cx.vvd.commonkt.naming

import mikufan.cx.vocadbapiclient.model.SongForApiContract
import mikufan.cx.vvd.common.naming.FileNamePostFix
import mikufan.cx.vvd.commonkt.exception.requireNotNull

/**
 * @author CX无敌
 * @date 2020-12-19
 */

fun SongForApiContract.toPvFileName(extensionWithDot: String = ""): String {
  return this.toProperFileName() + FileNamePostFix.VIDEO + extensionWithDot
}

fun SongForApiContract.toThumbnailFileName(extensionWithDot: String = ""): String {
  return this.toProperFileName() + FileNamePostFix.THUMBNAIL + extensionWithDot
}

fun SongForApiContract.toAudioFileName(extensionWithDot: String = ""): String {
  return this.toProperFileName() + FileNamePostFix.AUDIO + extensionWithDot
}

// not sure if above extensions are needed

fun SongForApiContract.toErrorFileName(): String {
  return this.toProperFileName() + FileNamePostFix.ERROR + ".json"
}

fun SongForApiContract.toProperFileName(): String {
  val artists: List<String> = artistString.requireNotNull{ "artist string is null" }.split("feat.")
  val vocals = artists[1].trim()
  val producers = artists[0].trim()
  val songName: String = defaultName.requireNotNull{ "song name is null" }
  return removeIllegalChars(String.format("【%s】%s【%s】", vocals, songName, producers))
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
