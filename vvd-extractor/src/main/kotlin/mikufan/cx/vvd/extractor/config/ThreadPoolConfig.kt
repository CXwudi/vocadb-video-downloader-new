package mikufan.cx.vvd.extractor.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.atomic.AtomicInteger

/**
 * @date 2022-07-23
 * @author CX无敌
 */
@Configuration
class ThreadPoolConfig {

  @Bean("extractorThreadPool")
  fun extractorThreadPool(batchConfig: BatchConfig, processConfig: ProcessConfig) = ThreadPoolExecutor(
    batchConfig.batchSize * 3,
    batchConfig.batchSize * 3,
    processConfig.timeout,
    processConfig.unit,
    LinkedBlockingDeque(),
    SharedExternalProcessThreadFactory("base-cli-extractor")
  )

  @Bean("taggerThreadPool")
  fun taggerThreadPool(batchConfig: BatchConfig, processConfig: ProcessConfig) = ThreadPoolExecutor(
    batchConfig.batchSize * 3,
    batchConfig.batchSize * 3,
    processConfig.timeout,
    processConfig.unit,
    LinkedBlockingDeque(),
    SharedExternalProcessThreadFactory("base-mutagen-tagger")
  )

  private fun SharedExternalProcessThreadFactory(name: String): ThreadFactory =
    object : ThreadFactory {
      private val counter = AtomicInteger(0)
      override fun newThread(r: Runnable): Thread {
        return Thread(r, "$name-${counter.getAndIncrement()}")
      }
    }
}
