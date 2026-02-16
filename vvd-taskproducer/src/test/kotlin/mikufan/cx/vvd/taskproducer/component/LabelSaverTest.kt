package mikufan.cx.vvd.taskproducer.component

import tools.jackson.databind.ObjectMapper
import kotlinx.coroutines.runBlocking
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.commonkt.naming.toProperFileName
import mikufan.cx.vvd.commonkt.vocadb.api.model.SongForApiContract
import mikufan.cx.vvd.taskproducer.config.IOConfig
import mikufan.cx.vvd.taskproducer.model.Parameters
import mikufan.cx.vvd.taskproducer.model.VSongTask
import mikufan.cx.vvd.taskproducer.util.toInfoFileName
import mikufan.cx.vvd.taskproducer.util.toLabelFileName
import org.jeasy.batch.core.record.GenericRecord
import org.jeasy.batch.core.record.Header
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import kotlin.io.path.isRegularFile

/**
 * @author CX无敌
 * @date 2021-06-04
 */
@SpringBootTest
internal class LabelSaverTest(
  @Autowired val labelSaver: LabelSaver, @Autowired val objectMapper: ObjectMapper, @Autowired ioConfig: IOConfig
) {
  val outputDirectory = ioConfig.outputDirectory

  lateinit var dummyRecord: GenericRecord<VSongTask>

  @BeforeEach
  fun setupRecord() {
    val jsonString = javaClass.classLoader
      // get the test json that contains "various"
      .getResourceAsStream("test/PaⅢ.REVOLUTION  雄之助 vocadb api response.json").reader().use { it.readText() }
    val song: SongForApiContract = objectMapper.readValue(jsonString, SongForApiContract::class.java)
    dummyRecord = GenericRecord(
      Header(1, "Test Record", LocalDateTime.now()),
      VSongTask(
        VSongLabel.builder().order(1).infoFileName(song.toProperFileName().toInfoFileName())
          .labelFileName(song.toProperFileName().toLabelFileName()).build(),
        Parameters(song)
      )
    )
  }

  @Test
  fun `should able to write label and song info on proper location`() = runBlocking {
    labelSaver.write(dummyRecord)
    assertTrue(outputDirectory.resolve("【various】PaⅢ.REVOLUTION【雄之助, 攻】[299406]-label.json").isRegularFile())
    assertTrue(outputDirectory.resolve("【various】PaⅢ.REVOLUTION【雄之助, 攻】[299406]-songInfo.json").isRegularFile())
  }
}

