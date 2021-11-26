package mikufan.cx.vvd.downloader.model

import mikufan.cx.vocadbapiclient.model.PVContract
import mikufan.cx.vocadbapiclient.model.SongForApiContract
import mikufan.cx.vvd.common.label.VSongLabel

/**
 * @date 2021-08-30
 * @author CX无敌
 */

data class VSongTask(val label: VSongLabel, val parameters: Parameters)

data class Parameters(
  var songProperFileName: String = "",
  var songForApiContract: SongForApiContract? = null,
  /**
   * the order of try download which PV first, then which next
   */
  var pvCandidates: List<PVContract>? = null,

  )

/**
 * using a data class to group related PV infos
 * used during downloading phase only
 */
data class PVTask(
  val pv: PVContract,
// other field that is val ? = null
  )
