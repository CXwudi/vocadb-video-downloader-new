package mikufan.cx.vvd.extractor.model

import mikufan.cx.vvd.commonkt.vocadb.api.model.SongForApiContract
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.commonkt.naming.SongProperFileName
import mikufan.cx.vvd.commonkt.naming.toProperFileName
import mikufan.cx.vvd.extractor.component.ExtractorDecider
import mikufan.cx.vvd.extractor.component.extractor.base.BaseAudioExtractor
import mikufan.cx.vvd.extractor.component.tagger.base.BaseAudioTagger
import java.nio.file.Path
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
  /**
   * the audio file to be used for tagging,
   *
   * either it is extracted by [chosenAudioExtractor] or it is copied over to outputDirectory from [VSongLabel]
   *
   * it should be saved in [outputDirectory] with [songProperFileName]
   */
  var processedAudioFile: Path? = null,

  var chosenAudioTagger: BaseAudioTagger? = null,
) {
  /**
   * save the proper file name for debugging and other filename generation
   */
  val songProperFileName: SongProperFileName by lazy {
    requireNotNull(songForApiContract) { "can not call toProperFileName() on null songForApiContract" }
      .toProperFileName()
  }
}
