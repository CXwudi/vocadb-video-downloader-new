package mikufan.cx.vvd.extractor.component

import io.kotest.matchers.shouldBe
import mikufan.cx.vvd.commonkt.batch.toIterator
import mikufan.cx.vvd.extractor.util.SpringBootDirtyTestWithTestProfile
import mikufan.cx.vvd.extractor.util.SpringShouldSpec

@SpringBootDirtyTestWithTestProfile
class LabelsReaderTest(
  private val labelsReader: LabelsReader
) : SpringShouldSpec({

  context("reader") {
    should("read all labels") {
      val records = labelsReader.toIterator().asSequence().toList()
      records.size shouldBe 4
    }
  }
})
