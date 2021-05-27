package mikufan.cx.vvd.taskproducer

import mu.KotlinLogging
import org.junit.jupiter.api.Test

/**
 * @date 2021-05-14
 * @author CX无敌
 */
internal class TaskProducerApplicationTest {
  @Test
  fun `should boot`(){
    log.debug { "success" }
  }
}

private val log = KotlinLogging.logger {}