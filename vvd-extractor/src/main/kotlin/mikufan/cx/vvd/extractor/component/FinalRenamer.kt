package mikufan.cx.vvd.extractor.component

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vocadbapiclient.model.SongForApiContract
import mikufan.cx.vvd.commonkt.naming.SongProperFileName
import mikufan.cx.vvd.commonkt.naming.removeIllegalChars
import mikufan.cx.vvd.commonkt.naming.renameWithSameExtension
import mikufan.cx.vvd.extractor.model.VSongTask
import mikufan.cx.vvd.extractor.util.OrderConstants
import org.jeasy.batch.core.processor.RecordProcessor
import org.jeasy.batch.core.record.Record
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.nio.file.Path

/**
 * @author CX无敌
 * 2022-12-03
 */
@Order(OrderConstants.FINAL_RENAMER_ORDER)
@Component
class FinalRenamer(
  private val finalRenamerCore: FinalRenamerCore,
) : RecordProcessor<VSongTask, VSongTask> {

  override fun processRecord(record: Record<VSongTask>): Record<VSongTask> {
    val label = record.payload.label
    val parameters = record.payload.parameters
    val audioFile = requireNotNull(parameters.processedAudioFile) { "null processed audio file?" }
    val songInfo = requireNotNull(parameters.songForApiContract) { "null song info?" }
    val vocaDbPvId = label.vocaDbPvId
    val newFile = finalRenamerCore.doProperRename(audioFile, songInfo, vocaDbPvId)
    parameters.processedAudioFile = newFile
    label.processedAudioFileName = newFile.fileName.toString()
    return record
  }
}

@Component
class FinalRenamerCore {

  fun doProperRename(
    audioFile: Path,
    songInfo: SongForApiContract,
    vocaDbPvId: Int,
  ): Path {
    val newFileName = generateProperName(songInfo, vocaDbPvId)
    val newFile = audioFile.renameWithSameExtension(newFileName.toString())
    log.info { "Rename to proper filename $newFileName" }
    return newFile
  }

  internal fun generateProperName(
    songInfo: SongForApiContract,
    vocaDbPvId: Int,
  ): SongProperFileName {
    val artists: List<String> = requireNotNull(songInfo.artistString) { "artist string is null" }.split("feat.")
    val vocals = artists[1].trim()
    val producers = artists[0].trim()
    val songName = requireNotNull(songInfo.defaultName) { "song name is null" }
    val pvInfo = requireNotNull(songInfo.pvs?.find { it.id == vocaDbPvId }) { "pv info is null" }
    val pvService = requireNotNull(pvInfo.service) { "pv service is null" }
    val pvId = requireNotNull(pvInfo.pvId) { "pv id is null" }
    val fileName = "【%s】%s【%s】[%s %s]".format(vocals, songName, producers, pvService, pvId)
    return SongProperFileName(removeIllegalChars(fileName)) // remove illegal chars
  }
}

private val log = KInlineLogging.logger()
