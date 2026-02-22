package mikufan.cx.vvd.downloader.component

import mikufan.cx.vvd.downloader.model.VSongTask
import mikufan.cx.vvd.downloader.util.SpringBootDirtyTestWithTestProfile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.jeasy.batch.core.record.Record

@SpringBootDirtyTestWithTestProfile
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SongInfoLoaderTest(
  private val labelsReader: LabelsReader,
  private val songInfoLoader: SongInfoLoader
) {

  private val labelRecords = mutableListOf<Record<VSongTask>>()

  @BeforeAll
  fun loadRecords() {
    repeat(15) {
      labelRecords.add(labelsReader.readRecord()!!)
    }
  }

  @ParameterizedTest(name = "map task for label {0}")
  @MethodSource("recordSource")
  fun mapTask(index: Int, labelRec: Record<VSongTask>) {
    val orderFromLabel = labelRec.payload.label.order
    val taskRecord = songInfoLoader.processRecord(labelRec)
    val header = taskRecord.header
    val task = taskRecord.payload

    assertThat(header.number).isEqualTo(index.toLong())
    assertThat(task.label.labelFileName).isEqualTo(labelRec.payload.label.labelFileName)
    assertThat(task.label.infoFileName).isEqualTo(labelRec.payload.label.infoFileName)
    assertThat(task.label.order).isEqualTo(orderFromLabel)
    assertThat(task.parameters.songForApiContract).isNotNull()
  }

  fun recordSource(): List<Arguments> =
    labelRecords.mapIndexed { index, record -> Arguments.of(index + 1, record) }
}
