package mikufan.cx.vvd.commonkt.threading

import java.util.concurrent.ThreadFactory

/**
 * The thread factory for syncing streams from a running external process,
 * where all thread name are prefixed by [baseName],
 * and the order of thread name is `stdout`, `stderr`, `stdin`.
 *
 * The [newThread] method is not thread-safe.
 *
 * @param baseName the base name of thread name
 */
class ExternalProcessThreadFactory(baseName: String) : ThreadFactory {

  private val names = listOf("$baseName-stdout", "$baseName-stderr", "$baseName-stdin")
  private var counter = 0

  override fun newThread(r: Runnable): Thread {
    return Thread.ofVirtual().name(names[counter++ % names.size]).start(r)
  }

  /**
   * reset the counter so that next thread name is `stdout`
   */
  fun resetCounter() {
    setCounter(0)
  }

  /**
   * manually set the counter to get a desired thread name
   * @param counter Int 1 -> `stdout`, 2 -> `stderr`, 3 -> `stdin`
   */
  fun setCounter(counter: Int) {
    this.counter = counter
  }
}
