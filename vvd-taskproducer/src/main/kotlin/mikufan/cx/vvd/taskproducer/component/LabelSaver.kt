package mikufan.cx.vvd.taskproducer.component

import tools.jackson.databind.ObjectMapper
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.commonkt.batch.AbstractParallelWriter
import mikufan.cx.vvd.commonkt.batch.RecordErrorWriter
import mikufan.cx.vvd.taskproducer.config.IOConfig
import mikufan.cx.vvd.taskproducer.model.VSongTask
import org.jeasy.batch.core.record.Record
import org.springframework.stereotype.Component
import kotlin.io.path.absolute

/**
 * Save the -label json and any other files to the output directory.
 *
 * Each label json represent one task.
 * @date 2021-06-03
 * @author CX无敌
 */
@Component
class LabelSaver(
  ioConfig: IOConfig,
  private val objectMapper: ObjectMapper,
  recordErrorWriter: RecordErrorWriter
) : AbstractParallelWriter<VSongTask>(recordErrorWriter) {
  private val outputDirectory = ioConfig.outputDirectory

  override suspend fun write(record: Record<VSongTask>) {
    val song = requireNotNull(record.payload.parameters.songForApiContract) { "vsong is null" }
    val label = record.payload.label

    // write info json
    val infoFile = outputDirectory.resolve(label.infoFileName)
    objectMapper.writeValue(infoFile.toFile(), song)

    // write label json
    val labelFile = outputDirectory.resolve(label.labelFileName)
    objectMapper.writeValue(labelFile.toFile(), label)

    log.info { "Wrote info json file to ${infoFile.absolute()} and label json file to ${labelFile.absolute()}" }
  }
}

private val log = KInlineLogging.logger()

