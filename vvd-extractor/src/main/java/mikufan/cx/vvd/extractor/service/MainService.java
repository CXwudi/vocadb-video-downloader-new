package mikufan.cx.vvd.extractor.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException;
import mikufan.cx.vvd.common.label.VSongResource;
import org.springframework.stereotype.Service;

/**
 * @author CX无敌
 * @date 2020-12-28
 */
@Service @Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MainService implements Runnable {

  IOService ioService;

  ExtractorDecider extractorDecider;

  ExtractorService extractorService;

  TaggerDecider taggerDecider;

  TaggerService taggerService;

  @Override
  public void run() {
    // what is returned is what is used for tracking input files to be extracted
    var allSongsToBeExtracted = ioService.getAllSongsToBeExtracted();
    allSongsToBeExtracted.forEach(this::handleExtraction);
    log.info("All done, thanks for using vocadb-video-downloader - vvd-extractor submodule");
  }

  private void handleExtraction(VSongResource toBeExtractedSongResource) {
    //0. build extract context
    log.info("Start handling extraction and tagging for {}", toBeExtractedSongResource.getPvFileName());
    var contextBuilder = ioService.toExtractContextBuilder(toBeExtractedSongResource);
    //1. choose extractor
    var extractorAndAudioExtHolder = extractorDecider.getProperExtractorAndAudioExt(contextBuilder.build());
    contextBuilder
        .audioExtension(extractorAndAudioExtHolder.getAudioExtension())
        .audioExtractor(extractorAndAudioExtHolder.getAudioExtractor());
    //2. handle extracting
    var extractStatus = extractorService.handleExtract(contextBuilder.build());
    if (extractStatus.isFailure()){
      //TODO quick: handle extraction fail - stop and record now
      throw new RuntimeVocaloidException(String.format("failure in extracting vsong: %s", extractStatus.getDescription()));
    }
    //3. choose tag adder
    var taggerHolder = taggerDecider.chooseTagger(contextBuilder.build());
    contextBuilder.audioTagger(taggerHolder.getAudioTagger());
    //4. handle tag adding
    var extractStatus2 = taggerService.handleTagging(contextBuilder.build());
    if (extractStatus2.isFailure()){
      //TODO quick: handle extraction fail - stop and record now
      throw new RuntimeVocaloidException(String.format("failure in tagging vsong: %s", extractStatus2.getDescription()));
    }
    //5. move/copy input json to output/err dir (let make json file as the status recorder in everywhere)
    //TODO
    log.info("Finished extraction and tagging for {}", toBeExtractedSongResource.getPvFileName());
  }


}
