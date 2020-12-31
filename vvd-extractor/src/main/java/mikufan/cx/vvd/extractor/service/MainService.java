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

  ExtractorService extractorService;

  @Override
  public void run() {
    //TODO: change it to return a local vsong resource (called VSongInfo) where song info json file is replaced with actual song info
    var allSongsToBeExtracted = ioService.getAllSongsToBeExtracted();
    allSongsToBeExtracted.forEach(pair -> handleExtraction(pair.getLeft(), pair.getRight()));
    log.info("All done, thanks for using vocadb-video-downloader - vvd-extractor submodule");
  }

  private void handleExtraction(SongForApi song, VSongResource resource) {
    //1. choose extractor
    var extractorInfo = extractorDecider.getProperExtractorAndInfo(resource.getVideo());
    //2. handle extracting
    var extractStatus = extractorService.handleExtract(extractorInfo, resource.getVideo(), song);
    //TODO quick: handle extraction fail - stop and record now
    //3. choose tag adder

    //4. handle tag adding

    //5. move/copy input json to output/err dir (let make json file as the status recorder in everywhere)
  }


}
