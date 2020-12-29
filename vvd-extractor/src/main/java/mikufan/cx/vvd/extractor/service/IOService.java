package mikufan.cx.vvd.extractor.service;

import mikufan.cx.vvd.common.label.VSongResource;
import mikufan.cx.vvd.common.vocadb.model.SongForApi;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * @author CX无敌
 * @date 2020-12-28
 */
public interface IOService {

  List<Pair<SongForApi, VSongResource>> getAllSongsToBeExtracted();
}
