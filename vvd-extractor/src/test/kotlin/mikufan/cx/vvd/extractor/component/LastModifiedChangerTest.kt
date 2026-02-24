package mikufan.cx.vvd.extractor.component

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import kotlin.io.path.getLastModifiedTime

class LastModifiedChangerTest {

  @Test
  fun changeLastModifiedTimeToFuture() {
    val lastModifiedChangerCore = LastModifiedChangerCore { Instant.now(Clock.systemDefaultZone()).plusSeconds(500) }
    val tempFile = kotlin.io.path.createTempFile("future-", ".temp.txt")
    lastModifiedChangerCore.changeLastModifiedTimeByOrder(tempFile, 1)
    assertThat(tempFile.getLastModifiedTime().toInstant()).isAfter(Instant.now())
  }
}
