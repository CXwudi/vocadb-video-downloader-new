package mikufan.cx.vvd.extractor.service.extractor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.threading.ProcessUtil;
import mikufan.cx.vvd.extractor.config.EnvironmentConfig;
import mikufan.cx.vvd.extractor.model.ExtractStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author CX无敌
 * @date 2020-12-29
 */
@Service @Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class YoutubeOpusAudioExtractor implements AudioExtractor {

  EnvironmentConfig environmentConfig;

  @Override
  public String getName() {
    return "Opus Audio Extractor by ffmpeg only";
  }

  @Override
  public ExtractStatus extractAudio(Path pv, Path directory, String fileName) throws InterruptedException {
    var extract1Pb = new ProcessBuilder(environmentConfig.getFfmpegLaunchCmd(),
        "-i", pv.toAbsolutePath().toString(),
        //answer yes to override
        "-y",
        // video no
        "-vn",
        // copy audio
        "-acodec", "copy",
        fileName);
    extract1Pb.directory(directory.toFile());
    log.debug("Running extraction with command: {}", extract1Pb.command());

    try {
      ProcessUtil.runShortProcess(extract1Pb.start(), log::info, log::debug);
    } catch (IOException e){
      return ExtractStatus.failure(String.format("Fail to extract audio for %s: %s", pv, e.getMessage()));
    }

    if (Files.exists(directory.resolve(fileName))){
      return ExtractStatus.success();
    } else {
      return ExtractStatus.failure(String.format("Can not find extracted file %s", fileName));
    }
  }
}
