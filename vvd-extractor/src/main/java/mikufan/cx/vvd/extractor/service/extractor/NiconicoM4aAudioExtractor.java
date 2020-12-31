package mikufan.cx.vvd.extractor.service.extractor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.ProcessUtil;
import mikufan.cx.vvd.extractor.config.EnvironmentConfig;
import mikufan.cx.vvd.extractor.label.ExtractStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This infact can handle any .mp4, .flv, .ts video file, as long as their audio are AAC
 * @author CX无敌
 * @date 2020-12-29
 */
@Service @Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class NiconicoM4aAudioExtractor implements AudioExtractor {

  EnvironmentConfig environmentConfig;

  @Override
  public String getName() {
    return "M4a Audio Extractor by ffmpeg only";
  }

  @Override
  public ExtractStatus extractAudio(Path pv, Path directory, String fileName) throws InterruptedException {
    var rawAudioFileName = fileName.replace(
        fileName.substring(fileName.lastIndexOf('.')),
        ".aac"
    );
    try {
      var extraction1Status = extractRawAudio(pv, directory, rawAudioFileName);
      if (extraction1Status.isFailure()){
        return extraction1Status;
      }
      var extraction2Status = wrapAudio(directory, rawAudioFileName, fileName);
      if (extraction2Status.isSucceed()){
        Files.delete(directory.resolve(rawAudioFileName));
      }
      return extraction2Status;
    } catch (IOException e) {
      return ExtractStatus.failure(String.format("Fail to extract audio for %s: %s", pv, e.getMessage()));
    }
  }

  private ExtractStatus extractRawAudio(Path pv, Path directory, String rawAudioFileName) throws IOException, InterruptedException {
    var extract1Pb = new ProcessBuilder(environmentConfig.getFfmpegLaunchCmd(),
        "-i", pv.toAbsolutePath().toString(),
        //answer yes to override
        "-y",
        // video no
        "-vn",
        // copy audio
        "-acodec", "copy",
        rawAudioFileName);
    extract1Pb.directory(directory.toFile());
    log.debug("Running first extraction with command: {}", extract1Pb.command());

    ProcessUtil.runShortProcess(extract1Pb.start(), log::info, log::debug);

    if (Files.exists(directory.resolve(rawAudioFileName))){
      return ExtractStatus.success();
    } else {
      return ExtractStatus.failure(String.format("Can not find extracted temp file %s", rawAudioFileName));
    }
  }

  private ExtractStatus wrapAudio(Path directory, String rawAudioFileName, String finalFileName) throws IOException, InterruptedException {
    var extract2Pb = new ProcessBuilder(environmentConfig.getFfmpegLaunchCmd(),
        "-i", directory.resolve(rawAudioFileName).toAbsolutePath().toString(),
        "-y",
        "-acodec", "copy",
        // simulate MP4Box way of extracting m4a
        "-movflags", "+faststart",
        finalFileName);
    extract2Pb.directory(directory.toFile());
    log.debug("Running second extraction with command: {}", extract2Pb.command());

    ProcessUtil.runShortProcess(extract2Pb.start(), log::info, log::debug);

    if (Files.exists(directory.resolve(finalFileName))){
      return ExtractStatus.success();
    } else {
      return ExtractStatus.failure(String.format("Can not find final extracted file %s", rawAudioFileName));
    }
  }
}
