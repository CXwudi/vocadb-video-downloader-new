package mikufan.cx.vvd.downloader.component

import com.fasterxml.jackson.databind.ObjectMapper
import mikufan.cx.vvd.commonkt.batch.RecordErrorWriter
import mikufan.cx.vvd.commonkt.naming.toErrorFileName
import mikufan.cx.vvd.downloader.config.IOConfig
import mikufan.cx.vvd.downloader.model.VSongTask
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @date 2021-09-28
 * @author CX无敌
 */
@Configuration
class ErrorWriterConfig {

  @Bean
  fun errorWriter(ioConfig: IOConfig, objectMapper: ObjectMapper) =
    RecordErrorWriter(ioConfig.errorDirectory, objectMapper) {
      val payload = it.payload
      if (payload is VSongTask) {
        payload.parameters.songForApiContract?.toErrorFileName() ?: "unknown song ${it.header}.json"
      } else {
        ""
      }
    }
}
