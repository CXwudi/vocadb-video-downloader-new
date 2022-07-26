package mikufan.cx.vvd.extractor.component

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.string.shouldContainIgnoringCase
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.commonkt.batch.toIterator
import mikufan.cx.vvd.extractor.util.SpringBootDirtyTestWithTestProfile
import mikufan.cx.vvd.extractor.util.SpringShouldSpec

@SpringBootDirtyTestWithTestProfile
class BeforeProcessLabelValidatorTest(
  private val labelsReader: LabelsReader,
  private val beforeProcessLabelValidator: BeforeProcessLabelValidator
) : SpringShouldSpec({

  context("pass validation") {
    labelsReader.toIterator().forEach {
      should("not throw exception on ${it.payload.label.labelFileName}") {
        shouldNotThrow<RuntimeVocaloidException> {
          beforeProcessLabelValidator.processRecord(it)
        }
      }
    }
  }
})

@SpringBootDirtyTestWithTestProfile
class BeforeProcessLabelValidatorFailureTest(
  private val labelsReader: LabelsReader,
  private val beforeProcessLabelValidator: BeforeProcessLabelValidator
) : SpringShouldSpec({

  context("fail validation") {
    val oneRecord = labelsReader.readRecord()!!
    oneRecord.payload.label.apply {
      this.downloaderName = ""
    }

    should("throw exception on ${oneRecord.payload.label.labelFileName}") {
      val exp = shouldThrow<RuntimeVocaloidException> {
        beforeProcessLabelValidator.processRecord(oneRecord)
      }
      exp.message shouldContainIgnoringCase "must not be blank"
    }

    val anotherRecord = labelsReader.readRecord()!!
    anotherRecord.payload.label.apply {
      this.pvFileName = ""
      this.audioFileName = ""
    }

    should("throw exception on ${anotherRecord.payload.label.labelFileName}") {
      val exp = shouldThrow<RuntimeVocaloidException> {
        beforeProcessLabelValidator.processRecord(anotherRecord)
      }
      exp.message shouldContainIgnoringCase "must contain at least one of a PV file or an audio file"
    }

    anotherRecord.payload.label.apply {
      this.pvFileName = "123"
      this.audioFileName = "123"
      this.thumbnailFileName = ""
    }

    should("throw exception on ${anotherRecord.payload.label.labelFileName} again") {
      val exp = shouldThrow<RuntimeVocaloidException> {
        beforeProcessLabelValidator.processRecord(anotherRecord)
      }
      exp.message shouldContainIgnoringCase "must contain a thumbnail file"
    }

    val thirdRecord = labelsReader.readRecord()!!
    thirdRecord.payload.label.apply {
      this.pvVocaDbId = 0
    }

    should("throw exception on ${thirdRecord.payload.label.labelFileName}") {
      val exp = shouldThrow<RuntimeVocaloidException> {
        beforeProcessLabelValidator.processRecord(thirdRecord)
      }

      exp.message shouldContainIgnoringCase "must be greater than 0"
    }
  }
})
