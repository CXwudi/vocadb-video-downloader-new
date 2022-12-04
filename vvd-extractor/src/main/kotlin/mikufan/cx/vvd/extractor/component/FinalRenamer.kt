package mikufan.cx.vvd.extractor.component

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vocadbapiclient.model.SongForApiContract
import mikufan.cx.vvd.commonkt.naming.removeIllegalChars
import mikufan.cx.vvd.extractor.model.VSongTask
import mikufan.cx.vvd.extractor.util.OrderConstants
import org.jeasy.batch.core.processor.RecordProcessor
import org.jeasy.batch.core.record.Record
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.nio.file.Path
import kotlin.io.path.extension

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
    parameters.processedAudioFile = finalRenamerCore.doProperRename(audioFile, songInfo, vocaDbPvId)
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
    val newFileName = "${generateProperName(songInfo, vocaDbPvId)}.${audioFile.extension}"
    val newFile = audioFile.resolveSibling(newFileName)
    audioFile.toFile().renameTo(newFile.toFile())
    log.info { "Renamed" }
    return newFile
  }

  internal fun generateProperName(
    songInfo: SongForApiContract,
    vocaDbPvId: Int,
  ): String {
    val artists: List<String> = requireNotNull(songInfo.artistString) { "artist string is null" }.split("feat.")
    val vocals = artists[1].trim()
    val producers = artists[0].trim()
    val songName: String = requireNotNull(songInfo.defaultName) { "song name is null" }
    val pvInfo = requireNotNull(songInfo.pvs?.find { it.id == vocaDbPvId }) { "pv info is null" }
    val pvService = requireNotNull(pvInfo.service?.name) { "pv service is null" }
    val pvId = requireNotNull(pvInfo.pvId) { "pv id is null" }
    return removeIllegalChars("$songName - $vocals feat. $producers [$pvService $pvId]")
  }
}

private val log = KInlineLogging.logger()
