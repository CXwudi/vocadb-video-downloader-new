package mikufan.cx.vvd.common.label;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;
import mikufan.cx.vvd.common.validation.annotation.IsFile;

import javax.validation.constraints.NotNull;
import java.nio.file.Path;

/**
 * @author CX无敌
 * @date 2020-12-19
 */
@Getter @ToString
@Builder(toBuilder = true) @Jacksonized
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public class VSongResource {

  @NotNull @IsFile Path video;
  @IsFile(optionalCheck = true) Path audio;
  @NotNull @IsFile Path thumbnail;
  @NotNull @IsFile Path infoFile;

}
