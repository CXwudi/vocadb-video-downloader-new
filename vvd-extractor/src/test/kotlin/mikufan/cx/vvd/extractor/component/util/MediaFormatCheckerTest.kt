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
    should("find the audio format of mp4") {
      val audioFormat =
        mediaFormatChecker.checkAudioFormat(Path("../test-files/【初音ミク】LONELY POP feat.初音ミク (Yandere VIP Remix)【オリジナル】 [sm40260101]-trim.mp4"))
      audioFormat shouldBe AudioMediaFormat.AAC
    }

    should("find audio format of ts") {
      val audioFormat =
        mediaFormatChecker.checkAudioFormat(Path("../test-files/「クリーデンス」／霧島feat.初音ミク-sm39825313-trim.ts"))
      audioFormat shouldBe AudioMediaFormat.AAC
    }

    should("find audio format in webm") {
      val audioFormat =
        mediaFormatChecker.checkAudioFormat(Path("../test-files/Kikuo - 幽体離脱 [UHH2KKN0xoc]-trim.webm"))
      audioFormat shouldBe AudioMediaFormat.OPUS
    }
  }

  context("checking the audio format from audio file") {
    should("find audio format of m4a") {
      val audioFormat =
        mediaFormatChecker.checkAudioFormat(getResourceAsPath("test-audio-files/【初音ミク】ヤー・チャイカ【yamada】[350950]-audio.m4a"))
      audioFormat shouldBe AudioMediaFormat.AAC
    }

    should("find audio format of ogg") {
      val audioFormat =
        mediaFormatChecker.checkAudioFormat(getResourceAsPath("test-audio-files/【初音ミク】シル・ヴ・プレジデント【ナナホシ管弦楽団】[328036]-audio.ogg"))
      audioFormat shouldBe AudioMediaFormat.OPUS
    }

    should("find audio format of mp3") {
      val audioFormat =
        mediaFormatChecker.checkAudioFormat(getResourceAsPath("test-audio-files/【初音ミク】WANCO!!【Twinfield】[336290]-audio.mp3"))
      audioFormat shouldBe AudioMediaFormat.MPEG_AUDIO
    }
  }
})
