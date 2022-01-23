package mikufan.cx.vvd.extractor.service;

import mikufan.cx.vvd.extractor.model.ExtractContext;

/**
 * @author CX无敌
 * @date 2020-12-30
 */

public interface TaggerDecider {

  ExtractContext chooseTagger(ExtractContext context);

}
