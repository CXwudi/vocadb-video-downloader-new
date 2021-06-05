package mikufan.cx.vvd.taskproducer.model

import mikufan.cx.vocadbapiclient.model.SongForApiContract
import mikufan.cx.vvd.common.label.VSongLabel

/**
 * @date 2021-05-29
 * @author CX无敌
 */

data class VSongTask(val label: VSongLabel, val parameters: Parameters)

data class Parameters(
  var songForApiContract: SongForApiContract?,
  var order: Long?
)
