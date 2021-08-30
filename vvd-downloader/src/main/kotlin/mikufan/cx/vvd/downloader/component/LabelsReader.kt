package mikufan.cx.vvd.downloader.component

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.common.naming.FileNamePostFix
import mikufan.cx.vvd.downloader.config.IOConfig
import org.jeasy.batch.core.reader.RecordReader
import org.jeasy.batch.core.record.GenericRecord
import org.jeasy.batch.core.record.Header
import org.jeasy.batch.core.record.Record
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.time.LocalDateTime

/**
 * @date 2021-08-30
 * @author CX无敌
 */
@Component
class LabelsReader(
  ioConfig: IOConfig,
  val objectMapper: ObjectMapper,
) : RecordReader<VSongLabel> {

  private val inputDirectory = ioConfig.inputDirectory

  private val fileItr = Files.list(inputDirectory)
    .filter { it.fileName.toString().contains(FileNamePostFix.LABEL) }
    .map { objectMapper.readValue<VSongLabel>(it.toFile()) }
    .sorted { l1, l2 ->
      // here, the order is a primitive type, so it will be 0 if unassigned,
      // and our order starts from 1
      l1.order.compareTo(l2.order)
    }
    .iterator()

  private var order = 0L

  override fun readRecord(): Record<VSongLabel>? {
    return if (fileItr.hasNext()) {
      val label = fileItr.next()
      val header = Header(++order, "Input Label File", LocalDateTime.now())
      return GenericRecord(header, label)
    } else {
      null
    }
  }
}
