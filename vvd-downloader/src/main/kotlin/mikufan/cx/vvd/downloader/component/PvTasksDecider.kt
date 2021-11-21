package mikufan.cx.vvd.downloader.component

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vocadbapiclient.model.PVContract
import mikufan.cx.vocadbapiclient.model.PVType
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.downloader.config.Preference
import mikufan.cx.vvd.downloader.config.validation.SUPPORTED_SERVICES
import mikufan.cx.vvd.downloader.model.PVTask
import mikufan.cx.vvd.downloader.model.VSongTask
import mikufan.cx.vvd.downloader.util.toPVServicesEnum
import org.jeasy.batch.core.processor.RecordProcessor
import org.jeasy.batch.core.record.Record
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.function.ToIntFunction

/**
 * @date 2021-10-26
 * @author CX无敌
 */
@Component
@Order(3)
class PvTasksDecider(
  private val preference: Preference
) : RecordProcessor<VSongTask, VSongTask> {

  /**
   * service to int map used for comparator
   */
  private val serviceToIntMap = preference.pvPreference.withIndex().associate { Pair(it.value, it.index) }

  private val pvTypeComparator = Comparator<PVContract> { p1, p2 ->
    when {
      p1.pvType == p2.pvType -> 0 // if both are same type, do nothing
      // here two pvs has different types
      p1.pvType == PVType.ORIGINAL -> -1 // if first one is original
      p2.pvType == PVType.ORIGINAL -> 1 // if second one is original
      // else don't touch
      else -> 0
    }
  }
  private val pvServiceKeyExtractor = ToIntFunction<PVContract> {
    requireNotNull(
      serviceToIntMap[requireNotNull(it.service?.toPVServicesEnum()) { "the pv service enum is null in PV info?" }]
    ) { "Did we failed to filter out un-supported PV services?" }
  }

  override fun processRecord(record: Record<VSongTask>): Record<VSongTask> {
    val (_, parameters) = record.payload
    val songInfo = requireNotNull(parameters.songForApiContract) { "null song info?" }
    val pvs = requireNotNull(songInfo.pvs) { "null pvs in song info?" }

    log.info { "Deciding pv tasks" }
    // check if empty
    throwAndLogIfNoPvsLeft(pvs) { "${songInfo.defaultName} doesn't have any available PVs, skip downloading" }

    // filter out pv services that users are not interested
    val supportedPvs = pvs.filter { it.service?.toPVServicesEnum() in preference.pvPreference }
    // after filter check empty
    throwAndLogIfNoPvsLeft(supportedPvs) {
      "${songInfo.defaultName} doesn't have any PVs that are in our supported PV services $SUPPORTED_SERVICES, " +
          "please consider manually downloading this song's PV"
    }

    // sort pv base on pv service preference and reprint's priority
    val sortedPvs = if (preference.tryAllOriginalPvsBeforeReprintedPvs) {
      supportedPvs.sortedWith(
        Comparator
          .nullsLast(pvTypeComparator)
          .thenComparingInt(pvServiceKeyExtractor)
      )
    } else {
      supportedPvs.sortedWith(
        Comparator
          .comparingInt(pvServiceKeyExtractor)
          .thenComparing(pvTypeComparator)
      )
    }

    // decided should we try reprint pvs
    val reprintDecidedPvs = if (!preference.tryReprintedPv) {
      sortedPvs.filter { it.pvType != PVType.REPRINT } // let's allow "Other" type for now
    } else sortedPvs
    log.debug { "after deciding reprint = $reprintDecidedPvs" }
    // after filter check empty
    throwAndLogIfNoPvsLeft(reprintDecidedPvs) {
      "By filtering out all reprint PVs, ${songInfo.defaultName} doesn't have any available PVs, " +
          "consider enabling reprint PVs for this song"
    }

    // decided should we move on next pv service if failed
    val serviceDecidedPvs = if (!preference.tryNextPvServiceOnFail) {
      // remember that the first available pv can be in any service, so always use pvList[0], not pvPreference[0]
      val firstServ = reprintDecidedPvs.first().service
      reprintDecidedPvs.takeWhile { it.service?.equals(firstServ) ?: false }
    } else reprintDecidedPvs
    log.debug { "after deciding services = $serviceDecidedPvs" }

    parameters.pvCandidates = serviceDecidedPvs.map { PVTask(it) }
      .also { log.info { "Done deciding PV tasks, tasks = $it" } }
    return record
  }

  private inline fun throwAndLogIfNoPvsLeft(
    pvs: List<PVContract>,
    expMsgFunc: () -> String
  ) {
    if (pvs.isEmpty()) {
      val expMsg = expMsgFunc()
      log.warn { expMsg }
      throw RuntimeVocaloidException(expMsg)
    }
  }
}

private val log = KInlineLogging.logger()
