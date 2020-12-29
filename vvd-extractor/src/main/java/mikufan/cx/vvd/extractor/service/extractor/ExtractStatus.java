package mikufan.cx.vvd.extractor.service.extractor;

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
public class ExtractStatus {

  public static ExtractStatus failure(String description){
    return new ExtractStatus(false, description);
  }

  public static ExtractStatus success(){
    return new ExtractStatus(true, null);
  }

  boolean succeed;
  String description;

  public boolean isFailure(){
    return !succeed;
  }

  public static ExtractStatus merge(ExtractStatus... statuses){
    var errors = Arrays.stream(statuses)
        .map(ExtractStatus::getDescription)
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableList());
    if (errors.isEmpty()){
      return ExtractStatus.success();
    } else {
      return ExtractStatus.failure(
          errors.stream().collect(
              Collectors.joining(", ", "All failure messages = [", "]")));
    }
  }
}
