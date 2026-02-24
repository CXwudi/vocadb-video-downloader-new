package mikufan.cx.vvd.extractor.component.extractor.impl

import tools.jackson.databind.ObjectMapper
import mikufan.cx.vvd.commonkt.vocadb.api.model.SongForApiContract
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.extractor.component.extractor.base.BaseAudioExtractor
import mikufan.cx.vvd.extractor.config.IOConfig
import mikufan.cx.vvd.extractor.model.Parameters
import mikufan.cx.vvd.extractor.model.VSongTask
import mikufan.cx.vvd.extractor.util.SpringBootTestWithTestProfile
import mikufan.cx.vvd.extractor.util.loadResourceAsString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.util.ResourceUtils
import java.nio.file.Paths

@SpringBootTestWithTestProfile(
  customProperties = [
    "config.environment.ffmpeg-launch-cmd=ffmpeg"
  ]
)
class AudioExtractorImplTest(
  private val aacToM4aAudioExtractor: AacToM4aAudioExtractor,
  private val opusToOggAudioExtractor: OpusToOggAudioExtractor,
  private val anyToMkaAudioExtractor: AnyToMkaAudioExtractor,
  private val objectMapper: ObjectMapper,
  ioConfig: IOConfig,
) {
  private val outputDir = ioConfig.outputDirectory

  private fun testExtract(resourceName: String, pvFileName: String, audioExtractor: BaseAudioExtractor) {
    val song = objectMapper.readValue(
      loadResourceAsString(resourceName),
      SongForApiContract::class.java
    )
    val pvFile = Paths.get(ResourceUtils::class.java.classLoader.getResource(pvFileName)!!.toURI())
    val task = VSongTask(VSongLabel.builder().build(), Parameters(song))
    val result = audioExtractor.extract(pvFile, task, outputDir)
    assertThat(result.isSuccess).isTrue()
  }

  @Test
  fun handleAacToM4a() {
    testExtract(
      "20xx年V家新曲-download-test/【初音ミク】ヤー・チャイカ【yamada】[350950]-songInfo.json",
      "20xx年V家新曲-download-test/【初音ミク】ヤー・チャイカ【yamada】[350950]-pv.mp4",
      aacToM4aAudioExtractor
    )
  }

  @Test
  fun handleOpusToOgg() {
    testExtract(
      "20xx年V家新曲-download-test/【初音ミク】シル・ヴ・プレジデント【ナナホシ管弦楽団】[328036]-songInfo.json",
      "20xx年V家新曲-download-test/【初音ミク】シル・ヴ・プレジデント【ナナホシ管弦楽団】[328036]-pv.webm",
      opusToOggAudioExtractor
    )
  }

  @Test
  fun handleEac3ToMka() {
    testExtract(
      "20xx年V家新曲-download-test/【初音ミク】こころのキラリ【shishy】[661223]-songInfo.json",
      "20xx年V家新曲-download-test/【初音ミク】こころのキラリ【shishy】[661223]-pv.mkv",
      anyToMkaAudioExtractor
    )
  }
}
