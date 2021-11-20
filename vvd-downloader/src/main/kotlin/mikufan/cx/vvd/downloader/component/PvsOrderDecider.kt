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
    val (_, parameters) = record.payload
    val songInfo = requireNotNull(parameters.songForApiContract) { "null song info?" }
    val pvs = requireNotNull(songInfo.pvs) { "null pvs in song info?" }

    log.info { "Deciding pv tasks" }
    // check if empty
    throwAndLogIfNoPvsLeft(pvs, "${songInfo.defaultName} doesn't have any available PVs, skip downloading")

    // check if no pvs are supported
    val supportedPvs = pvs.filter { it.service?.toPVServicesEnum() in SUPPORTED_SERVICES }
    throwAndLogIfNoPvsLeft(
      supportedPvs,
      "${songInfo.defaultName} doesn't have any PVs that are in our supported PV services $SUPPORTED_SERVICES, " +
          "please consider manually downloading this song's PV"
    )

    // sort pvs by 
    // 1. pv service
    // 2. pv types
    // so overall list is sorted by pv service, in each service, it is sorted by types
    val serviceToIntMap = preference.pvPreference.withIndex().associate { Pair(it.value, it.index) }
    val sortByServAndTypesPvs = supportedPvs.sortedWith(Comparator.comparingInt<PVContract> {
      requireNotNull(
        serviceToIntMap[requireNotNull(it.service?.toPVServicesEnum()) { "the pv service enum is null in PV info?" }]
      ) { "Did we failed to filter out un-supported PV services?" }
    }.thenComparingInt {
      requireNotNull(
        TYPE_TO_PRIORITY_MAP[requireNotNull(it.pvType) { "the pv service enum is null in PV info?" }]
      ) { "Is there a new PV type?" }
    })
    log.debug { "filtered and sorted pvs = $sortByServAndTypesPvs" }

    // decided should we move on next pv service if failed
    val serviceDecidedPvs = if (!preference.tryNextPvServiceOnFail) {
      val firstServ =
        sortByServAndTypesPvs.first().service // remember that the first available pv can be in any service, so always use pvList[0], not pvPreference[0]
      sortByServAndTypesPvs.filter { it.service?.equals(firstServ) ?: false }
    } else sortByServAndTypesPvs
    log.debug { "after deciding services = $serviceDecidedPvs" }

    // decided should we try reprint pvs
    val reprintDecidedPvs = if (!preference.tryReprintedPv) {
      serviceDecidedPvs.filter { it.pvType != PVType.REPRINT } // let's allow "Other" type for now
    } else serviceDecidedPvs
    log.debug { "after deciding reprint = $reprintDecidedPvs" }

    throwAndLogIfNoPvsLeft(
      reprintDecidedPvs,
      "By filtering out all reprint PVs, ${songInfo.defaultName} doesn't have any available PVs, " +
          "consider enabling reprint PVs for this song"
    )

    // decided should we try reprint pvs before moving to next pv service if failed
    val orderFurtherDecidedPvs = if (
      preference.tryNextPvServiceOnFail &&
      preference.tryReprintedPv &&
      // this setting is only meaningful when previous two settings are true,
      // which helps avoiding useless computation
      preference.tryReprintedAfterAllOriginalPvs
    ) {
      reprintDecidedPvs.sortedWith { p1, p2 ->
        when {
          p1.pvType == p2.pvType -> 0 // if both are same type, do nothing
          // here two pvs has different types
          p1.pvType == PVType.ORIGINAL -> -1 // if first one is original
          p2.pvType == PVType.ORIGINAL -> 1 // if second one is original
          // else don't touch
          else -> 0
        }
      }
    } else reprintDecidedPvs

    parameters.pvCandidates = orderFurtherDecidedPvs.map { PVTask(it) }
      .also { log.info { "Done deciding PV tasks, tasks = $it" } }
    return record
  }

  private fun throwAndLogIfNoPvsLeft(
    pvs: List<PVContract>,
    expMsg: String
  ) {
    if (pvs.isEmpty()) {
      log.warn { expMsg }
      throw RuntimeVocaloidException(expMsg)
    }
  }

  companion object {
    private val TYPE_TO_PRIORITY_MAP: Map<PVType, Int> = mapOf(
      PVType.ORIGINAL to 0,
      PVType.OTHER to 1,
      PVType.REPRINT to 2
    )
  }
}

private val log = KInlineLogging.logger()
