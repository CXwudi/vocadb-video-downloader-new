package mikufan.cx.vvd.downloader.label;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

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

  public static DownloadStatus merge(DownloadStatus... statuses){
    var errors = Arrays.stream(statuses)
        .map(DownloadStatus::getDescription)
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableList());
    if (errors.isEmpty()){
      return DownloadStatus.success();
    } else {
      return DownloadStatus.failure(
          errors.stream().collect(
              Collectors.joining(", ", "All failure messages = [", "]")));
    }
  }
}
