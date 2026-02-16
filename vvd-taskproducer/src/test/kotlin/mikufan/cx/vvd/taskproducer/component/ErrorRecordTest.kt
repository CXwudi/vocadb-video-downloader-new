package mikufan.cx.vvd.taskproducer.component

import com.fasterxml.jackson.databind.ObjectMapper
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.commonkt.batch.RecordErrorWriter
import mikufan.cx.vvd.commonkt.vocadb.api.model.SongForApiContract
import mikufan.cx.vvd.taskproducer.config.IOConfig
import mikufan.cx.vvd.taskproducer.model.Parameters
import mikufan.cx.vvd.taskproducer.model.VSongTask
import org.jeasy.batch.core.record.GenericRecord
import org.jeasy.batch.core.record.Header
import org.jeasy.batch.core.record.StringRecord
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

/**
 * @date 2021-06-14
 * @author CX无敌
 */
@SpringBootTest
class ErrorRecordTest(
  @Autowired val recordErrorWriter: RecordErrorWriter,
  @Autowired val objectMapper: ObjectMapper,
  @Autowired ioConfig: IOConfig
) {
  val errorDirectory = ioConfig.errorDirectory
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
        VSongLabel.builder().build(),
        Parameters(song)
      )
    )
  }

  @Test
  fun `should write error vsong task with exception correctly`() {
    recordErrorWriter.handleError(dummyRecord, RuntimeVocaloidException("some error fufufu"))
    assertTrue(errorDirectory.resolve("【various】PaⅢ.REVOLUTION【雄之助, 攻】[299406]-error.json").isRegularFile())
  }

  @Test
  fun `should write other error task with exception correctly`() {
    recordErrorWriter.handleError(
      StringRecord(Header(1, "Some Other String Record", LocalDateTime.now()), "my dummy string"),
      RuntimeVocaloidException("some other errors fufufu")
    )
    val list = errorDirectory.listDirectoryEntries("failure record*")
    assertTrue(list.any { it.name.contains("failure record") })
  }
}
