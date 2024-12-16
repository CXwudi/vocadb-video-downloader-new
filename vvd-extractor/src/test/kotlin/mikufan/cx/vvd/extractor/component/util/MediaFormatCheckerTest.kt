package mikufan.cx.vvd.extractor.component.util

import io.kotest.matchers.shouldBe
import mikufan.cx.vvd.extractor.util.AudioMediaFormat
import mikufan.cx.vvd.extractor.util.SpringBootTestWithTestProfile
import mikufan.cx.vvd.extractor.util.SpringShouldSpec
import mikufan.cx.vvd.extractor.util.getResourceAsPath
import kotlin.io.path.Path

@SpringBootTestWithTestProfile
class MediaFormatCheckerTest(
  private val mediaFormatChecker: MediaFormatChecker
) : SpringShouldSpec({

  context("checking the audio format from PV") {
    data class PvTestCase(
      val fileName: String,
      val expectedFormat: String,
      val fileType: String = fileName.substringAfterLast('.')
    )

    listOf(
      PvTestCase(
        "../test-files/【初音ミク】LONELY POP feat.初音ミク (Yandere VIP Remix)【オリジナル】 [sm40260101]-trim.mp4",
        AudioMediaFormat.AAC
      ),
      PvTestCase(
        "../test-files/「クリーデンス」／霧島feat.初音ミク-sm39825313-trim.ts",
        AudioMediaFormat.AAC
      ),
      PvTestCase(
        "../test-files/Kikuo - 幽体離脱 [UHH2KKN0xoc]-trim.webm",
        AudioMediaFormat.OPUS
      )
    ).forEach { (fileName, expectedFormat, fileType) ->
      should("find audio format of $fileType") {
        val audioFormat = mediaFormatChecker.checkAudioFormat(Path(fileName))
        audioFormat shouldBe expectedFormat
      }
    }
  }

  context("checking the audio format from audio file") {
    data class AudioFileTestCase(
      val fileName: String,
      val expectedFormat: String,
      val fileType: String = fileName.substringAfterLast('.')
    )

    listOf(
      AudioFileTestCase(
        "test-audio-files/【初音ミク】ヤー・チャイカ【yamada】[350950]-audio.m4a",
        AudioMediaFormat.AAC
      ),
      AudioFileTestCase(
        "test-audio-files/【初音ミク】シル・ヴ・プレジデント【ナナホシ管弦楽団】[328036]-audio.ogg",
        AudioMediaFormat.OPUS
      ),
      AudioFileTestCase(
        "test-audio-files/【初音ミク】WANCO!!【Twinfield】[336290]-audio.mp3",
        AudioMediaFormat.MPEG_AUDIO
      )
    ).forEach { (fileName, expectedFormat, fileType) ->
      should("find audio format of $fileType") {
        val audioFormat = mediaFormatChecker.checkAudioFormat(getResourceAsPath(fileName))
        audioFormat shouldBe expectedFormat
      }
    }
  }

  context("checking the image mimetype from thumbnail file") {
    data class ImageTestCase(
      val fileName: String,
      val expectedType: String,
      val fileType: String = fileName.substringAfterLast('.')
    )

    listOf(
      ImageTestCase(
        "20xx年V家新曲-download-test/【初音ミク】WANCO!!【Twinfield】[336290]-thumbnail.jpg",
        "jpeg"
      ),
      ImageTestCase(
        "20xx年V家新曲-download-test/【初音ミク】シル・ヴ・プレジデント【ナナホシ管弦楽団】[328036]-thumbnail.webp",
        "webp"
      )
    ).forEach { (fileName, expectedType, fileType) ->
      should("find image mimetype of $fileType") {
        val imageType = mediaFormatChecker.checkImageType(getResourceAsPath(fileName))
        imageType shouldBe expectedType
      }
    }
  }
})
