// auto-generated by get-my-vocadb-java-model at 2020-12-19T03:10:29.825839
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
public final class ArtistForSong {

   Artist artist;
   String categories;
   String effectiveRoles;
   int id;
   boolean isCustomName;
   boolean isSupport;
   String name;
   String roles;

}
