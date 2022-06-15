package mikufan.cx.vvd.extractor.component

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
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
      this.pvFileName = ""
      this.audioFileName = ""
    }

    should("throw exception on ${oneRecord.payload.label.labelFileName}") {
      shouldThrow<RuntimeVocaloidException> {
        beforeProcessLabelValidator.processRecord(oneRecord)
      }
    }
  }
})
