// auto-generated by get-my-vocadb-java-model at 2020-12-18T02:33:31.8322225
package mikufan.cx.vvd.common.vocadb.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;

@Getter @ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Builder(toBuilder = true) @Jacksonized
public final class EntryThumb {

   String mime;
   String urlOriginal;
   String urlSmallThumb;
   String urlThumb;
   String urlTinyThumb;

}
