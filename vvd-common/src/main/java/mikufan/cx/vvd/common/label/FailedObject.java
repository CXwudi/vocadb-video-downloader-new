package mikufan.cx.vvd.common.label;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

/**
 * @author CX无敌
 * @date 2020-12-19
 */
@Getter @ToString
@SuperBuilder(toBuilder = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public abstract class FailedObject<T> {

  /**
   * rename to object when the new list provider is done,
   * right now this name is for competitive reason
   */
  T failedObj;
  String reason;

}
