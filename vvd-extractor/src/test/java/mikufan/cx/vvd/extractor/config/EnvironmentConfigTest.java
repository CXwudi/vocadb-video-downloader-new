package mikufan.cx.vvd.extractor.config;

import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.threading.ProcessUtil;
import mikufan.cx.vvd.extractor.util.TestEnvHolder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

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
    var processBuilder = new ProcessBuilder(environmentConfig.getFfmpegLaunchCmd(), "-version");
    ProcessUtil.runShortProcess(processBuilder.start(), log::info, log::debug);
  }
}