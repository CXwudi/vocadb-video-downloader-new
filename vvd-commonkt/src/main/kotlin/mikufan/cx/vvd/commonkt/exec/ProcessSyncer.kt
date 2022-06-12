@file:Suppress("NOTHING_TO_INLINE")
package mikufan.cx.vvd.commonkt.exec

import org.slf4j.Logger
import org.slf4j.event.Level
import java.io.BufferedReader
import java.io.BufferedWriter
import java.nio.charset.Charset
import java.util.concurrent.ExecutorService

/**
 * @date 2021-07-17
 * @author CX无敌
 */
class ProcessSyncer(
  val executor: ExecutorService,
  val process: Process
) {

  /**
   * Read the process's std-out with a [handler] and a specific [Charset]
   * @param charset Charset the char set that the std-out is intended to be read, default is [Charsets.UTF_8]
   * @param handler Function1<BufferedReader, Unit> code about how to handle the std-out
   */
  inline fun onStdOut(charset: Charset = Charsets.UTF_8, crossinline handler: BufferedReader.() -> Unit) {
    process.inputStream?.let {
      executor.execute {
        it.bufferedReader(charset).use { it.apply(handler) }
      }
    }
  }

  /**
   * Read the process's std-err with a [handler] and a specific [Charset]
   * @param charset Charset the char set that the std-err is intended to be read, default is [Charsets.UTF_8]
   * @param handler Function1<BufferedReader, Unit> code about how to handle the std-err
   */
  inline fun onStdErr(charset: Charset = Charsets.UTF_8, crossinline handler: BufferedReader.() -> Unit) {
    process.errorStream?.let {
      executor.execute {
        it.bufferedReader(charset).use { it.apply(handler) }
      }
    }
  }

  /**
   * Read the process's std-in with a [handler] and a specific [Charset]
   * @param charset Charset the char set that the std-in is intended to be used, default is [Charsets.UTF_8]
   * @param handler Function1<BufferedWriter, Unit> code about how to handle the std-in
   */
  inline fun onStdIn(charset: Charset = Charsets.UTF_8, crossinline handler: BufferedWriter.() -> Unit) {
    process.outputStream?.let {
      executor.execute {
        it.bufferedWriter(charset).use { it.apply(handler) }
      }
    }
  }

  /**
   * A convenience method for [onStdOut] that read the process's std-out line-by-line with a [lineHandler] and a specific [Charset].
   * @param charset Charset the char set that the std-out is intended to be read, default is [Charsets.UTF_8]
   * @param lineHandler Function1<String, Unit> code about how to handle each line in std-out
   */
  inline fun onStdOutEachLine(charset: Charset = Charsets.UTF_8, noinline lineHandler: (String) -> Unit) {
    onStdOut(charset) { forEachLine(lineHandler) }
  }

  /**
   * A convenience method for [onStdErr] that read the process's std-err line-by-line with a [lineHandler] and a specific [Charset].
   * @param charset Charset the char set that the std-err is intended to be read, default is [Charsets.UTF_8]
   * @param lineHandler Function1<String, Unit> code about how to handle each line in std-err
   */
  inline fun onStdErrEachLine(charset: Charset = Charsets.UTF_8, noinline lineHandler: (String) -> Unit) {
    onStdOut(charset) { forEachLine(lineHandler) }
  }

  /**
   * A convenience method to flush and discard the std-out and std-err stream.
   * In another word, let the process runs slightly.
   *
   * It's useful when std-out and std-err are not needed,
   * but don't want the process hangs due to not flushing streams
   */
  inline fun silently() {
    onStdOut { }
    onStdErr { }
  }

  /**
   * A convenience method to redirect std-out and std-err to a slf4j logger
   * with std-out messages are logged with [stdOutLevel] level, and std-err messages are logged with [stdErrLevel] level.
   * @param logger [Logger] the SLF4J logger where both std-out and std-err messages are delegated to
   * @param stdOutLevel [Level] the log level that std-out messages are logged with
   * @param stdErrLevel [Level] the log level that std-err messages are logged with
   */
  inline fun withLogger(logger: Logger, stdOutLevel: Level = Level.INFO, stdErrLevel: Level = Level.DEBUG) {
    val logStdOut = chooseLogMethod(stdOutLevel)
    onStdOutEachLine {
      logStdOut(it, logger)
    }
    val logStdErr = chooseLogMethod(stdErrLevel)
    onStdOutEachLine {
      logStdErr(it, logger)
    }
  }

  /**
   * A convenience method to write each string into std-in with [Charsets.UTF_8]
   * @param lines Array<out String> varargs or array of string that will be written to std-in line-by-line
   */
  inline fun withInputs(vararg lines: String) {
    withInputs(Charsets.UTF_8, *lines)
  }

  /**
   * A convenience method to write each string into std-in with a custom [Charset]
   * @param charset Charset the char set that the std-in is intended to be used
   * @param lines Array<out String> varargs or array of string that will be written to std-in line-by-line
   */
  inline fun withInputs(charset: Charset, vararg lines: String) {
    onStdIn(charset) { lines.forEach { this writeLine it } }
  }

  fun chooseLogMethod(level: Level): (String, Logger) -> Unit = when (level) {
    Level.TRACE -> { str, logger -> logger.trace(str) }
    Level.DEBUG -> { str, logger -> logger.debug(str) }
    Level.INFO -> { str, logger -> logger.info(str) }
    Level.WARN -> { str, logger -> logger.warn(str) }
    Level.ERROR -> { str, logger -> logger.error(str) }
  }
}

inline infix fun BufferedWriter.write(string: String): BufferedWriter = this.apply { write(string) }
inline infix fun BufferedWriter.writeLine(string: String): BufferedWriter = this.apply { write(string); newLine() }
