package mikufan.cx.vvd.downloader.config.downloader

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

/**
 * @date 2021-07-14
 * @author CX无敌
 */
@Disabled
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

  /**
   * ultimate function from string to a list of command
   */
  @Deprecated(
    """use YAML list for entering commands to avoid any other separating issue.
      |this allow us to use back the data class
      |for easy commands without comma involved, users can write their command separated by comma,
      |spring boot configuration will be able to parse both YAML list and comma separated string to List<String>
    """
  )
  internal fun String.splitCommands(): List<String> {
    val splitted = this.split(' ').filter { it.isNotBlank() }
    val commands = mutableListOf<String>()
    var currentQuote = ""
    val longCommandStrs = mutableListOf<String>()

    if (splitted.isEmpty()) return commands
    splitted.forEach { str ->
      // 6 beginning stages, 3 beginning with quote, 3 contains equal-quote, turn on recording mode
      if (currentQuote == "" && str.startsWith('\'') && !str.endsWith('\'')) {
        currentQuote = "'"
        longCommandStrs += str
      } else if (currentQuote == "" && str.startsWith('"') && !str.endsWith('"')) {
        currentQuote = "\""
        longCommandStrs += str
      } else if (currentQuote == "" && str.startsWith('`') && !str.endsWith('`')) {
        currentQuote = "`"
        longCommandStrs += str
      } else if (currentQuote == "" && str.contains("='") && !str.endsWith('\'')) {
        currentQuote = "='"
        longCommandStrs += str
      } else if (currentQuote == "" && str.contains("=\"") && !str.endsWith('"')) {
        currentQuote = "=\""
        longCommandStrs += str
      } else if (currentQuote == "" && str.contains("=`") && !str.endsWith('`')) {
        currentQuote = "=`"
        longCommandStrs += str
      }
      // all goes in this 1 ending stage, turn off recording mode
      else if (currentQuote != "" && (str.endsWith('\'') || str.endsWith('"') || str.endsWith('`'))) {
        longCommandStrs += str
        val quotedCommand = longCommandStrs.joinToString(" ")
        commands += if (currentQuote.startsWith('=')) {
          // from 3 beginning stage with equal-quote
          quotedCommand
        } else {
          // from 3 beginning stage with quote, remove the outer quote
          quotedCommand.replace(currentQuote, "")
        }
        currentQuote = ""
        longCommandStrs.clear()
      } else { // do stuff
        // normal model
        if (currentQuote == "") {
          commands += if (str.contains('=')) {
            // e.g. --arg="something"
            str
          } else {
            // e.g. "--some-arg1"
            str.replace("'", "")
              .replace("\"", "")
              .replace("`", "")
          }
        } else { // recording mode
          longCommandStrs += str
        }
      }
    }
    return commands
  }

}
