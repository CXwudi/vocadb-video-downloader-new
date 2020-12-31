package mikufan.cx.vvd.extractor.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.label.VSongResource;
import mikufan.cx.vvd.extractor.label.ExtractContext;
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

  @Override
  public void run() {
    // what is returned is what is used for tracking input files to be extracted
    var allSongsToBeExtracted = ioService.getAllSongsToBeExtracted();
    allSongsToBeExtracted.forEach(this::handleExtraction);
    log.info("All done, thanks for using vocadb-video-downloader - vvd-extractor submodule");
  }

  private void handleExtraction(VSongResource toBeExtractedSongResource) {
    //0. build extract context
    var extractContextBuilder = ExtractContext.builder()
        .songResource(toBeExtractedSongResource);
    //1. choose extractor
    var extractorAndAudioExtHolder = extractorDecider.getProperExtractorAndAudioExt(extractContextBuilder.build());
    extractContextBuilder
        .audioExtension(extractorAndAudioExtHolder.getAudioExtension())
        .audioExtractor(extractorAndAudioExtHolder.getAudioExtractor());
    //2. handle extracting
    var extractStatus = extractorService.handleExtract(extractContextBuilder.build());
    if (extractStatus.isFailure()){
      //TODO quick: handle extraction fail - stop and record now
    }
    //3. choose tag adder

    //4. handle tag adding

    //5. move/copy input json to output/err dir (let make json file as the status recorder in everywhere)

  }


}
