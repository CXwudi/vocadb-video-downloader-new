package mikufan.cx.vvd.commonkt.exec

import java.util.concurrent.*

/**
 * Wait and sync on a running process, using a temporary [ThreadPoolExecutor]
 */
inline fun Process.sync(
  timeout: Long = 10,
  unit: TimeUnit = TimeUnit.MINUTES,
  setupHandler: ProcessSyncer.() -> Unit = { silently() }
): Process {
  val executor: ExecutorService = ThreadPoolExecutor(
    3, 3,
    timeout, unit,
    LinkedBlockingQueue(3), Executors.defaultThreadFactory()
  )
  val syncer = ProcessSyncer(executor, this)
  syncer.setupHandler()
  executor.shutdown()
  waitFor(timeout, unit)
  executor.awaitTermination(timeout, unit)
  return this
}

/**
 * Wait and sync on a running process, using an existing [ThreadPoolExecutor]
 */
inline fun Process.sync(
  timeout: Long = 10,
  unit: TimeUnit = TimeUnit.MINUTES,
  executor: ExecutorService,
  setupHandler: ProcessSyncer.() -> Unit = { silently() }
): Process {
  val syncer = ProcessSyncer(executor, this)
  syncer.setupHandler()
  waitFor(timeout, unit)
  executor.awaitTermination(timeout, unit)
  return this
}

// this requires Java 11
/**
 * Asynchronously sync on a running process, using a temporary [ThreadPoolExecutor]
 */
inline fun Process.async(
  setupHandler: ProcessSyncer.() -> Unit = { silently() }
): CompletableFuture<Process> {
  val executor: ExecutorService = ThreadPoolExecutor(
    3, 3,
    1, TimeUnit.MINUTES,
    LinkedBlockingQueue(3), Executors.defaultThreadFactory()
  )
  val syncer = ProcessSyncer(executor, this)
  syncer.setupHandler()
  executor.shutdown()
  return onExit()
}

/**
 * Asynchronously sync on a running process, using an existing [ThreadPoolExecutor]
 */
inline fun Process.async(
  executor: ExecutorService,
  setupHandler: ProcessSyncer.() -> Unit = { silently() }
): CompletableFuture<Process> {
  val syncer = ProcessSyncer(executor, this)
  syncer.setupHandler()
  return onExit()
}
