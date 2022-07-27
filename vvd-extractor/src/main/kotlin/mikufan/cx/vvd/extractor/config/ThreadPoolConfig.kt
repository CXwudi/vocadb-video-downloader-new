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

  @Bean("mediainfoThreadPool")
  fun mediainfoThreadPool(batchConfig: BatchConfig, processConfig: ProcessConfig) = ThreadPoolExecutor(
    // so far only use the stdout of mediainfo to extract info
    // also there are two places of using mediainfo
    // hence size = batchSize * 2
    batchConfig.batchSize * 2,
    batchConfig.batchSize * 2,
    processConfig.timeout,
    processConfig.unit,
    LinkedBlockingDeque(),
    SharedExternalProcessThreadFactory("mediainfo-runner-t")
  )

  @Bean("extractorThreadPool")
  fun extractorThreadPool(batchConfig: BatchConfig, processConfig: ProcessConfig) = ThreadPoolExecutor(
    batchConfig.batchSize * 3,
    batchConfig.batchSize * 3,
    processConfig.timeout,
    processConfig.unit,
    LinkedBlockingDeque(),
    SharedExternalProcessThreadFactory("cli-extractor-t")
  )

  @Bean("taggerThreadPool")
  fun taggerThreadPool(batchConfig: BatchConfig, processConfig: ProcessConfig) = ThreadPoolExecutor(
    batchConfig.batchSize * 3,
    batchConfig.batchSize * 3,
    processConfig.timeout,
    processConfig.unit,
    LinkedBlockingDeque(),
    SharedExternalProcessThreadFactory("cli-tagger-t")
  )

  private fun SharedExternalProcessThreadFactory(name: String): ThreadFactory =
    object : ThreadFactory {
      private val counter = AtomicInteger(0)
      override fun newThread(r: Runnable): Thread {
        return Thread(r, "$name${counter.getAndIncrement()}")
      }
    }
}
