package mikufan.cx.vvd.common.naming;

import mikufan.cx.vvd.common.vocadb.model.SongForApi;

/**
 * @author CX无敌
 * @date 2020-12-19
 */
public interface FileNameUtil {

  static String buildErrorInfoJsonFileName(SongForApi song){
    return buildBasicFileNameForSong(song) + FileNamePostFix.SONG_INFO_ERR + ".json";
  }

  static String buildInfoJsonFileName(SongForApi song){
    return buildBasicFileNameForSong(song) + FileNamePostFix.SONG_INFO + ".json";
  }

  static String buildResourceJsonFileName(SongForApi song){
    return buildBasicFileNameForSong(song) + FileNamePostFix.RESOURCES + ".json";
  }

  static String buildPvFileName(SongForApi song, String extensionWithDot){
    return buildBasicFileNameForSong(song) + FileNamePostFix.VIDEO + extensionWithDot;
  }

  static String buildThumbnailFileName(SongForApi song, String extensionWithDot){
    return buildBasicFileNameForSong(song) + FileNamePostFix.THUMBNAIL + extensionWithDot;
  }

  static String buildAudioFileName(SongForApi song, String extensionWithDot){
    return buildBasicFileNameForSong(song) + FileNamePostFix.AUDIO + extensionWithDot;
  }

  static String buildBasicFileNameForSong(SongForApi song){
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
