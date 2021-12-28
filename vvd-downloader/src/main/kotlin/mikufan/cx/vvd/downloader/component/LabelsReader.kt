package mikufan.cx.vvd.downloader.component

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.common.naming.FileNamePostFix
import mikufan.cx.vvd.downloader.config.IOConfig
import mikufan.cx.vvd.downloader.model.Parameters
import mikufan.cx.vvd.downloader.model.VSongTask
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
) : RecordReader<VSongTask> {

  private val inputDirectory = ioConfig.inputDirectory

  private val fileItr = Files.list(inputDirectory)
    .filter { it.fileName.toString().contains(FileNamePostFix.LABEL) }
    // is ok to leave the
    .peek { log.debug { "reading label $it" } }
    .map { objectMapper.readValue<VSongLabel>(it.toFile()) }
    .sorted { l1, l2 -> // because of the sorting, we have to load all file names to RAM
      // here, the order is a primitive type, so it will be 0 if unassigned,
      // and our order starts from 1
      l1.order.compareTo(l2.order)
    }
    .iterator()

  private var order = 0L

  override fun readRecord(): Record<VSongTask>? = if (fileItr.hasNext()) {
    val oldLabel = fileItr.next()
    // creating new label instance in case if user want to re-download a song
    // in that case, the label file is probably moved from the output directory of this module to the input directory
    // this can avoid bug caused by overwriting labels where both old and new label info present
    val label = VSongLabel.builder()
      .order(oldLabel.order) // remember that users can mix their folders, given multiple same order
      .labelFileName(oldLabel.labelFileName)
      .infoFileName(oldLabel.infoFileName)
      .build()
    val header = Header(++order, "VSong Task by ${label.labelFileName}", LocalDateTime.now())
    log.info { "Start processing ${label.labelFileName}" }
    GenericRecord(header, VSongTask(label, Parameters()))
  } else {
    null
  }
}

private val log = KInlineLogging.logger()
