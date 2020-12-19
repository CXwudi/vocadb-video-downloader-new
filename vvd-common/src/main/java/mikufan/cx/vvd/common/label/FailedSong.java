package mikufan.cx.vvd.common.label;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import mikufan.cx.vvd.common.vocadb.model.SongForApi;

/**
 * @author CX无敌
 * @date 2020-12-19
 */
@Getter @ToString
@SuperBuilder(toBuilder = true) @Jacksonized
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FailedSong extends FailedObject<SongForApi>{

}
