package mikufan.cx.vvd.extractor.component

import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.commonkt.batch.toIterator
import mikufan.cx.vvd.extractor.model.VSongTask
import mikufan.cx.vvd.extractor.util.SpringBootDirtyTestWithTestProfile
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.jeasy.batch.core.record.Record

@SpringBootDirtyTestWithTestProfile
class BeforeProcessLabelValidatorTest(
  private val labelsReader: LabelsReader,
  private val beforeProcessLabelValidator: BeforeProcessLabelValidator
) {

  @Test
  fun passValidation() {
    labelsReader.toIterator().forEach {
      assertDoesNotThrow {
        beforeProcessLabelValidator.processRecord(it)
      }
    }
  }
}

@SpringBootDirtyTestWithTestProfile
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BeforeProcessLabelValidatorFailureTest(
  private val labelsReader: LabelsReader,
  private val beforeProcessLabelValidator: BeforeProcessLabelValidator
) {

  data class FailureCase(
    val name: String,
    val record: Record<VSongTask>,
    val expectedMessage: String,
    val assertRecord: (Record<VSongTask>) -> Unit
  ) {
    override fun toString(): String = name
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("failureCases")
  fun failValidation(failureCase: FailureCase) {
    failureCase.assertRecord(failureCase.record)
    assertThatThrownBy { beforeProcessLabelValidator.processRecord(failureCase.record) }
      .isInstanceOf(RuntimeVocaloidException::class.java)
      .message()
      .containsIgnoringCase(failureCase.expectedMessage)
  }

  fun failureCases(): List<FailureCase> {
    val oneRecord = labelsReader.readRecord()!!
    val anotherRecord = labelsReader.readRecord()!!
    val thirdRecord = labelsReader.readRecord()!!

    return listOf(
      FailureCase(
        name = "missing downloader name",
        record = oneRecord,
        expectedMessage = "must not be blank",
        assertRecord = { record ->
          record.payload.label.apply {
            this.downloaderName = ""
          }
        }
      ),
      FailureCase(
        name = "missing pv or audio file",
        record = anotherRecord,
        expectedMessage = "must contain at least one of a PV file or an audio file",
        assertRecord = { record ->
          record.payload.label.apply {
            this.pvFileName = ""
            this.audioFileName = ""
          }
        }
      ),
      FailureCase(
        name = "missing thumbnail file",
        record = anotherRecord,
        expectedMessage = "must contain a thumbnail file",
        assertRecord = { record ->
          record.payload.label.apply {
            this.pvFileName = "123"
            this.audioFileName = "123"
            this.thumbnailFileName = ""
          }
        }
      ),
      FailureCase(
        name = "missing voca db pv id",
        record = thirdRecord,
        expectedMessage = "must be greater than 0",
        assertRecord = { record ->
          record.payload.label.apply {
            this.vocaDbPvId = 0
          }
        }
      )
    )
  }
}
