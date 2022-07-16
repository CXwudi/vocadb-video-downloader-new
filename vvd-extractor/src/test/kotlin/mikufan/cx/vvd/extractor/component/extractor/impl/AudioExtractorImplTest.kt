package mikufan.cx.vvd.extractor.component.extractor.impl

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import mikufan.cx.vocadbapiclient.model.SongForApiContract
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.extractor.component.extractor.base.BaseAudioExtractor
import mikufan.cx.vvd.extractor.config.IOConfig
import mikufan.cx.vvd.extractor.model.Parameters
import mikufan.cx.vvd.extractor.model.VSongTask
import mikufan.cx.vvd.extractor.util.SpringBootDirtyTestWithTestProfile
import mikufan.cx.vvd.extractor.util.SpringShouldSpec
import mikufan.cx.vvd.extractor.util.loadResourceAsString
import org.springframework.util.ResourceUtils
import java.nio.file.Paths

@SpringBootDirtyTestWithTestProfile(
  customProperties = [
    "config.environment.ffmpeg-launch-cmd=ffmpeg"
  ]
)
class AudioExtractorImplTest(
  private val aacToM4aAudioExtractor: AacToM4aAudioExtractor,
  private val opusToOggAudioExtractor: OpusToOggAudioExtractor,
  private val objectMapper: ObjectMapper,
  ioConfig: IOConfig,
) : SpringShouldSpec({
  val outputDir = ioConfig.outputDirectory
  val testExtract = fun(resourceName: String, pvFileName: String, audioExtractor: BaseAudioExtractor) {
    val song = objectMapper.readValue(
      loadResourceAsString(resourceName),
      SongForApiContract::class.java
    )
    val pvFile = Paths.get(ResourceUtils::class.java.classLoader.getResource(pvFileName)!!.toURI())
    val task = VSongTask(VSongLabel.builder().build(), Parameters(song))
    val result = audioExtractor.extract(pvFile, task, outputDir)
    result.isSuccess shouldBe true
  }
  context("test extraction") {
    should("handle aac to m4a") {
      testExtract(
        "2021年V家新曲-download-test/【初音ミク】ヤー・チャイカ【yamada】[350950]-songInfo.json",
        "2021年V家新曲-download-test/【初音ミク】ヤー・チャイカ【yamada】[350950]-pv.mp4",
        aacToM4aAudioExtractor
      )
    }
    should("handle opus to ogg") {
      testExtract(
        "2021年V家新曲-download-test/【初音ミク】シル・ヴ・プレジデント【ナナホシ管弦楽団】[328036]-songInfo.json",
        "2021年V家新曲-download-test/【初音ミク】シル・ヴ・プレジデント【ナナホシ管弦楽団】[328036]-pv.webm",
        opusToOggAudioExtractor
      )
    }
  }
})
