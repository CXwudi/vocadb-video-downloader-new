package mikufan.cx.vvd.taskproducer

import mikufan.cx.vvd.taskproducer.util.TestEnvHolder
import mu.KotlinLogging
import org.junit.jupiter.api.Test

/**
 * @date 2021-05-14
 * @author CX无敌
 */
internal class TaskProducerApplicationTest: TestEnvHolder() {
  @Test
  fun `should boot`(){
    log.debug { "success" }
  }
}

private val log = KotlinLogging.logger {}