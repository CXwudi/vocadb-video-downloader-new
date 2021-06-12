package mikufan.cx.vvd.taskproducer.component

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mikufan.cx.vvd.commonkt.exception.orThrowVocaloidExp
import mikufan.cx.vvd.taskproducer.config.IOConfig
import mikufan.cx.vvd.taskproducer.model.VSongTask
import mikufan.cx.vvd.taskproducer.util.toLabelFileName
import mu.KotlinLogging
import org.jeasy.batch.core.record.Batch
import org.jeasy.batch.core.writer.RecordWriter
import org.springframework.stereotype.Component
import kotlin.io.path.absolute

/**
 * @date 2021-06-03
 * @author CX无敌
 */
@Component
class VSongJsonWriter(
  ioConfig: IOConfig,
  private val objectMapper: ObjectMapper,
  private val recordErrorWriter: RecordErrorWriter
) : RecordWriter<VSongTask> {
  private val outputDirectory = ioConfig.outputDirectory

  /**
   * When we reach here, label should contains all info needed before writing.
   *
   * so here we only need to concerned about writing
   */
  override fun writeRecords(batch: Batch<VSongTask>) = runBlocking(Dispatchers.IO) {
    batch.forEach {
      launch {
        try {
          writeVSongJson(it.payload)
        } catch (e: Exception) {
          recordErrorWriter.writeError(it, e)
        }
      }
    }
  }

  internal suspend fun writeVSongJson(vSongTask: VSongTask) {
    val song = vSongTask.parameters.songForApiContract.orThrowVocaloidExp("vsong is null")
    val label = vSongTask.label

    // write info json
    val infoFile = outputDirectory.resolve(label.infoFileName)
    objectMapper.writeValue(infoFile.toFile(), song)

    // write label json
    val labelFile = outputDirectory.resolve(song.toLabelFileName())
    objectMapper.writeValue(labelFile.toFile(), label)

    log.info {
      "done processing ${song.defaultName}, " +
          "wrote info json file to ${infoFile.absolute()} and label json file to ${labelFile.absolute()}"
    }
  }
}

private val log = KotlinLogging.logger {}
