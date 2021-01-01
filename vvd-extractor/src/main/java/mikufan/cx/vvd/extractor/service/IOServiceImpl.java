package mikufan.cx.vvd.extractor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.label.VSongResource;
import mikufan.cx.vvd.common.util.FileNamePostFix;
import mikufan.cx.vvd.common.vocadb.model.SongForApi;
import mikufan.cx.vvd.extractor.config.IOConfig;
import mikufan.cx.vvd.extractor.label.ExtractContext;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author CX无敌
 * @date 2020-12-28
 */
@Service @Slf4j @Validated
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IOServiceImpl implements IOService {

  private static final String NO_EXTENSION = "";

  IOConfig ioConfig;

  ObjectMapper objectMapper;

  /**
   * scan input files, read json, and sort them in last modify increasing order
   * @return list of songs to be extracted in order
   */
  @Override
  public List<@Valid VSongResource> getAllSongsToBeExtracted() {
    //1. read and sort all input jsons in last modify increasing order
    var inputDirectory = ioConfig.getInputDirectory();

    var inputJsonFilesArray = inputDirectory.toFile().listFiles(
        (dir, name) -> name.contains(FileNamePostFix.RESOURCES));

    if (inputJsonFilesArray == null || inputJsonFilesArray.length == 0){
      log.info("No songs found to be extracted");
      return List.of();
    }
    log.debug("Find {} jsons from {}", inputJsonFilesArray.length, inputDirectory.toAbsolutePath());
    var inputJsonFilesList = Arrays.asList(inputJsonFilesArray);
    var orderedInputJsonPairs = inputJsonFilesList.stream()
        .map(file -> Pair.of(file, file.lastModified()))
        .sorted(Comparator.comparingLong(Pair::getRight))
        .collect(Collectors.toUnmodifiableList());
    log.debug("They are now sorted from early to latest last modify date\n{}", orderedInputJsonPairs);

    return orderedInputJsonPairs.stream()
        .map(Pair::getLeft)
        .map(this::toVsongResource)
        .collect(Collectors.toUnmodifiableList());
  }

  @Override
  public ExtractContext.ExtractContextBuilder toExtractContextBuilder(VSongResource songResource) {
    return ExtractContext.builder()
        .songResource(songResource)
        .songInfo(toSongInfo(
            ioConfig.getInputDirectory().resolve(
                songResource.getInfoFileName()
            ).toFile()
        ));
  }


  @SneakyThrows({IOException.class})
  private VSongResource toVsongResource(File file) {
    return objectMapper.readValue(file, VSongResource.class);
  }

  @SneakyThrows({IOException.class})
  private SongForApi toSongInfo(File file) {
    return objectMapper.readValue(file, SongForApi.class);
  }

}
