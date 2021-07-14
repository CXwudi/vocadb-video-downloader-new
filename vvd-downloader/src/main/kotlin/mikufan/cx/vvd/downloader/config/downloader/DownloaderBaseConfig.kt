package mikufan.cx.vvd.downloader.config.downloader

/**
 * @date 2021-06-25
 * @author CX无敌
 */

interface DownloaderBaseConfig {
  val launchCmd: List<String>
}

/**
 * ultimate function from string to a list of command
 */
internal fun String.splitCommands(): List<String> {
  val splitted = this.split(' ')
  val commands = mutableListOf<String>()
  var currentQuote = ""
  val longCommandStrs = mutableListOf<String>()
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
