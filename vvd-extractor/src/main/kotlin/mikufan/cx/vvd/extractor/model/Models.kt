package mikufan.cx.vvd.extractor.model

import mikufan.cx.vocadbapiclient.model.SongForApiContract
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.commonkt.naming.SongProperFileName
import mikufan.cx.vvd.commonkt.naming.toProperFileName

/**
 * @date 2022-06-11
 * @author CX无敌
 */

data class VSongTask(val label: VSongLabel, val parameters: Parameters)

data class Parameters(
  var songForApiContract: SongForApiContract? = null,

) {
  /**
   * save the proper file name for debugging and other filename generation
   */
  val songProperFileName: SongProperFileName by lazy {
    requireNotNull(songForApiContract) { "can not call toProperFileName() on null songForApiContract" }
      .toProperFileName()
  }
}
