package mikufan.cx.vvd.downloader.config.downloader

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

/**
 * @date 2021-07-14
 * @author CX无敌
 */
class SplitCommandTest {

  @ParameterizedTest
  @MethodSource("strings")
  fun tryWithCommands(rawString: String, expectedCommands: List<String>) {
    assertEquals(expectedCommands, rawString.splitCommands())
  }

  companion object {
    @JvmStatic
    fun strings(): Stream<Arguments> = Stream.of(
      Arguments.of(
        "youtube-dl --username \"123456\" --password '654321'",
        listOf("youtube-dl", "--username", "123456", "--password", "654321")
      ),
      Arguments.of(
        "youtube-dl --external=\"ffmpeg --arg1 --arg2 'some string'\"",
        listOf("youtube-dl", "--external=\"ffmpeg --arg1 --arg2 'some string'\"")
      ),
      Arguments.of(
        "youtube-dl --external \"ffmpeg --arg1 --arg2 'some string'\"",
        listOf("youtube-dl", "--external", "ffmpeg --arg1 --arg2 'some string'")
      ),
      Arguments.of(
        "youtube-dl --external=\"some-arg\"",
        listOf("youtube-dl", "--external=\"some-arg\"")
      ),
    )
  }
}
