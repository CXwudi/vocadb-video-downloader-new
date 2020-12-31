package mikufan.cx.vvd.common.label;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * To support non-blocking debugging purposes. <br/>
 * App can use this class to collect or write all failure into one place, <br/>
 * so that when long running app is finished, we can get a clear look of all failure we have <br/>
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
  @NotNull T failedObj;
  @NotBlank String reason;

}
