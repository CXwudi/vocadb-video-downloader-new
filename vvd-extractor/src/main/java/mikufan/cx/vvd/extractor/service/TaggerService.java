package mikufan.cx.vvd.extractor.service;

import mikufan.cx.vvd.extractor.model.ExtractContext;
import mikufan.cx.vvd.extractor.model.ExtractStatus;

/**
 * @author CX无敌
 * @date 2020-12-31
 */
public interface TaggerService {

  ExtractStatus handleTagging(ExtractContext extractContext);
}
