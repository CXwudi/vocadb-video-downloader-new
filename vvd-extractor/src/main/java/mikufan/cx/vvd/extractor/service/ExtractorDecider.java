package mikufan.cx.vvd.extractor.service;

import mikufan.cx.vvd.extractor.service.extractor.AudioExtractor;

import java.nio.file.Path;

/**
 * @author CX无敌
 * @date 2020-12-29
 */
public interface ExtractorDecider {

  AudioExtractor chooseExtractor(Path videoFile);
}
