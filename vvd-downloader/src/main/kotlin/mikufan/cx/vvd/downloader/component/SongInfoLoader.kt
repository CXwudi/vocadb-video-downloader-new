package mikufan.cx.vvd.downloader.component

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vocadbapiclient.model.SongForApiContract
import mikufan.cx.vvd.commonkt.naming.toProperFileName
import mikufan.cx.vvd.downloader.config.IOConfig
import mikufan.cx.vvd.downloader.model.VSongTask
import org.jeasy.batch.core.processor.RecordProcessor
import org.jeasy.batch.core.record.Record
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import kotlin.io.path.div

/**
 * @date 2021-08-30
 * @author CX无敌
 */
@Component
@Order(2)
class SongInfoLoader(
  ioConfig: IOConfig,
  private val objectMapper: ObjectMapper
) : RecordProcessor<VSongTask, VSongTask> {

  private val inputDirectory = ioConfig.inputDirectory

  override fun processRecord(record: Record<VSongTask>): Record<VSongTask> {
    val task = record.payload
    val label = task.label
    val song = objectMapper.readValue<SongForApiContract>(
      (inputDirectory / label.infoFileName).toFile()
    )
    log.info { "Read song info for ${song.toProperFileName()}" }
    task.parameters.apply {
      songProperFileName = song.toProperFileName()
      songForApiContract = song
    }
    return record
  }
}

private val log = KInlineLogging.logger()
