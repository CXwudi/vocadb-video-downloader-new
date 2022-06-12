package mikufan.cx.vvd.downloader.model

import mikufan.cx.vocadbapiclient.model.PVContract
import mikufan.cx.vocadbapiclient.model.SongForApiContract
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.commonkt.naming.SongProperFileName
import mikufan.cx.vvd.commonkt.naming.toProperFileName
import mikufan.cx.vvd.downloader.component.downloader.base.DownloadFiles

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
  // next two records the download results
  var downloadFiles: DownloadFiles? = null,
  var downloadedPv: PVContract? = null,
) {
  /**
   * save the proper file name for debugging and other filename generation
   */
  val songProperFileName: SongProperFileName by lazy {
    requireNotNull(songForApiContract) { "can not call toProperFileName() on null songForApiContract" }
      .toProperFileName()
  }
}
