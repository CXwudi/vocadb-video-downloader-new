package mikufan.cx.vvd.downloader.service

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.downloader.component.BeforeProcessLabelValidator
import mikufan.cx.vvd.downloader.component.LabelToTaskMapper
import mikufan.cx.vvd.downloader.component.LabelsReader
import mikufan.cx.vvd.downloader.model.VSongTask
import org.jeasy.batch.core.job.JobBuilder
import org.springframework.stereotype.Service

/**
 * @date 2021-08-30
 * @author CX无敌
 */
@Service
class MainService(
  val labelsReader: LabelsReader,
  val beforeProcessLabelValidator: BeforeProcessLabelValidator,
  val labelToTaskMapper: LabelToTaskMapper,
) : Runnable {

  override fun run() {
    val job = JobBuilder<VSongLabel, VSongTask>()
      .named("read VocaDB list task")
      // this is fixed to 1 to allow label and info files moved and saved immediately after the downloading
      .batchSize(1)
      .reader(labelsReader)
      .validator(beforeProcessLabelValidator)
      .mapper(labelToTaskMapper)
  }
}

private val log = KInlineLogging.logger()
