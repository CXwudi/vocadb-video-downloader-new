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
    var count = 0L
    do {
      val record = labelsReader.readRecord()
      count += if (record != null) 1L else 0L
      if (record != null) {
        assertThat(count).isEqualTo(record.payload.label.order)
      }
    } while (record != null)
    // our own sample input directory have 154 labels
    assertThat(count).isEqualTo(154L) // WARNING: update the count when changed to new list
  }
}
