package mikufan.cx.vvd.downloader.component

import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldNotThrowAnyUnit
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainIgnoringCase
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.downloader.util.SpringBootTestWithTestProfile
import mikufan.cx.vvd.downloader.util.SpringShouldSpec
import org.jeasy.batch.core.record.Record
import org.springframework.test.annotation.DirtiesContext

@DirtiesContext
@SpringBootTestWithTestProfile(
  customProperties = [
    "io.input-directory=src/test/resources/2020年V家新曲 with failing labels"
  ]
)
class BeforeProcessLabelValidatorFailureTest(
  val labelsReader: LabelsReader,
  val beforeProcessLabelValidator: BeforeProcessLabelValidator
) : SpringShouldSpec({

  val shouldFailValidationWith: Record<VSongLabel>.(String) -> Unit = { containedString ->
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
      record1.payload.order shouldBe 0
      record1.shouldFailValidationWith("must be greater than or equal to 1")
    }

    should("not pass if has blank info file name") {
      val record2 = labelsReader.readRecord()!!
      record2.payload.order shouldBe 37
      record2.payload.infoFileName shouldBe " "
      record2.shouldFailValidationWith("must not be blank")
    }

    should("not pass if has null info file name") {
      val record3 = labelsReader.readRecord()!!
      record3.payload.order shouldBe 139
      record3.payload.infoFileName shouldBe null
      record3.shouldFailValidationWith("must not be blank")
    }
  }
})

@DirtiesContext
@SpringBootTestWithTestProfile
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