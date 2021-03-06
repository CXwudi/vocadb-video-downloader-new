// auto-generated by get-my-vocadb-java-model at 2020-12-19T03:10:29.8647371
package mikufan.cx.vvd.common.vocadb.model;

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
public final class VenueForApi {

   String additionalNames;
   String address;
   String addressCountryCode;
   OptionalGeoPoint coordinates;
   String description;
   List<ReleaseEvent> events;
   int id;
   String name;
   List<LocalizedString> names;
   String status;
   int version;
   List<WebLinkForApi> webLinks;

}
