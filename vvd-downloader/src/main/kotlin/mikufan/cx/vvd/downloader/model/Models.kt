package mikufan.cx.vvd.downloader.model

import mikufan.cx.vocadbapiclient.model.PVContract
import mikufan.cx.vocadbapiclient.model.SongForApiContract
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.commonkt.naming.SongProperFileName
import mikufan.cx.vvd.commonkt.naming.toProperFileName

/**
 * @date 2021-08-30
 * @author CX无敌
 */

data class VSongTask(val label: VSongLabel, val parameters: Parameters)

data class Parameters(
  var songForApiContract: SongForApiContract? = null,
  /**
   * the order of try download which PV first, then which next
   */
  var pvCandidates: List<PVContract>? = null,
) {
  /**
   * save the proper file name for debugging and other filename generation
   */
  val songProperFileName: SongProperFileName by lazy {
    requireNotNull(songForApiContract) { "can not call toProperFileName() on null songForApiContract" }
      .toProperFileName()
  }
}

/**
 * using a data class to group related PV infos
 * used during downloading phase only
 */
data class PVTask(
  val pv: PVContract,
// other field that is val ? = null
)
