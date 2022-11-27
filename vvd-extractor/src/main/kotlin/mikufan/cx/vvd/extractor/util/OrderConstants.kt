package mikufan.cx.vvd.extractor.util

object OrderConstants {
  const val BEFORE_PROCESS_LABEL_VALIDATOR_ORDER = 1
  const val SONG_INFO_LOADER_ORDER = 2
  const val EXTRACTOR_DECIDER_ORDER = 3
  const val EXTRACTOR_RUNNER_ORDER = 4
  const val TAGGER_DECIDER_ORDER = 5
  const val TAGGER_RUNNER_ORDER = 6
  const val LAST_MODIFIED_CHANGER = 7

  /*
  TODO:
    finalRenamer
    // after process label validator, let's only add it if one day more vvd-module is needed after this one
    labelSaver
   */
}
