package mikufan.cx.vvd.extractor.service.tagger;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.ProcessUtil;
import mikufan.cx.vvd.extractor.config.EnvironmentConfig;
import mikufan.cx.vvd.extractor.config.IOConfig;
import mikufan.cx.vvd.extractor.label.ExtractStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

/**
 * @author CX无敌
 * @date 2020-12-31
 */
@Service @Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class NiconicoM4aAudioTagger implements AudioTagger {

  EnvironmentConfig environmentConfig;

  IOConfig ioConfig;

  @Override
  public String getName() {
    return "M4a Audio Tagger by python mutagen";
  }

  @Override
  public ExtractStatus handleTagging(
      Path audioFile, Path thumbnailFile, Path resourceFile, Path infoFile) throws InterruptedException {
    var scriptFile = getPythonScriptFile();
    var processBuilder = new ProcessBuilder(environmentConfig.getPythonLaunchCmd(), scriptFile.toAbsolutePath().toString(),
        "-i", audioFile.toAbsolutePath().toString(),
        "-t", thumbnailFile.toAbsolutePath().toString(),
        "-r", resourceFile.toAbsolutePath().toString(),
        "-if", infoFile.toAbsolutePath().toString()
    );
    try {
      log.debug("Executing extraction by commands: {}", processBuilder.command());
      ProcessUtil.runShortProcess(processBuilder.start(), log::info, log::debug);
      return ExtractStatus.success();
    } catch (IOException e) {
      var msg = String.format("Failed to add tag to %s", audioFile);
      log.error(msg, e);
      return ExtractStatus.failure(msg + ":" + e.getMessage());
    }

  }

  @SneakyThrows(URISyntaxException.class)
  private Path getPythonScriptFile() {
    ClassLoader classLoader = getClass().getClassLoader();
    var resource = classLoader.getResource("python/tag_m4a_niconico.py");
    return Path.of(resource.toURI());
  }
}
