package mikufan.cx.vvd.taskproducer.config

import com.fasterxml.jackson.databind.ObjectMapper
import mikufan.cx.vvd.commonkt.batch.PipelineExceptionHandler
import mikufan.cx.vvd.commonkt.batch.RecordErrorWriter
import mikufan.cx.vvd.commonkt.naming.toErrorFileName
import mikufan.cx.vvd.taskproducer.model.VSongTask
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackageClasses = [PipelineExceptionHandler::class])
class ErrorPathConfig {

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
