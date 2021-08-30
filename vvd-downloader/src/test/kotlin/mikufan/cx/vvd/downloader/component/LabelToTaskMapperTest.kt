package mikufan.cx.vvd.downloader.component

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import mikufan.cx.vvd.downloader.util.SpringBootTestWithTestProfile
import mikufan.cx.vvd.downloader.util.SpringShouldSpec
import org.springframework.test.annotation.DirtiesContext

@DirtiesContext
@SpringBootTestWithTestProfile
class LabelToTaskMapperTest(
  val labelsReader: LabelsReader,
  val labelToTaskMapper: LabelToTaskMapper
) : SpringShouldSpec({

  context("map first 15 labels in sample input directory to tasks") {
    for (i in 0 until 15) {
      val labelRec = labelsReader.readRecord()!!
      val orderFromLabel = labelRec.payload.order
      should("correctly map to task on $labelRec") {
        val taskRecord = labelToTaskMapper.processRecord(labelRec)
        val header = taskRecord.header
        val task = taskRecord.payload

        header.number shouldBe orderFromLabel
        task.label.infoFileName shouldBe labelRec.payload.infoFileName
        task.label.order shouldBe orderFromLabel
        task.parameters.songForApiContract shouldNotBe null
        // no need to compare song name and info file name, as the info file name might be normalized
      }
    }
  }
})
