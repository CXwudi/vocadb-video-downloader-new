package mikufan.cx.vvd.extractor.service;

import mikufan.cx.vvd.extractor.model.ExtractContext;

/**
 * @author CX无敌
 * @date 2020-12-29
 */
public interface ExtractorDecider {

  ExtractContext getProperExtractorAndAudioExt(ExtractContext extractContext);
}
