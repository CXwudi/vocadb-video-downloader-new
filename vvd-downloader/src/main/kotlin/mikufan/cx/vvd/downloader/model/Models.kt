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
  var songForApiContract: SongForApiContract? = null,
  /**
   * the order of try download which PV first, then which next
   */
  val pvCandidates: MutableList<PVTask> = mutableListOf(),
)

/**
 * using a data class to group related PV infos
 */
data class PVTask(
  val pv: PVContract,

  )
