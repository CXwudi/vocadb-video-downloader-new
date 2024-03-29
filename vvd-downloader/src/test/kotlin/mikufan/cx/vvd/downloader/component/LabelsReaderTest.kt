package mikufan.cx.vvd.downloader.component

import io.kotest.matchers.shouldBe
import mikufan.cx.vvd.downloader.util.SpringBootDirtyTestWithTestProfile
import mikufan.cx.vvd.downloader.util.SpringShouldSpec

@SpringBootDirtyTestWithTestProfile
class LabelsReaderTest(
  val labelsReader: LabelsReader
) : SpringShouldSpec({

  context("reading input directory") {
    should("works on normal input from task producer") {
      var count = 0
      do {
        val record = labelsReader.readRecord()
        count += if (record != null) 1 else 0
        if (record != null) {
          count shouldBe record.payload.label.order
        }
      } while (record != null)
      // our own sample input directory have 170 labels
      count shouldBe 154 // WARNING: update the count when changed to new list
    }
  }
})
