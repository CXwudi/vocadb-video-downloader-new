package mikufan.cx.vvd.taskproducer.util

import mikufan.cx.vvd.common.naming.FileNamePostFix
import mikufan.cx.vvd.commonkt.naming.SongProperFileName

/**
 * @date 2021-06-12
 * @author CX无敌
 */

fun SongProperFileName.toInfoFileName(): String {
  return this.toString() + FileNamePostFix.SONG_INFO + ".json"
}

fun SongProperFileName.toLabelFileName(): String {
  return this.toString() + FileNamePostFix.LABEL + ".json"
}
