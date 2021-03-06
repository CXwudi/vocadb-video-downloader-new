// auto-generated by get-my-vocadb-java-model at 2020-12-19T03:10:29.8697224
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
public final class WebLink {

   String category;
   String description;
   String descriptionOrUrl;
   boolean disabled;
   int id;
   String url;

}
