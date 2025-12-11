package mikufan.cx.vvd.taskproducer.component

import tools.jackson.databind.ObjectMapper
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vocadbapiclient.model.SongForApiContract
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.taskproducer.model.Parameters
import mikufan.cx.vvd.taskproducer.model.VSongTask
import org.jeasy.batch.core.record.GenericRecord
import org.jeasy.batch.core.record.Header
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

/**
 * @date 2021-06-12
 * @author CX无敌
 */
@SpringBootTest
internal class BeforeWriteValidatorTest(
  @Autowired val beforeWriteValidator: BeforeWriteValidator,
  @Autowired val objectMapper: ObjectMapper
) {

  lateinit var dummyRecord: GenericRecord<VSongTask>

  @BeforeEach
  fun setupRecord() {
    val jsonString = javaClass.classLoader
      // get the test json that contains "various"
      .getResourceAsStream("test/PaⅢ.REVOLUTION  雄之助 vocadb api response.json")
      .reader().use { it.readText() }
    val song: SongForApiContract = objectMapper.readValue(jsonString, SongForApiContract::class.java)
    dummyRecord = GenericRecord(
      Header(1, "Test Record", LocalDateTime.now()), VSongTask(
        VSongLabel.builder()
//          .order(-1)
//          .infoFileName(song.toInfoFileName())
          .build(),
        Parameters(song)
      )
    )
  }
  @Test
  fun `should fail`() {
    assertThrows(RuntimeVocaloidException::class.java) {
      beforeWriteValidator.processRecord(dummyRecord)
    }.also {
      log.error(it) { "Exception: " }
    }
  }
}

private val log = KInlineLogging.logger()
