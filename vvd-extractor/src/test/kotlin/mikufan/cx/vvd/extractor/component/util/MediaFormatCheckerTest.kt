package mikufan.cx.vvd.extractor.component.util

import mikufan.cx.vvd.extractor.util.AudioMediaFormat
import mikufan.cx.vvd.extractor.util.SpringBootTestWithTestProfile
import mikufan.cx.vvd.extractor.util.getResourceAsPath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.io.path.Path

@SpringBootTestWithTestProfile
class MediaFormatCheckerTest(
  private val mediaFormatChecker: MediaFormatChecker
) {

  @ParameterizedTest(name = "find audio format of pv {0}")
  @MethodSource("pvTestCases")
  fun findAudioFormatFromPv(fileName: String, expectedFormat: String) {
    val audioFormat = mediaFormatChecker.checkAudioFormat(Path(fileName))
    assertThat(audioFormat).isEqualTo(expectedFormat)
  }

  @ParameterizedTest(name = "find audio format of audio file {0}")
  @MethodSource("audioFileTestCases")
  fun findAudioFormatFromAudioFile(fileName: String, expectedFormat: String) {
    val audioFormat = mediaFormatChecker.checkAudioFormat(getResourceAsPath(fileName))
    assertThat(audioFormat).isEqualTo(expectedFormat)
  }

  @ParameterizedTest(name = "find image mimetype of {0}")
  @MethodSource("imageTestCases")
  fun findImageMimeType(fileName: String, expectedType: String) {
    val imageType = mediaFormatChecker.checkImageType(getResourceAsPath(fileName))
    assertThat(imageType).isEqualTo(expectedType)
  }

  companion object {
    @JvmStatic
    fun pvTestCases(): List<Arguments> = listOf(
      Arguments.of(
        "../test-files/【初音ミク】LONELY POP feat.初音ミク (Yandere VIP Remix)【オリジナル】 [sm40260101]-trim.mp4",
        AudioMediaFormat.AAC
      ),
      Arguments.of(
        "../test-files/「クリーデンス」／霧島feat.初音ミク-sm39825313-trim.ts",
        AudioMediaFormat.AAC
      ),
      Arguments.of(
        "../test-files/Kikuo - 幽体離脱 [UHH2KKN0xoc]-trim.webm",
        AudioMediaFormat.OPUS
      )
    )

    @JvmStatic
    fun audioFileTestCases(): List<Arguments> = listOf(
      Arguments.of(
        "test-audio-files/【初音ミク】ヤー・チャイカ【yamada】[350950]-audio.m4a",
        AudioMediaFormat.AAC
      ),
      Arguments.of(
        "test-audio-files/【初音ミク】シル・ヴ・プレジデント【ナナホシ管弦楽団】[328036]-audio.ogg",
        AudioMediaFormat.OPUS
      ),
      Arguments.of(
        "test-audio-files/【初音ミク】WANCO!!【Twinfield】[336290]-audio.mp3",
        AudioMediaFormat.MPEG_AUDIO
      )
    )

    @JvmStatic
    fun imageTestCases(): List<Arguments> = listOf(
      Arguments.of(
        "20xx年V家新曲-download-test/【初音ミク】WANCO!!【Twinfield】[336290]-thumbnail.jpg",
        "jpeg"
      ),
      Arguments.of(
        "20xx年V家新曲-download-test/【初音ミク】シル・ヴ・プレジデント【ナナホシ管弦楽団】[328036]-thumbnail.webp",
        "webp"
      )
    )
  }
}
