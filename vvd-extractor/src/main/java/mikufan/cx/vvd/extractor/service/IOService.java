package mikufan.cx.vvd.extractor.service;

import mikufan.cx.vvd.common.label.VSongResource;

import java.util.List;

/**
 * @author CX无敌
 * @date 2020-12-28
 */
public interface IOService {

  List<VSongResource> getAllSongsToBeExtracted();
}
