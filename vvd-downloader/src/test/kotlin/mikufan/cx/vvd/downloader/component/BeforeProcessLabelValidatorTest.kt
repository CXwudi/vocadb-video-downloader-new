package mikufan.cx.vvd.downloader.component

import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.downloader.model.VSongTask
import mikufan.cx.vvd.downloader.util.SpringBootDirtyTestWithTestProfile
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
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

  private fun Record<VSongTask>.assertFailValidationWith(containedString: String) {
    assertThatThrownBy { beforeProcessLabelValidator.processRecord(this) }
      .isInstanceOf(RuntimeVocaloidException::class.java)
      .message()
      .containsIgnoringCase(containedString)
  }

  @Test
  fun failValidation() {
    val record1 = labelsReader.readRecord()!!
    assertThat(record1.payload.label.order).isEqualTo(0)
    record1.assertFailValidationWith("must be greater than 0")

    val record2 = labelsReader.readRecord()!!
    assertThat(record2.payload.label.order).isEqualTo(37)
    assertThat(record2.payload.label.infoFileName).isEqualTo(" ")
    record2.assertFailValidationWith("must not be blank")

    val record3 = labelsReader.readRecord()!!
    assertThat(record3.payload.label.order).isEqualTo(139)
    assertThat(record3.payload.label.infoFileName).isNull()
    record3.assertFailValidationWith("must not be blank")

    val record4 = labelsReader.readRecord()!!
    assertThat(record4.payload.label.order).isEqualTo(164)
    assertThat(record4.payload.label.labelFileName).isEqualTo(" ")
    record4.assertFailValidationWith("must not be blank")
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
