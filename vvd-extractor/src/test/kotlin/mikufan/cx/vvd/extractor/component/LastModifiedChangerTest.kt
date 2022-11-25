package mikufan.cx.vvd.extractor.component

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.date.shouldBeAfter
import java.time.Clock
import java.time.Instant
import kotlin.io.path.getLastModifiedTime

class LastModifiedChangerTest : ShouldSpec({
  context("LastModifiedChanger") {
    should("be able to change last modified time to future!!") {
      val lastModifiedChangerCore = LastModifiedChangerCore { Instant.now(Clock.systemDefaultZone()).plusSeconds(500) }
      val tempFile = kotlin.io.path.createTempFile("future-", ".temp.txt")
      lastModifiedChangerCore.changeLastModifiedTimeByOrder(tempFile, 1)
      tempFile.getLastModifiedTime().toInstant() shouldBeAfter Instant.now()
    }
  }
})
