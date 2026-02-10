package mikufan.cx.vvd.taskproducer.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 * @date 2021-05-29
 * @author CX无敌
 */
@SpringBootTest
internal class ApiConfigTest(
  @Autowired val vocaDbClient: VocaDbClient,
  @Autowired val systemConfig: SystemConfig
){

  @Test
  fun `should correctly configure vocadb client`(){
    assertNotNull(vocaDbClient)
    assertEquals("https://vocadb.net", systemConfig.baseUrl)
    assertEquals("vocadb-video-downloader-new test agent (created by CXwudi)", systemConfig.userAgent)
  }
}
