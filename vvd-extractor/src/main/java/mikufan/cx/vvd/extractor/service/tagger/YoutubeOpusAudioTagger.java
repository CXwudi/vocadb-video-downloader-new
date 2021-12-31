package mikufan.cx.vvd.extractor.service.tagger;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.threading.ProcessUtil;
import mikufan.cx.vvd.extractor.config.EnvironmentConfig;
import mikufan.cx.vvd.extractor.model.ExtractStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CX无敌
 * @date 2020-12-31
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class YoutubeOpusAudioTagger implements AudioTagger {

  EnvironmentConfig environmentConfig;

  @Override
  public String getName() {
    return "Opus Audio Tagger by python mutagen";
  }

  @Override
  public ExtractStatus handleTagging(
      Path audioFile, Path thumbnailFile, Path resourceFile, Path infoFile) throws InterruptedException {
    var scriptFile = getPythonScriptFile();
    List<String> cmdList = new ArrayList<>();
    cmdList.addAll(environmentConfig.getPythonLaunchCmd());
    cmdList.addAll(List.of(
        scriptFile.toAbsolutePath().toString(),
        "-i", audioFile.toAbsolutePath().toString(),
        "-t", thumbnailFile.toAbsolutePath().toString(),
        "-r", resourceFile.toAbsolutePath().toString(),
        "-if", infoFile.toAbsolutePath().toString()));
    var processBuilder = new ProcessBuilder(cmdList);
    try {
      log.debug("Executing extraction by commands: {}", processBuilder.command());
      var returnCode = ProcessUtil.runShortProcess(processBuilder.start(), log::info, log::debug);
      if (returnCode == 0) {
        return ExtractStatus.success();
      } else {
        return ExtractStatus.failure(String.format("Failed to add tag to %s due to python script bug, please check log", audioFile));
      }
    } catch (IOException e) {
      var msg = String.format("Failed to add tag to %s", audioFile);
      log.error(msg, e);
      return ExtractStatus.failure(msg + ":" + e.getMessage());
    }

  }

  @SneakyThrows(URISyntaxException.class)
  private Path getPythonScriptFile() {
    ClassLoader classLoader = getClass().getClassLoader();
    var resource = classLoader.getResource("python/tag_opusogg_youtube.py");
    return Path.of(resource.toURI());
  }
}
