package mikufan.cx.vvd.extractor.component

import mikufan.cx.vvd.commonkt.batch.toIterator
import mikufan.cx.vvd.extractor.util.SpringBootDirtyTestWithTestProfile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@SpringBootDirtyTestWithTestProfile
class LabelsReaderTest(
  private val labelsReader: LabelsReader
) {

  @Test
  fun readAllLabels() {
    val records = labelsReader.toIterator().asSequence().toList()
    assertThat(records).hasSize(5)
  }
}
