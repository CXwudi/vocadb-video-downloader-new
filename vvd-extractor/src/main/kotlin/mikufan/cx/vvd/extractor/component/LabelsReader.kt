package mikufan.cx.vvd.extractor.component

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.common.naming.FileNamePostFix
import mikufan.cx.vvd.extractor.config.IOConfig
import mikufan.cx.vvd.extractor.model.Parameters
import mikufan.cx.vvd.extractor.model.VSongTask
import org.jeasy.batch.core.reader.RecordReader
import org.jeasy.batch.core.record.GenericRecord
import org.jeasy.batch.core.record.Header
import org.jeasy.batch.core.record.Record
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.time.LocalDateTime

/**
 * @date 2022-06-12
 * @author CX无敌
 */
@Component
class LabelsReader(
  ioConfig: IOConfig,
  private val objectMapper: ObjectMapper,
) : RecordReader<VSongTask> {

  private val inputDir = ioConfig.inputDirectory
  private val fileItr = Files.list(inputDir)
    .filter { it.fileName.toString().contains(FileNamePostFix.LABEL) }
    // is ok to leave the
    .peek { log.debug { "reading label $it" } }
    .map { objectMapper.readValue<VSongLabel>(it.toFile()) }
    .sorted { l1, l2 -> // because of the sorting, we have to load all file names to RAM
      // but since label file contains less info, it shouldn't cost too much RAM when there is a lots of them
      // here, the order is a primitive type, so it will be 0 if unassigned,
      // and our order starts from 1
      // also, the order is not strictly ordered, it allows gaps and duplicates order number, since user can mix their folders or whatever preprocessing
      l1.order.compareTo(l2.order)
    }
    .iterator()

  private var order = 0L

  override fun readRecord(): Record<VSongTask>? = if (fileItr.hasNext()) {
    val oldLabel = fileItr.next()
    // creating new label instance in case if user want to re-extract a song
    // as usual, transfer the needed info from old label
    val label = VSongLabel.builder()
      .labelFileName(oldLabel.labelFileName)
      .infoFileName(oldLabel.infoFileName)
      .order(oldLabel.order)
      .pvFileName(oldLabel.pvFileName)
      .audioFileName(oldLabel.audioFileName)
      .thumbnailFileName(oldLabel.thumbnailFileName)
      .pvVocaDbId(oldLabel.pvVocaDbId)
      .downloaderName(oldLabel.downloaderName)
      .build()
    val header = Header(++order, "VSong Task by ${label.labelFileName}", LocalDateTime.now())
    log.info { "Start processing ${label.labelFileName}" }
    GenericRecord(header, VSongTask(label, Parameters()))
  } else {
    null
  }
}

private val log = KInlineLogging.logger()
