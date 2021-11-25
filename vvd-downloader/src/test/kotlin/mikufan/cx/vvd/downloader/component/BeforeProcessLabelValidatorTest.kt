package mikufan.cx.vvd.downloader.component

import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldNotThrowAnyUnit
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainIgnoringCase
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.downloader.model.VSongTask
import mikufan.cx.vvd.downloader.util.SpringBootDirtyTestWithTestProfile
import mikufan.cx.vvd.downloader.util.SpringShouldSpec
import org.jeasy.batch.core.record.Record

@SpringBootDirtyTestWithTestProfile(
  customProperties = [
    "io.input-directory=src/test/resources/2020年V家新曲 with failing labels"
  ]
)
class BeforeProcessLabelValidatorFailureTest(
  val labelsReader: LabelsReader,
  val beforeProcessLabelValidator: BeforeProcessLabelValidator
) : SpringShouldSpec({

  val shouldFailValidationWith: Record<VSongTask>.(String) -> Unit = { containedString ->
    try {
      beforeProcessLabelValidator.processRecord(this)
      fail("fail to catch the no order validation error")
    } catch (e: RuntimeVocaloidException) {
      e.message shouldContainIgnoringCase containedString
    }
  }
  context("validating the read label") {
    should("not pass if missing an order") {
      val record1 = labelsReader.readRecord()!!
      record1.payload.label.order shouldBe 0
      record1.shouldFailValidationWith("must be greater than or equal to 1")
    }

    should("not pass if has blank info file name") {
      val record2 = labelsReader.readRecord()!!
      record2.payload.label.order shouldBe 37
      record2.payload.label.infoFileName shouldBe " "
      record2.shouldFailValidationWith("must not be blank")
    }

    should("not pass if has null info file name") {
      val record3 = labelsReader.readRecord()!!
      record3.payload.label.order shouldBe 139
      record3.payload.label.infoFileName shouldBe null
      record3.shouldFailValidationWith("must not be blank")
    }

    should("not pass if has blank label file name") {
      val record2 = labelsReader.readRecord()!!
      record2.payload.label.order shouldBe 164
      record2.payload.label.labelFileName shouldBe " "
      record2.shouldFailValidationWith("must not be blank")
    }
  }
})

@SpringBootDirtyTestWithTestProfile
class BeforeProcessLabelValidatorSuccessTest(
  val labelsReader: LabelsReader,
  val beforeProcessLabelValidator: BeforeProcessLabelValidator
) : SpringShouldSpec({

  // our own sample input directory is the output the task producer, should be all working
  context("read and validate first 10 labels in sample input directory") {
    for (i in 0 until 10) {
      val record = labelsReader.readRecord()!!
      should("success on $record") {
        shouldNotThrowAnyUnit { beforeProcessLabelValidator.processRecord(record) }
      }
    }
  }
})
