package mikufan.cx.vvd.common.util;

import mikufan.cx.vvd.common.vocadb.model.SongForApi;

/**
 * @author CX无敌
 * @date 2020-12-19
 */
public interface FileNameUtil {

  static String buildFileNameForSong(SongForApi song){
    var artists = song.getArtistString().split("feat\\.");
    var vocals = artists[1].trim();
    var producers = artists[0].trim();
    var songName = song.getName();
    return removeIllegalChars(String.format("【%s】%s【%s】", vocals, songName, producers));
  }

  static String removeIllegalChars(String fileName){
    return fileName
        .replace("/", "-")
        .replace("\\", "-")

        .replace("? ", " ")
        .replace("* ", " ")
        .replace(": ", " ")

        .replace("?", " ")
        .replace("*", " ")
        .replace(":", " ");
  }
}
