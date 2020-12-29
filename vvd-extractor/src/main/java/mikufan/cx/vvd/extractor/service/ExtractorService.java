package mikufan.cx.vvd.extractor.service;

import mikufan.cx.vvd.common.vocadb.model.SongForApi;
import mikufan.cx.vvd.extractor.service.extractor.ExtractStatus;
import mikufan.cx.vvd.extractor.util.ExtractorInfo;

import java.nio.file.Path;

/**
 * @author CX无敌
 * @date 2020-12-29
 */
public interface ExtractorService {

  ExtractStatus handleExtract(ExtractorInfo extractorInfo, Path videoFile, SongForApi song);
}
