package mikufan.cx.vvd.taskproducer

import mu.KotlinLogging
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

/**
 * @date 2021-05-14
 * @author CX无敌
 */
@SpringBootTest
internal class TaskProducerApplicationTest {
  @Test
  fun `should boot`(){
    log.debug { "success" }
  }
}

private val log = KotlinLogging.logger {}