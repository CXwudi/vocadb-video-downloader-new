package mikufan.cx.vvd.common.naming;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileNamePostFix {
  private static final String SEPARATOR = "-";
  public static final String SONG_INFO = SEPARATOR + "songInfo";
  public static final String SONG_INFO_ERR = SONG_INFO + SEPARATOR + "err";
  public static final String ERROR = SEPARATOR + "error";
  public static final String VIDEO = SEPARATOR + "pv";
  public static final String AUDIO = SEPARATOR + "audio";
  public static final String THUMBNAIL = SEPARATOR + "thumbnail";
  public static final String LABEL = SEPARATOR + "label";
  public static final String RESOURCES = SEPARATOR + "resource";
}
