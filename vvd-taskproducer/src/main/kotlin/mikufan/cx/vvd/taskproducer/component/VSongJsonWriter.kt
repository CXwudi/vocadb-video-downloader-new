package mikufan.cx.vvd.taskproducer.component

import com.fasterxml.jackson.databind.ObjectMapper
import mikufan.cx.vvd.commonkt.batch.AbstractParallelWriter
import mikufan.cx.vvd.commonkt.batch.RecordErrorWriter
import mikufan.cx.vvd.commonkt.exception.requireNotNull
import mikufan.cx.vvd.taskproducer.config.IOConfig
import mikufan.cx.vvd.taskproducer.model.VSongTask
import mikufan.cx.vvd.taskproducer.util.toLabelFileName
import mu.KotlinLogging
import org.jeasy.batch.core.record.Record
import org.springframework.stereotype.Component
import kotlin.io.path.absolute

/**
 * @date 2021-06-03
 * @author CX无敌
 */
@Component
class VSongJsonWriter(
  ioConfig: IOConfig,
  private val objectMapper: ObjectMapper,
  recordErrorWriter: RecordErrorWriter
) : AbstractParallelWriter<VSongTask>(recordErrorWriter) {
  private val outputDirectory = ioConfig.outputDirectory

  override suspend fun write(record: Record<VSongTask>) {
    val song = record.payload.parameters.songForApiContract.requireNotNull{ "vsong is null" }
    val label = record.payload.label

    // write info json
    val infoFile = outputDirectory.resolve(label.infoFileName)
    objectMapper.writeValue(infoFile.toFile(), song)

    // write label json
    val labelFile = outputDirectory.resolve(song.toLabelFileName())
    objectMapper.writeValue(labelFile.toFile(), label)

    log.info {
      "done processing ${song.defaultName}, " +
          "wrote info json file to ${infoFile.absolute()} and label json file to ${labelFile.absolute()}"
    }
  }
}

private val log = KotlinLogging.logger {}
