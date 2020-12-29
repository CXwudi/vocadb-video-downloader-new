package mikufan.cx.vvd.extractor.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.label.VSongResource;
import mikufan.cx.vvd.common.vocadb.model.SongForApi;
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

  @Override
  public void run() {
    var allSongsToBeExtracted = ioService.getAllSongsToBeExtracted();
    allSongsToBeExtracted.forEach(pair -> handleExtraction(pair.getLeft(), pair.getRight()));
    log.info("All done, thanks for using vocadb-video-downloader - vvd-extractor submodule");
  }

  private void handleExtraction(SongForApi song, VSongResource resource) {
    //1. choose extractor
    var chosenExtractor = extractorDecider.getProperExtractorAndInfo(resource.getVideo());
    //2. handle extracting

    //3. choose tag adder

    //4. handle tag adding

    //5. move input json to output dir (let make json file as the status recorder in everywhere)
  }


}
