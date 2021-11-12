package mikufan.cx.vvd.downloader.component

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.downloader.config.Preference
import mikufan.cx.vvd.downloader.model.VSongTask
import mikufan.cx.vvd.downloader.util.toPVServicesEnum
import org.jeasy.batch.core.processor.RecordProcessor
import org.jeasy.batch.core.record.Record
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

/**
 * @date 2021-10-26
 * @author CX无敌
 */
@Component
@Order(3)
class PvsOrderDecider(
  private val preference: Preference
) : RecordProcessor<VSongTask, VSongTask> {

  override fun processRecord(record: Record<VSongTask>): Record<VSongTask> {
    val (label, parameters) = record.payload
    val songInfo = requireNotNull(parameters.songForApiContract) { "null song info?" }
    val pvs = requireNotNull(songInfo.pvs) { "null pvs in song info?" }

    if (pvs.isEmpty()) {
      throw RuntimeVocaloidException("${songInfo.defaultName} doesn't have any available PVs, skip downloading")
        .also { log.warn { it.message } }
    }

//    val availablePvs =
//      pvs.filter { !requireNotNull(it.disabled) { "can not determine if ${it.name} from ${it.service} is a disabled PV" } }

//    if (pvs.isEmpty()) {
//      throw RuntimeVocaloidException("After filtering out disabled PVs, ${songInfo.defaultName} doesn't have any available PVs, skip downloading")
//        .also { log.warn { it.message } }
//    }

    val serviceToIntMap = preference.pvPreference.withIndex().associate { Pair(it.value, it.index) }
    val sortByServPvs = pvs.sortedBy {
      serviceToIntMap[requireNotNull(it.service?.toPVServicesEnum()) { "the pv service enum is null in PV info?" }]
    }

    val serviceFilteredPvs = if (!preference.tryNextPvServiceOnFail) {
      val firstServ = preference.pvPreference[0]
      sortByServPvs.filter { it.service?.toPVServicesEnum()?.equals(firstServ) ?: false }
    } else sortByServPvs

    TODO()
  }
}

private val log = KInlineLogging.logger()
