package mikufan.cx.vvd.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileNamePostFix {
  private static final String SEPARATOR = "-";
  public static final String SONG_INFO = SEPARATOR + "songInfo";
  public static final String SONG_INFO_ERR = SONG_INFO + SEPARATOR + "err";
  public static final String VIDEO = SEPARATOR + "pv";
  public static final String AUDIO = SEPARATOR + "audio";
  public static final String THUMBNAIL = SEPARATOR + "thumbnail";
  public static final String RESOURCES_LABEL = SEPARATOR + "resources";
}
