package mikufan.cx.vvd.downloader.component.downloader

import java.nio.file.Path

/**
 * Data class storing all downloaded reousces, noticed that at least one of [pvFile] and [audioFile] should be non-null
 *
 * @property pvFile the PV file
 * @property audioFile the audio file
 * @property thumbnailFile the thumbnail file
 */
data class DownloadFiles(
  val pvFile: Path?,
  val audioFile: Path?,
  val thumbnailFile: Path,
)

enum class DownloadFileType {
  PV,
  AUDIO,
  THUMBNAIL,
  OTHERS,
}
