package mikufan.cx.vvd.downloader.component

import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.downloader.model.VSongTask
import mikufan.cx.vvd.downloader.util.SpringBootDirtyTestWithTestProfile
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.jeasy.batch.core.record.Record

@SpringBootDirtyTestWithTestProfile(
  customProperties = [
    "io.input-directory=src/test/resources/2020年V家新曲 with failing labels"
  ]
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BeforeProcessLabelValidatorFailureTest(
  private val labelsReader: LabelsReader,
  private val beforeProcessLabelValidator: BeforeProcessLabelValidator
) {

  private data class FailureCase(
    val name: String,
    val record: Record<VSongTask>,
    val expectedMessage: String,
    val assertRecord: (Record<VSongTask>) -> Unit
  ) {
    override fun toString(): String = name
  }

  private fun Record<VSongTask>.assertFailValidationWith(containedString: String) {
    assertThatThrownBy { beforeProcessLabelValidator.processRecord(this) }
      .isInstanceOf(RuntimeVocaloidException::class.java)
      .message()
      .containsIgnoringCase(containedString)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("failureCases")
  fun failValidation(failureCase: FailureCase) {
    failureCase.assertRecord(failureCase.record)
    failureCase.record.assertFailValidationWith(failureCase.expectedMessage)
  }

  fun failureCases(): List<FailureCase> {
    val record1 = labelsReader.readRecord()!!
    val record2 = labelsReader.readRecord()!!
    val record3 = labelsReader.readRecord()!!
    val record4 = labelsReader.readRecord()!!

    return listOf(
      FailureCase(
        name = "missing order",
        record = record1,
        expectedMessage = "must be greater than 0",
        assertRecord = { record ->
          assertThat(record.payload.label.order).isEqualTo(0)
        }
      ),
      FailureCase(
        name = "blank info file name",
        record = record2,
        expectedMessage = "must not be blank",
        assertRecord = { record ->
          assertThat(record.payload.label.order).isEqualTo(37)
          assertThat(record.payload.label.infoFileName).isEqualTo(" ")
        }
      ),
      FailureCase(
        name = "null info file name",
        record = record3,
        expectedMessage = "must not be blank",
        assertRecord = { record ->
          assertThat(record.payload.label.order).isEqualTo(139)
          assertThat(record.payload.label.infoFileName).isNull()
        }
      ),
      FailureCase(
        name = "blank label file name",
        record = record4,
        expectedMessage = "must not be blank",
        assertRecord = { record ->
          assertThat(record.payload.label.order).isEqualTo(164)
          assertThat(record.payload.label.labelFileName).isEqualTo(" ")
        }
      )
    )
  }
}

@SpringBootDirtyTestWithTestProfile
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BeforeProcessLabelValidatorSuccessTest(
  private val labelsReader: LabelsReader,
  private val beforeProcessLabelValidator: BeforeProcessLabelValidator
) {

  private val records = mutableListOf<Record<VSongTask>>()

  @BeforeAll
  fun loadRecords() {
    repeat(10) {
      records.add(labelsReader.readRecord()!!)
    }
  }

  @ParameterizedTest(name = "success on {0}")
  @MethodSource("recordSource")
  fun successOn(record: Record<VSongTask>) {
    assertDoesNotThrow { beforeProcessLabelValidator.processRecord(record) }
  }

  fun recordSource(): List<Record<VSongTask>> = records
}
