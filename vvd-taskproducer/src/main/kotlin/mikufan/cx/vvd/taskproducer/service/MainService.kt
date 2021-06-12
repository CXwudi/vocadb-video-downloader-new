package mikufan.cx.vvd.taskproducer.service

import mikufan.cx.vvd.taskproducer.component.*
import mikufan.cx.vvd.taskproducer.config.SystemConfig
import mikufan.cx.vvd.taskproducer.model.VSongTask
import mu.KotlinLogging
import org.jeasy.batch.core.job.JobBuilder
import org.jeasy.batch.core.job.JobExecutor
import org.springframework.stereotype.Service
import javax.validation.Valid

/**
 * @date 2021-05-29
 * @author CX无敌
 */
@Service
class MainService(
  private val listReader: ListReader,
  private val artistFieldFixer: ArtistFieldFixer,
  private val vSongFileNameGenerator: VSongFileNameGenerator,
  private val beforeWriteValidator: BeforeWriteValidator,
  private val vSongJsonWriter: VSongJsonWriter,
  private val pipelineExceptionHandler: PipelineExceptionHandler,
  @Valid private val systemConfig: SystemConfig
) : Runnable {

  override fun run() {
    val job = JobBuilder<VSongTask, VSongTask>()
      .batchSize(systemConfig.batchSize)
      .reader(listReader)
      .processor(artistFieldFixer)
      .processor(vSongFileNameGenerator)
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

private val log = KotlinLogging.logger {}
