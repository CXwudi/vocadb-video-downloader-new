package mikufan.cx.vvd.extractor.component

import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.commonkt.batch.toIterator
import mikufan.cx.vvd.extractor.util.SpringBootDirtyTestWithTestProfile
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test

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
class BeforeProcessLabelValidatorFailureTest(
  private val labelsReader: LabelsReader,
  private val beforeProcessLabelValidator: BeforeProcessLabelValidator
) {

  @Test
  fun failValidation() {
    val oneRecord = labelsReader.readRecord()!!
    oneRecord.payload.label.apply {
      this.downloaderName = ""
    }

    assertThatThrownBy { beforeProcessLabelValidator.processRecord(oneRecord) }
      .isInstanceOf(RuntimeVocaloidException::class.java)
      .hasMessageContainingIgnoringCase("must not be blank")

    val anotherRecord = labelsReader.readRecord()!!
    anotherRecord.payload.label.apply {
      this.pvFileName = ""
      this.audioFileName = ""
    }

    assertThatThrownBy { beforeProcessLabelValidator.processRecord(anotherRecord) }
      .isInstanceOf(RuntimeVocaloidException::class.java)
      .hasMessageContainingIgnoringCase("must contain at least one of a PV file or an audio file")

    anotherRecord.payload.label.apply {
      this.pvFileName = "123"
      this.audioFileName = "123"
      this.thumbnailFileName = ""
    }

    assertThatThrownBy { beforeProcessLabelValidator.processRecord(anotherRecord) }
      .isInstanceOf(RuntimeVocaloidException::class.java)
      .hasMessageContainingIgnoringCase("must contain a thumbnail file")

    val thirdRecord = labelsReader.readRecord()!!
    thirdRecord.payload.label.apply {
      this.vocaDbPvId = 0
    }

    assertThatThrownBy { beforeProcessLabelValidator.processRecord(thirdRecord) }
      .isInstanceOf(RuntimeVocaloidException::class.java)
      .hasMessageContainingIgnoringCase("must be greater than 0")
  }
}
