package mikufan.cx.vvd.taskproducer.component

import com.fasterxml.jackson.databind.ObjectMapper
import mikufan.cx.vocadbapiclient.model.SongForApiContract
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.taskproducer.model.Parameters
import mikufan.cx.vvd.taskproducer.model.VSongTask
import org.jeasy.batch.core.record.GenericRecord
import org.jeasy.batch.core.record.Header
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

/**
 * @date 2021-06-02
 * @author CX无敌
 */
@SpringBootTest
internal class ArtistFieldFixerTest(
  @Autowired val artistFieldFixer: ArtistFieldFixer,
  @Autowired val objectMapper: ObjectMapper
){

  lateinit var dummyRecord: GenericRecord<VSongTask>

  @BeforeEach
  fun setupRecord(){
    val jsonByteArr = javaClass.classLoader
        // get the test json that contains "various"
      .getResourceAsStream("test/PaⅢ.REVOLUTION  雄之助 vocadb api response.json")
      .reader().readText()
    val song: SongForApiContract = objectMapper.readValue(jsonByteArr, SongForApiContract::class.java)
    dummyRecord = GenericRecord(Header(1, "Test Record", LocalDateTime.now()), VSongTask(
      VSongLabel.builder().build(),
      Parameters(song, 1)
    ))
  }

  @Test @Disabled
  fun `should fix various artist field`(){
    val processedRecord = artistFieldFixer.processRecord(dummyRecord)
    assertEquals(
      "雄之助, 攻 feat. 初音ミク, GUMI, 鏡音リン",
      processedRecord.payload.parameters.songForApiContract!!.artistString!!)
  }

  @TestFactory
  fun `should fix unknown artist field`() = listOf(
      "GYARI feat. 鏡音リン V4X (Unknown), 鏡音レン V4X (Unknown)" to "GYARI feat. 鏡音リン V4X, 鏡音レン V4X",
      "100回嘔吐 feat. 初音ミク, 音街ウナ (Unknown), v flower" to "100回嘔吐 feat. 初音ミク, 音街ウナ, v flower",
      "れるりり feat. Fukase (Unknown)" to "れるりり feat. Fukase")
    .map { (unfixed, expected) ->
      DynamicTest.dynamicTest("can fix $unfixed to $expected") {
        val fixed = artistFieldFixer.removeUnknown(unfixed)
        assertEquals(expected, fixed)
      }
    }

  //TODO: more complex test
}