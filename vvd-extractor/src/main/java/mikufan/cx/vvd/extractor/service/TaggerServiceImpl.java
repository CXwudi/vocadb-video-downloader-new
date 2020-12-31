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
import org.springframework.validation.annotation.Validated;

/**
 * @author CX无敌
 * @date 2020-12-31
 */
@Service @Slf4j @Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaggerServiceImpl implements TaggerService {

  IOConfig ioConfig;

  @Override @SneakyThrows(InterruptedException.class)
  public ExtractStatus handleTagging(ExtractContext extractContext) {
    var tagger = extractContext.getAudioTagger();
    var audioFileName = FileNameUtil.buildAudioFileName(extractContext.getSongInfo(), extractContext.getAudioExtension());
    log.info("Tagging {} by {}", audioFileName, tagger.getName());

    return tagger.handleTagging(
        ioConfig.getOutputDirectory().resolve(audioFileName),
        ioConfig.getInputDirectory().resolve(extractContext.getSongResource().getThumbnailFileName()),
        ioConfig.getInputDirectory().resolve(FileNameUtil.buildResourceJsonFileName(extractContext.getSongInfo())),
        ioConfig.getInputDirectory().resolve(FileNameUtil.buildInfoJsonFileName(extractContext.getSongInfo()))
    );
  }
}
