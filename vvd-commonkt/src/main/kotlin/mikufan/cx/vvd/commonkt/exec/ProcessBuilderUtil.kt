package mikufan.cx.vvd.commonkt.exec

import java.nio.file.Path

inline fun runCmd(vararg command: String, builder: ProcessBuilder.() -> Unit = {}): Process =
  ProcessBuilder(*command).apply(builder).start()

var ProcessBuilder.directory: Path
  get() = this.directory().toPath()
  set(value) {
    this.directory(value.toFile())
  }

var ProcessBuilder.redirectOutput: ProcessBuilder.Redirect
  get() = this.redirectOutput()
  set(value) {
    this.redirectOutput(value)
  }

var ProcessBuilder.redirectError: ProcessBuilder.Redirect
  get() = this.redirectError()
  set(value) {
    this.redirectError(value)
  }

var ProcessBuilder.redirectErrorStream: Boolean
  get() = this.redirectErrorStream()
  set(value) {
    this.redirectErrorStream(value)
  }

var ProcessBuilder.redirectInput: ProcessBuilder.Redirect
  get() = this.redirectInput()
  set(value) {
    this.redirectInput(value)
  }

val ProcessBuilder.environment: MutableMap<String, String>
  get() = this.environment()
