// auto-generated by get-my-vocadb-java-model at 2020-12-18T01:55:56.0126799
package mikufan.cx.vvd.common.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter @ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Builder(toBuilder = true) @Jacksonized
public final class Venue {

   String additionalNames;

   String address;

   String addressCountryCode;

   OptionalGeoPoint coordinates;

   boolean deleted;

   String description;

   int id;

   String name;

   String status;

   int version;

   List<WebLink> webLinks;

}
