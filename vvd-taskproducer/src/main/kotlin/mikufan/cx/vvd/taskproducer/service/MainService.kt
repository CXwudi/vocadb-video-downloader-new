package mikufan.cx.vvd.taskproducer.service

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.commonkt.batch.PipelineExceptionHandler
import mikufan.cx.vvd.taskproducer.component.*
import mikufan.cx.vvd.taskproducer.config.SystemConfig
import mikufan.cx.vvd.taskproducer.model.VSongTask
import org.jeasy.batch.core.job.JobBuilder
import org.jeasy.batch.core.job.JobExecutor
import org.springframework.stereotype.Service

/**
 * @date 2021-05-29
 * @author CX无敌
 */
@Service
class MainService(
  private val listReader: ListReader,
  private val artistFieldFixer: ArtistFieldFixer,
  private val labelInfoRecorder: LabelInfoRecorder,
  private val beforeWriteValidator: BeforeWriteValidator,
  private val vSongJsonWriter: VSongJsonWriter,
  private val pipelineExceptionHandler: PipelineExceptionHandler,
  private val systemConfig: SystemConfig
) : Runnable {

  override fun run() {
    val job = JobBuilder<VSongTask, VSongTask>()
      .named("read VocaDB list task")
      .batchSize(systemConfig.batchSize)
      .reader(listReader)
      .processor(artistFieldFixer)
      .processor(labelInfoRecorder)
      .validator(beforeWriteValidator)
      .writer(vSongJsonWriter)
      .pipelineListener(pipelineExceptionHandler)
      .build()

    val jobReport = JobExecutor().use {
      it.execute(job)
    }

    log.info { "やった！続きはPVをダウンロードするに行くぞ \n$jobReport" }
  }
}

private val log = KInlineLogging.logger()
