package mikufan.cx.vvd.taskproducer.component

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mikufan.cx.vocadbapiclient.model.SongForApiContract
import mikufan.cx.vvd.common.naming.FileNamePostFix
import mikufan.cx.vvd.common.naming.FileNameUtil
import mikufan.cx.vvd.commonkt.exception.orThrowVocaloidExp
import mikufan.cx.vvd.taskproducer.config.IOConfig
import mikufan.cx.vvd.taskproducer.model.VSongTask
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
  val objectMapper: ObjectMapper
) : RecordWriter<VSongTask> {
  val outputDirectory = ioConfig.outputDirectory

  override fun writeRecords(batch: Batch<VSongTask>) {
    runBlocking(Dispatchers.IO) {
      batch
        .map { it.payload }
        .forEach {
          launch {
            writeVSongJson(it)
          }
        }
    }
  }

  internal suspend fun writeVSongJson(vSongTask: VSongTask) {
    val song = vSongTask.parameters.songForApiContract.orThrowVocaloidExp("vsong is null")
    val fileBaseName = song.toProperFileName()

    // write info json
    val infoFileName = fileBaseName + FileNamePostFix.SONG_INFO + ".json"
    val infoFile = outputDirectory.resolve(infoFileName)
    objectMapper.writeValue(infoFile.toFile(), song)

    // write label json
    val labelFileName = fileBaseName + FileNamePostFix.LABEL + ".json"
    val label = vSongTask.label
    label.infoFileName = infoFileName
    val labelFile = outputDirectory.resolve(labelFileName)
    objectMapper.writeValue(labelFile.toFile(), label)

    log.info { "done processing, " +
        "wrote info json file to ${infoFile.absolute()} and label json file to ${labelFile.absolute()}" }
  }

  private fun SongForApiContract.toProperFileName(): String {
    val artists: List<String> = artistString.orThrowVocaloidExp("artist string is null")
      .split("feat.")
    val vocals = artists[1].trim()
    val producers = artists[0].trim()
    val songName: String = defaultName.orThrowVocaloidExp("song name is null")
    return FileNameUtil.removeIllegalChars(String.format("【%s】%s【%s】", vocals, songName, producers))
  }
}

private val log = KotlinLogging.logger {}
