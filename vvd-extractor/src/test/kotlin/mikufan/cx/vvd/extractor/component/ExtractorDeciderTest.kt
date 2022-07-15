package mikufan.cx.vvd.extractor.component

import io.kotest.matchers.shouldBe
import mikufan.cx.vvd.extractor.util.SpringBootDirtyTestWithTestProfile
import mikufan.cx.vvd.extractor.util.SpringShouldSpec
import kotlin.io.path.Path

@SpringBootDirtyTestWithTestProfile
class ExtractorDeciderTest(
  private val extractorDecider: ExtractorDecider
) : SpringShouldSpec({
  context("checking the audio format") {
    should("find the audio format of mp4") {
      val audioFormat =
        extractorDecider.checkAudioFormat(Path("../test-files/【初音ミク】LONELY POP feat.初音ミク (Yandere VIP Remix)【オリジナル】 [sm40260101]-trim.mp4"))
      audioFormat shouldBe "aac"
    }

    should("find audio format of ts") {
      val audioFormat =
        extractorDecider.checkAudioFormat(Path("../test-files/「クリーデンス」／霧島feat.初音ミク-sm39825313-trim.ts"))
      audioFormat shouldBe "aac"
    }

    should("find audio format in webm") {
      val audioFormat =
        extractorDecider.checkAudioFormat(Path("../test-files/Kikuo - 幽体離脱 [UHH2KKN0xoc]-trim.webm"))
      audioFormat shouldBe "opus"
    }
  }
})
