package mikufan.cx.vvd.downloader.config.preference;

import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.downloader.util.TestEnvHolder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author CX无敌
 * @date 2020-12-21
 */
@Slf4j
class PvPreferenceConfigTest extends TestEnvHolder {

  @Autowired
  private PvPreferenceConfig pvPreferenceConfig;

  @Test
  void testConfig(){
    log.info("pvPreferenceConfig = {}", pvPreferenceConfig.getPreference());
  }
}