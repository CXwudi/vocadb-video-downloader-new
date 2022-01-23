package mikufan.cx.vvd.extractor.config;

import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.threading.ProcessUtil;
import mikufan.cx.vvd.extractor.util.TestEnvHolder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

/**
 * @author CX无敌
 * @date 2020-12-29
 */
@Disabled
@Slf4j
class EnvironmentConfigTest extends TestEnvHolder {

  @Autowired
  private EnvironmentConfig environmentConfig;

  @Test
  void testExec() throws IOException, InterruptedException {
    var ffmpegLaunchCmd = environmentConfig.getFfmpegLaunchCmd();
    ffmpegLaunchCmd.addAll(List.of("-version"));
    var processBuilder = new ProcessBuilder(ffmpegLaunchCmd);
    ProcessUtil.runShortProcess(processBuilder.start(), log::info, log::debug);

    var pythonLaunchCmd = environmentConfig.getPythonLaunchCmd();
    pythonLaunchCmd.addAll(List.of("--version"));
    var processBuilder2 = new ProcessBuilder(pythonLaunchCmd);
    ProcessUtil.runShortProcess(processBuilder2.start(), log::info, log::debug);
  }
}