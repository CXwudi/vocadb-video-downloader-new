package mikufan.cx.vvd.common.vocadb.api.songlist.getlist;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import mikufan.cx.vvd.common.vocadb.api.common.PartialFindResult;

/**
 * @author CXwudi
 * @date 2020-12-18
 */
@Getter @ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true) @Jacksonized
public final class PartialSongList extends PartialFindResult<SongInList> {

}
