package mikufan.cx.vvd.extractor.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.atomic.AtomicInteger

/**
 * @date 2022-06-12
 * @author CX无敌
 */
@Configuration
class ThreadPoolConfig(
  batchConfig: BatchConfig,
  private val processConfig: ProcessConfig
) {
  private val batchSize = batchConfig.batchSize

  @Bean fun poolForExternalProcess() = ThreadPoolExecutor(
    batchSize * 2, batchSize * 2,
    processConfig.timeout, processConfig.unit,
    LinkedBlockingDeque(),
    ExternalProcessThreadFactory()
  )
}

private class ExternalProcessThreadFactory : ThreadFactory {

  private val counter = AtomicInteger(0)
  override fun newThread(r: Runnable): Thread {
    return Thread(r, "external process stream syncer - ${counter.incrementAndGet()}")
  }
}
