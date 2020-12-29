package mikufan.cx.vvd.extractor.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.util.FileNameUtil;
import mikufan.cx.vvd.common.vocadb.model.SongForApi;
import mikufan.cx.vvd.extractor.config.IOConfig;
import mikufan.cx.vvd.extractor.service.extractor.ExtractStatus;
import mikufan.cx.vvd.extractor.util.ExtractorInfo;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

/**
 * @author CX无敌
 * @date 2020-12-29
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExtractorServiceImpl implements ExtractorService {

  IOConfig ioConfig;

  @Override @SneakyThrows(InterruptedException.class)
  public ExtractStatus handleExtract(ExtractorInfo extractorInfo, Path videoFile, SongForApi song) {
    var extractor = extractorInfo.getAudioExtractor();
    var audioFileName = FileNameUtil.buildAudioFileName(song, extractorInfo.getAudioExtension());
    log.info("Extracting to {} by {}", audioFileName, extractor.getName());

    return extractor.extractAudio(videoFile, ioConfig.getOutputDirectory(), audioFileName);
  }
}
