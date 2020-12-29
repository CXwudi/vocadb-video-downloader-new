package mikufan.cx.vvd.extractor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException;
import mikufan.cx.vvd.common.label.VSongResource;
import mikufan.cx.vvd.common.util.FileNamePostFix;
import mikufan.cx.vvd.common.util.FileNameUtil;
import mikufan.cx.vvd.common.vocadb.model.SongForApi;
import mikufan.cx.vvd.extractor.config.IOConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
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
  public List<Pair<SongForApi, @Valid VSongResource>> getAllSongsToBeExtracted() {
    //1. read and sort all input jsons in last modify increasing order
    var inputDirectory = ioConfig.getInputDirectory();

    var inputJsonFilesArray = inputDirectory.toFile().listFiles(
        (dir, name) -> name.contains(FileNamePostFix.SONG_INFO));

    if (inputJsonFilesArray == null || inputJsonFilesArray.length == 0){
      log.info("No songs found to be downloaded");
      return List.of();
    }
    log.debug("Find {} jsons from {}", inputJsonFilesArray.length, inputDirectory.toAbsolutePath());
    var inputJsonFilesList = Arrays.asList(inputJsonFilesArray);
    var orderedInputJsonPairs = inputJsonFilesList.stream()
        .map(file -> Pair.of(file, file.lastModified()))
        .sorted(Comparator.comparingLong(Pair::getRight))
        .collect(Collectors.toUnmodifiableList());
    log.debug("They are now sorted from early to latest last modify date\n{}", orderedInputJsonPairs);

    //2. json -> vsong resource, to do so, we need a list of all files
    var inputAllFilesArray = inputDirectory.toFile().listFiles(
        (dir, name) -> name.contains(FileNamePostFix.SONG_INFO) ||
            name.contains(FileNamePostFix.VIDEO) ||
            name.contains(FileNamePostFix.THUMBNAIL));

    return orderedInputJsonPairs.stream()
        .map(Pair::getLeft)
        .map(jsonFile -> toSongAndResourcePair(jsonFile, Set.of(inputAllFilesArray)))
        .collect(Collectors.toUnmodifiableList());
  }


  @SneakyThrows({IOException.class})
  private Pair<SongForApi, VSongResource> toSongAndResourcePair(File file, Set<File> allFiles) {
    var song = objectMapper.readValue(file, SongForApi.class);
    var basePvFileName = FileNameUtil.buildPvFileName(song, NO_EXTENSION);
    var baseAudioFileName = FileNameUtil.buildAudioFileName(song, NO_EXTENSION);
    var baseThumbnailFileName = FileNameUtil.buildThumbnailFileName(song, NO_EXTENSION);
    var songResource = VSongResource.builder()
        .infoFile(file.toPath())
        .video(findFile(allFiles, basePvFileName).orElseThrow(
            () -> new RuntimeVocaloidException(String.format("Can not find pv file for %s", basePvFileName))
        ))
        .audio(findFile(allFiles, baseAudioFileName).orElse(null))
        .thumbnail(findFile(allFiles, baseThumbnailFileName).orElseThrow(
            () -> new RuntimeVocaloidException(String.format("Can not find thumbnail file for %s", baseThumbnailFileName))
        ))
        .build();
    log.debug("Find all recourses for {}, resources={}",
        FileNameUtil.buildBasicFileNameForSong(song), songResource);
    return Pair.of(song, songResource);
  }

  private Optional<Path> findFile(Set<File> allFiles, String partialName){
    return allFiles.parallelStream()
        .filter(file -> file.getName().contains(partialName))
        .map(File::toPath)
        .findFirst();
  }
}
