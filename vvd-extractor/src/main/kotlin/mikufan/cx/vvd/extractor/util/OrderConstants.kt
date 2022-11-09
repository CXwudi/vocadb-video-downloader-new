package mikufan.cx.vvd.extractor.util

object OrderConstants {
  const val BEFORE_PROCESS_LABEL_VALIDATOR_ORDER = 1
  const val SONG_INFO_LOADER_ORDER = 2
  const val EXTRACTOR_DECIDER_ORDER = 3
  const val EXTRACTOR_RUNNER_ORDER = 4
  const val TAGGER_DECIDER_ORDER = 5
  const val TAGGER_RUNNER_ORDER = 6

  /*
  TODO:
    tagger runner
    lastModifiedChanger // rely on order int to achieve parallel
    finalRenamer
    labelSaver
   */
}
