package mikufan.cx.vvd.downloader.component

import tools.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.commonkt.batch.AbstractParallelWriter
import mikufan.cx.vvd.commonkt.batch.RecordErrorWriter
import mikufan.cx.vvd.downloader.config.IOConfig
import mikufan.cx.vvd.downloader.model.VSongTask
import org.jeasy.batch.core.record.Record
import org.springframework.stereotype.Component
import kotlin.io.path.absolute
import kotlin.io.path.deleteExisting
import kotlin.io.path.div
import kotlin.io.path.moveTo

/**
 * Save the -label json and any other files to the output directory.
 *
 * Each label json represent one task.
 * @date 2022-06-02
 * @author CX无敌
 */
@Component
class LabelSaver(
  ioConfig: IOConfig,
  private val objectMapper: ObjectMapper,
  recordErrorWriter: RecordErrorWriter
) : AbstractParallelWriter<VSongTask>(recordErrorWriter) {

  private val inputDirectory = ioConfig.inputDirectory
  private val outputDirectory = ioConfig.outputDirectory

  /**
   * write a single record to somewhere.
   *
   * blocking IO is fine here as this suspend fun is ran in [Dispatchers.IO]
   */
  override suspend fun write(record: Record<VSongTask>) {
    val (label, parameters) = record.payload

    val movedInfoFile = outputDirectory / label.infoFileName
    inputDirectory.resolve(label.infoFileName).moveTo(movedInfoFile)

    val newLabelFile = outputDirectory / label.labelFileName
    objectMapper.writeValue(newLabelFile.toFile(), label)
    inputDirectory.resolve(label.labelFileName).deleteExisting() // the old label can be deleted

    log.info {
      "Done all tasks for ${parameters.songProperFileName}. " +
          "Saved new label json file to ${newLabelFile.absolute()} and deleted the old one from input directory. " +
          "Moved the song info json file from input directory to ${movedInfoFile.absolute()}"
    }
  }
}

private val log = KInlineLogging.logger()

