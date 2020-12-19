package mikufan.cx.vvd.common.label;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;

import java.nio.file.Path;

/**
 * @author CX无敌
 * @date 2020-12-19
 */
@Getter @ToString
@Builder(toBuilder = true) @Jacksonized
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public class VSongResource {

  Path video;
  Path audio;
  Path thumbnail;
  Path infoFile;

}
