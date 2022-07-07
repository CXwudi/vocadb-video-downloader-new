package mikufan.cx.vvd.extractor.model

import mikufan.cx.vocadbapiclient.model.SongForApiContract
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.commonkt.naming.SongProperFileName
import mikufan.cx.vvd.commonkt.naming.toProperFileName
import mikufan.cx.vvd.extractor.component.ExtractorDecider
import mikufan.cx.vvd.extractor.component.extractor.base.BaseAudioExtractor
import java.util.*

/**
 * @date 2022-06-11
 * @author CX无敌
 */

data class VSongTask(val label: VSongLabel, val parameters: Parameters)

data class Parameters(
  var songForApiContract: SongForApiContract? = null,
  /**
   * it is `?` because it wasn't set before [ExtractorDecider]
   *
   * it is [Optional] because a vsong may already have the audio defined in [VSongLabel]
   */
  var chosenAudioExtractor: Optional<BaseAudioExtractor>? = null,
) {
  /**
   * save the proper file name for debugging and other filename generation
   */
  val songProperFileName: SongProperFileName by lazy {
    requireNotNull(songForApiContract) { "can not call toProperFileName() on null songForApiContract" }
      .toProperFileName()
  }
}