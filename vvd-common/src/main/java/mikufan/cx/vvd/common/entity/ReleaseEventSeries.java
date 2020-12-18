// auto-generated by get-my-vocadb-java-model at 2020-12-17T22:35:44.8558619
package mikufan.cx.vvd.common.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter @ToString
@Builder @Jacksonized
public class ReleaseEventSeries {

  private String additionalNames;

  private String category;

  private boolean deleted;

  private String description;

  private int id;

  private String name;

  private String pictureMime;

  private String status;

  private String urlSlug;

  private int version;

  private List<WebLink> webLinks;

}
