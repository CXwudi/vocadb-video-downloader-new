package mikufan.cx.vvd.taskproducer.config

import mikufan.cx.vocadbapiclient.client.ApiClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 * @date 2021-05-29
 * @author CX无敌
 */
@SpringBootTest
internal class ApiConfigTest(
  @Autowired val apiClient: ApiClient
){

  @Test
  fun `should correctly configure api client`(){
    assertEquals("https://vocadb.net", apiClient.basePath)
    //assertEquals("vocadb-video-downloader-new test agent (created by CXwudi)")
  }
}