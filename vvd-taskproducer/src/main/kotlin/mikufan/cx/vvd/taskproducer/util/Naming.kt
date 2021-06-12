package mikufan.cx.vvd.taskproducer.util

import mikufan.cx.vocadbapiclient.model.SongForApiContract
import mikufan.cx.vvd.common.naming.FileNamePostFix
import mikufan.cx.vvd.commonkt.naming.toProperFileName

/**
 * @date 2021-06-12
 * @author CX无敌
 */


fun SongForApiContract.toInfoFileName(): String {
  return this.toProperFileName() + FileNamePostFix.SONG_INFO + ".json"
}

fun SongForApiContract.toLabelFileName(): String {
  return this.toProperFileName() + FileNamePostFix.LABEL + ".json"
}