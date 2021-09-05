package mikufan.cx.vvd.downloader.model

import mikufan.cx.vocadbapiclient.model.SongForApiContract
import mikufan.cx.vvd.common.label.VSongLabel

/**
 * @date 2021-08-30
 * @author CX无敌
 */

data class VSongTask(val label: VSongLabel, val parameters: Parameters)

data class Parameters(
  val songForApiContract: SongForApiContract,
)
