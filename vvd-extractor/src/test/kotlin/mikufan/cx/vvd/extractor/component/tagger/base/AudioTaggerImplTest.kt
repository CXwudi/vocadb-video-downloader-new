package mikufan.cx.vvd.extractor.component.tagger.base

import tools.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import mikufan.cx.vvd.commonkt.vocadb.api.model.SongForApiContract
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.extractor.component.extractor.base.BaseAudioExtractor
import mikufan.cx.vvd.extractor.component.tagger.impl.M4aAudioTagger
import mikufan.cx.vvd.extractor.component.tagger.impl.MkaAudioTagger
import mikufan.cx.vvd.extractor.component.tagger.impl.Mp3AudioTagger
import mikufan.cx.vvd.extractor.component.tagger.impl.OggOpusAudioTagger
import mikufan.cx.vvd.extractor.config.IOConfig
import mikufan.cx.vvd.extractor.model.Parameters
import mikufan.cx.vvd.extractor.model.VSongTask
import mikufan.cx.vvd.extractor.util.SpringBootTestWithTestProfile
import mikufan.cx.vvd.extractor.util.getResourceAsPath
import mikufan.cx.vvd.extractor.util.loadResourceAsString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.util.Optional
import kotlin.io.path.copyTo

@SpringBootTestWithTestProfile(
  customProperties = [
    "config.environment.python-launch-cmd=python"
  ]
)
class AudioTaggerImplTest(
  ioConfig: IOConfig,
  private val m4aAudioTagger: M4aAudioTagger,
  private val oggOpusAudioTagger: OggOpusAudioTagger,
  private val mp3AudioTagger: Mp3AudioTagger,
  private val mkaAudioTagger: MkaAudioTagger,
  private val objectMapper: ObjectMapper,
) {
  private val dummyAudioExtractor: BaseAudioExtractor = mockk()

  init {
    every { dummyAudioExtractor.name } returns "Dummy Audio Extractor for Testing"
  }

  private val outputDirectory = ioConfig.outputDirectory

  private fun copyTestAudioFile(fileName: String): Path {
    val testAudioFile = getResourceAsPath(fileName)
    val outputFile = outputDirectory.resolve(testAudioFile.fileName)
    return testAudioFile.copyTo(outputFile, overwrite = true)
  }

  private fun testTagging(audioFile: Path, labelFileName: String, infoFileName: String, audioTagger: BaseAudioTagger) {
    val label = objectMapper.readValue(
      loadResourceAsString(labelFileName),
      VSongLabel::class.java
    )
    val song = objectMapper.readValue(
      loadResourceAsString(infoFileName),
      SongForApiContract::class.java
    )
    val task = VSongTask(label, Parameters(song, Optional.of(dummyAudioExtractor), audioFile))
    assertThat(audioTagger.tag(audioFile, task).isSuccess).isTrue()
  }

  @Test
  fun handleTaggingForM4aAudioFile() {
    testTagging(
      copyTestAudioFile("test-audio-files/【初音ミク】ヤー・チャイカ【yamada】[350950]-audio.m4a"),
      "20xx年V家新曲-download-test/【初音ミク】ヤー・チャイカ【yamada】[350950]-label.json",
      "20xx年V家新曲-download-test/【初音ミク】ヤー・チャイカ【yamada】[350950]-songInfo.json",
      m4aAudioTagger
    )
  }

  @Test
  fun handleTaggingForOggAudioFile() {
    testTagging(
      copyTestAudioFile("test-audio-files/【初音ミク】シル・ヴ・プレジデント【ナナホシ管弦楽団】[328036]-audio.ogg"),
      "20xx年V家新曲-download-test/【初音ミク】シル・ヴ・プレジデント【ナナホシ管弦楽団】[328036]-label.json",
      "20xx年V家新曲-download-test/【初音ミク】シル・ヴ・プレジデント【ナナホシ管弦楽団】[328036]-songInfo.json",
      oggOpusAudioTagger
    )
  }

  @Test
  fun handleTaggingForMp3AudioFile() {
    testTagging(
      copyTestAudioFile("test-audio-files/【初音ミク】WANCO!!【Twinfield】[336290]-audio.mp3"),
      "20xx年V家新曲-download-test/【初音ミク】WANCO!!【Twinfield】[336290]-label.json",
      "20xx年V家新曲-download-test/【初音ミク】WANCO!!【Twinfield】[336290]-songInfo.json",
      mp3AudioTagger
    )
  }

  @Test
  fun handleTaggingForMkaAudioFile() {
    testTagging(
      copyTestAudioFile("test-audio-files/【初音ミク】こころのキラリ【shishy】[661223]-audio.mka"),
      "20xx年V家新曲-download-test/【初音ミク】こころのキラリ【shishy】[661223]-label.json",
      "20xx年V家新曲-download-test/【初音ミク】こころのキラリ【shishy】[661223]-songInfo.json",
      mkaAudioTagger
    )
  }
}
