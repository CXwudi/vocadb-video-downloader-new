package mikufan.cx.vvd.downloader.component

import mikufan.cx.vvd.downloader.util.SpringBootDirtyTestWithTestProfile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@SpringBootDirtyTestWithTestProfile
class LabelsReaderTest(
  val labelsReader: LabelsReader
) {
  @Test
  fun worksOnNormalInputFromTaskProducer() {
    var count = 0
    do {
      val record = labelsReader.readRecord()
      count += if (record != null) 1 else 0
      if (record != null) {
        assertThat(count).isEqualTo(record.payload.label.order)
      }
    } while (record != null)
    // our own sample input directory have 170 labels
    assertThat(count).isEqualTo(154) // WARNING: update the count when changed to new list
  }
}
