package mikufan.cx.vvd.downloader.service.downloader;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * @author CX无敌
 * @date 2020-12-25
 */
@Value @AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DownloadStatus {

  public static DownloadStatus failure(String description){
    return new DownloadStatus(false, description);
  }

  public static DownloadStatus success(){
    return new DownloadStatus(true, null);
  }

  boolean succeed;
  String description;
}
