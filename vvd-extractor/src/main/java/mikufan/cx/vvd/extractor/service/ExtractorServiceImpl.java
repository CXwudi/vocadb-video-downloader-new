package mikufan.cx.vvd.extractor.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.util.FileNameUtil;
import mikufan.cx.vvd.extractor.config.IOConfig;
import mikufan.cx.vvd.extractor.label.ExtractContext;
import mikufan.cx.vvd.extractor.label.ExtractStatus;
import org.springframework.stereotype.Service;

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
  public ExtractStatus handleExtract(ExtractContext extractContext) {
    var extractor = extractContext.getAudioExtractor();
    var audioFileName = FileNameUtil.buildAudioFileName(extractContext.getSongInfo(), extractContext.getAudioExtension());
    log.info("Extracting to {} by {}", audioFileName, extractor.getName());

    return extractor.extractAudio(
        ioConfig.getInputDirectory().resolve(extractContext.getSongResource().getPvFileName()),
        ioConfig.getOutputDirectory(),
        audioFileName);
  }
}
