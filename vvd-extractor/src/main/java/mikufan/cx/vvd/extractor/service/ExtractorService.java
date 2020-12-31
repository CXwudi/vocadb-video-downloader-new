package mikufan.cx.vvd.extractor.service;

import mikufan.cx.vvd.extractor.label.ExtractContext;
import mikufan.cx.vvd.extractor.label.ExtractStatus;

/**
 * @author CX无敌
 * @date 2020-12-29
 */
public interface ExtractorService {

  ExtractStatus handleExtract(ExtractContext extractContext);
}
