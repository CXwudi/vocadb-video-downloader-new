// auto-generated by get-my-vocadb-java-model at 2020-12-18T01:55:56.0057007
package mikufan.cx.vvd.common.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;

@Getter @ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Builder(toBuilder = true) @Jacksonized
public final class TagBase {

   String additionalNames;

   String categoryName;

   int id;

   String name;

   String urlSlug;

}
