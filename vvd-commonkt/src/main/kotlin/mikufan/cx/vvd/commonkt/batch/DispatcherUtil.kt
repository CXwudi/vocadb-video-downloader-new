package mikufan.cx.vvd.commonkt.batch

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

val Dispatchers.LOOM: ExecutorCoroutineDispatcher
  get() = Executors.newVirtualThreadPerTaskExecutor()
    .asCoroutineDispatcher()