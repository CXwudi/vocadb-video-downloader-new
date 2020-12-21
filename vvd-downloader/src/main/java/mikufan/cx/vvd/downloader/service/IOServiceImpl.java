package mikufan.cx.vvd.downloader.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.exception.ThrowableFunction;
import mikufan.cx.vvd.common.util.FileNamePostFix;
import mikufan.cx.vvd.common.vocadb.model.SongForApi;
import mikufan.cx.vvd.downloader.config.io.ApplicationIO;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author CX无敌
 * @date 2020-12-19
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IOServiceImpl implements IOService {

  ApplicationIO applicationIO;

  ObjectMapper objectMapper;

  /**
   * scan input file, read json, and sort them in last modify increasing order
   * @return list of songs to be download in order
   */
  @Override
  public List<SongForApi> getAllSongsToBeDownloadedInOrder(){
    var inputDirectory = applicationIO.getInputDirectory();
    var inputFilesArray = inputDirectory.toFile().listFiles((dir, name) -> name.contains(FileNamePostFix.SONG_INFO));
    if (inputFilesArray == null || inputFilesArray.length == 0){
      log.info("No songs found to be downloaded");
      return List.of();
    }
    log.debug("Find {} jsons from {}", inputFilesArray.length, inputDirectory.toAbsolutePath());
    var inputFilesList = Arrays.asList(inputFilesArray);
    log.debug("They are \n{}", inputFilesList.toString());
    var orderedInputPairs = inputFilesList.stream()
        .map(file -> Pair.of(file, file.lastModified()))
        .sorted(Comparator.comparingLong(Pair::getRight))
        .collect(Collectors.toUnmodifiableList());
    log.debug("They are now sorted from early to latest last modify date\n{}", orderedInputPairs);

    ThrowableFunction<File, SongForApi> toObjectMapping = file -> objectMapper.readValue(file, SongForApi.class);

    return orderedInputPairs.stream()
        .map(Pair::getLeft)
        .map(toObjectMapping.toFunction())
        .collect(Collectors.toUnmodifiableList());
  }
}
