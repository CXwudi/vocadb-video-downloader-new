package mikufan.cx.vvd.extractor.component

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.extractor.model.VSongTask
import mikufan.cx.vvd.extractor.util.OrderConstants
import org.jeasy.batch.core.processor.RecordProcessor
import org.jeasy.batch.core.record.Record
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.time.InstantSource
import kotlin.io.path.setLastModifiedTime

/**
 * @author CX无敌
 * 2022-11-22
 */
@Order(OrderConstants.LAST_MODIFIED_CHANGER)
@Component
class LastModifiedChanger(
  instantSource: InstantSource,
) : RecordProcessor<VSongTask, VSongTask> {

  private val lastModifiedChangerCore = LastModifiedChangerCore(instantSource)

  override fun processRecord(record: Record<VSongTask>): Record<VSongTask> {
    val payload = record.payload
    val parameters = payload.parameters
    val processedAudioFile = requireNotNull(parameters.processedAudioFile) { "null optional processed audio file?" }
    val order = payload.label.order
    lastModifiedChangerCore.changeLastModifiedTimeByOrder(processedAudioFile, order)
    return record
  }
}

class LastModifiedChangerCore(
  instantSource: InstantSource,
) {
  private val now by lazy { instantSource.instant() }

  fun changeLastModifiedTimeByOrder(processedAudioFile: Path, order: Long) {
    val newLastModifiedTime = now.plusSeconds(order)
    processedAudioFile.setLastModifiedTime(FileTime.from(newLastModifiedTime))
    log.info { "Set last modified time of $processedAudioFile to $newLastModifiedTime" }
  }
}

private val log = KInlineLogging.logger()
