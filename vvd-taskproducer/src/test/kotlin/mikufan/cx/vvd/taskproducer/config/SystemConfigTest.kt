package mikufan.cx.vvd.taskproducer.config

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

/**
 * @date 2021-06-18
 * @author CX无敌
 */
@SpringBootTest(properties = [
  "config.base-url=123",
  "config.user-agent=",
  "config.api-page-size=0",
  "config.batch-size=0"
])
@Disabled // disabled because the failure is from application itself, not from test cases
internal class SystemConfigTest{

  @Test
  fun `validation should fail`() {
    assertThrows(Exception::class.java){
    }

  }
}