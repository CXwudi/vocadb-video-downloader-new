package mikufan.cx.vvd.taskproducer.component

import mikufan.cx.vvd.taskproducer.model.VSongTask
import mu.KotlinLogging
import org.jeasy.batch.core.record.Record
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 * @date 2021-06-01
 * @author CX无敌
 */
@SpringBootTest(properties = [
  "io.input-list-id=9197",
  "config.api-page-size=15"
])
@Disabled
internal class ListReaderTest(
  @Autowired val listReader: ListReader
){

  @Test
  fun `should lazily read song list`(){
    var record: Record<VSongTask>?
    var counter = 0
    do {
      record = listReader.readRecord()
      record?.also {
        log.debug { "$it" }
        counter++
      }
    } while (record != null)
    assertEquals(33, counter)
  }
}

private val log = KotlinLogging.logger {}