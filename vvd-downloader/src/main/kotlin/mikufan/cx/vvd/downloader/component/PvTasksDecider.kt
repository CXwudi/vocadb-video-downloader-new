package mikufan.cx.vvd.downloader.component

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.commonkt.vocadb.api.model.PVContract
import mikufan.cx.vvd.commonkt.vocadb.api.model.PVService
import mikufan.cx.vvd.commonkt.vocadb.api.model.PVType
import mikufan.cx.vvd.downloader.config.preference.Preference
import mikufan.cx.vvd.downloader.config.validation.SUPPORTED_SERVICES
import mikufan.cx.vvd.downloader.model.VSongTask
import mikufan.cx.vvd.downloader.util.OrderConstants
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
@Order(OrderConstants.PV_TASKS_DECIDER_ORDER)
class PvTasksDecider(
  private val preference: Preference
) : RecordProcessor<VSongTask, VSongTask> {

  /**
   * service to int map used for comparator
   */
  private val serviceToIntMap = preference.pvPreference.withIndex().associate { Pair(it.value, it.index) }

  private val pvServiceKeyExtractor = ToIntFunction<PVContract> {
    val order = requireNotNull(
      serviceToIntMap[requireNotNull(it.service) { "the pv service enum is null for ${it.name}?" }]
    ) { "Did we failed to filter out un-supported PV services?" } * 10
    if (preference.preferSmIdOverSoId) {
      order + if (it.service == PVService.NICONICODOUGA && it.pvId?.startsWith("sm") == true) -1 else 0
    } else {
      order
    }
  }

  companion object {

    private val pvTypeToIntMap = mapOf(
      PVType.ORIGINAL to 0,
      PVType.OTHER to 1,
      PVType.REPRINT to 2
    )

    /**
     * to make sure that reprinted types goes behind original and others type
     */
    private val pvTypeComparator = ToIntFunction<PVContract> {
      requireNotNull(
        pvTypeToIntMap[requireNotNull(it.pvType) { "the pv type is null for ${it.name}?" }]
      ) { "More PV Type?" }
    }
  }

  override fun processRecord(record: Record<VSongTask>): Record<VSongTask> {
    val (_, parameters) = record.payload
    val songInfo = requireNotNull(parameters.songForApiContract) { "null song info?" }
    val pvs = songInfo.pvs

    log.info { "Deciding pv orders for ${songInfo.defaultName}" }
    // check if empty
    throwAndLogIfNoPvsLeft(pvs) { "${songInfo.defaultName} doesn't have any available PVs, skip downloading" }

    // filter out pv services that users are not interested
    val supportedPvs = pvs.filter { pv ->
      pv.service != null && pv.service in preference.pvPreference
    }
    // after filter check empty
    throwAndLogIfNoPvsLeft(supportedPvs) {
      "${songInfo.defaultName} doesn't have any PVs that are in our supported PV services $SUPPORTED_SERVICES, " +
          "please consider manually downloading this song's PV"
    }

    // sort pv base on pv service preference and reprint's priority
    val sortedPvs = if (preference.tryAllOriginalPvsBeforeReprintedPvs) {
      supportedPvs.sortedWith(
        Comparator
          .comparingInt(pvTypeComparator)
          .thenComparingInt(pvServiceKeyExtractor)
      )
    } else {
      supportedPvs.sortedWith(
        Comparator
          .comparingInt(pvServiceKeyExtractor)
          .thenComparingInt(pvTypeComparator)
      )
    }
    log.debug { "after deciding sorted order = ${sortedPvs.toPrettyString()}" }

    // decided should we try reprint pvs
    val reprintDecidedPvs = if (!preference.tryReprintedPv) {
      sortedPvs.filter { it.pvType != PVType.REPRINT } // let's allow "Other" type for now
    } else sortedPvs
    log.debug { "after deciding reprinted = ${reprintDecidedPvs.toPrettyString()}" }
    // after filter check empty
    throwAndLogIfNoPvsLeft(reprintDecidedPvs) {
      "By filtering out all reprint PVs, ${songInfo.defaultName} doesn't have any available PVs, " +
          "consider enabling reprint PVs for this song"
    }

    // decided should we move on next pv service if failed
    val serviceDecidedPvs = if (!preference.tryNextPvServiceOnFail) {
      // remember that the first available pv can be in any service, so always use pvList[0], not pvPreference[0]
      val firstServ = requireNotNull(reprintDecidedPvs.first().service) {
        "the pv service enum is null for ${reprintDecidedPvs.first().name}?"
      }
      reprintDecidedPvs.takeWhile { it.service == firstServ }
    } else reprintDecidedPvs
    log.debug { "after deciding try next pv services" }

    log.info {
      "Done deciding PV candidates for ${songInfo.defaultName}, " +
          "final chosen pvs = ${serviceDecidedPvs.toPrettyString()}"
    }
    parameters.pvCandidates = serviceDecidedPvs
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

  private fun List<PVContract>.toPrettyString() =
    this.joinToString(", ", "[", "]") { "${it.name} ${it.url ?: "NULL URL"} ${it.pvType}" }
}

private val log = KInlineLogging.logger()
