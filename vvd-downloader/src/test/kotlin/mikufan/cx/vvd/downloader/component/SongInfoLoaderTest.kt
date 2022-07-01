package mikufan.cx.vvd.downloader.component

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import mikufan.cx.vvd.downloader.util.SpringBootDirtyTestWithTestProfile
import mikufan.cx.vvd.downloader.util.SpringShouldSpec

@SpringBootDirtyTestWithTestProfile
class SongInfoLoaderTest(
  val labelsReader: LabelsReader,
  val songInfoLoader: SongInfoLoader
) : SpringShouldSpec({

  context("map first 15 labels in sample input directory to tasks") {
    for (i in 1..15) {
      val labelRec = labelsReader.readRecord()!!
      val orderFromLabel = labelRec.payload.label.order
      should("correctly map to task on $labelRec") {
        val taskRecord = songInfoLoader.processRecord(labelRec)
        val header = taskRecord.header
        val task = taskRecord.payload

        header.number shouldBe i
        task.label.labelFileName shouldBe labelRec.payload.label.labelFileName
        task.label.infoFileName shouldBe labelRec.payload.label.infoFileName
        task.label.order shouldBe orderFromLabel // the order from label can be random as users may combine or deletes some tasks in their input folder
        task.parameters.songForApiContract shouldNotBe null
        // no need to compare song name and info file name, as the info file name might be normalized
      }
    }
  }
})
