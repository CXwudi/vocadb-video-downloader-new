package mikufan.cx.vvd.downloader.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.vocadb.model.SongForApi;
import mikufan.cx.vvd.downloader.util.TestEnvHolder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;

/**
 * @author CX无敌
 * @date 2020-12-21
 */
@Slf4j
class PvDeciderImplTest extends TestEnvHolder {

  @Autowired
  private PvDecider pvDecider;

  @Autowired
  private ObjectMapper objectMapper;

  @Test @SneakyThrows
  void testRead(){
    var song = objectMapper.readValue(
        Path.of("src/test/resources/2019年V家新曲-测试用/【初音ミク】しう【MARETU】-songInfo.json").toFile(),
        SongForApi.class);
    var choosePreferredPv = pvDecider.choosePreferredPv(song.getPvs());
    log.info("choosePreferredPv = {}", choosePreferredPv);

  }

}