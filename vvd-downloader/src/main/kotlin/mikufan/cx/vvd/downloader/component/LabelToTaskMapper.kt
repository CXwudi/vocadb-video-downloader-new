package mikufan.cx.vvd.downloader.component

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import mikufan.cx.vocadbapiclient.model.SongForApiContract
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.downloader.config.IOConfig
import mikufan.cx.vvd.downloader.model.Parameters
import mikufan.cx.vvd.downloader.model.VSongTask
import org.jeasy.batch.core.mapper.RecordMapper
import org.jeasy.batch.core.record.GenericRecord
import org.jeasy.batch.core.record.Header
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
class LabelToTaskMapper(
  ioConfig: IOConfig,
  private val objectMapper: ObjectMapper
) : RecordMapper<VSongLabel, VSongTask> {

  private val inputDirectory = ioConfig.inputDirectory

  override fun processRecord(record: Record<VSongLabel>): Record<VSongTask> {
    val oldLabel = record.payload
    // creating new label instance in case if user want to re-download a song
    // in that case, the label file is probably moved from the output directory of this module to the input directory
    val label = VSongLabel.builder()
      .order(oldLabel.order)
      .infoFileName(oldLabel.infoFileName)
      .build()
    val song = objectMapper.readValue<SongForApiContract>(
      (inputDirectory / label.infoFileName).toFile()
    )
    val params = Parameters(song)
    val task = VSongTask(label, params)
    val header = Header(label.order, "Input Label and Song Info", record.header.creationDate)
    return GenericRecord(header, task)
  }
}
