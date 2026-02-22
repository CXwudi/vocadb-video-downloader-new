package mikufan.cx.vvd.extractor.component

import mikufan.cx.vvd.commonkt.vocadb.api.model.PVContract
import mikufan.cx.vvd.commonkt.vocadb.api.model.PVService
import mikufan.cx.vvd.commonkt.vocadb.api.model.SongForApiContract
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.io.path.deleteIfExists

class FinalRenamerTest {

  private val fileRenamer = FinalRenamerCore()
  private val vocadbPvId = 39
  private val testSong = SongForApiContract(
    defaultName = "Test Song with /",
    artistString = "Producer feat. Vocalist",
    pvs = listOf(
      PVContract(
        id = vocadbPvId,
        pvId = "sm123456",
        service = PVService.NICONICODOUGA
      )
    )
  )

  @Test
  fun properFileName() {
    assertThat(fileRenamer.generateProperName(testSong, vocadbPvId).toString())
      .isEqualTo("【Vocalist】Test Song with -【Producer】[NicoNicoDouga sm123456]")
  }

  @Test
  fun fileRenaming() {
    val tempFile = kotlin.io.path.createTempFile("test-", ".temp.txt")
    val path = fileRenamer.doProperRename(tempFile, testSong, vocadbPvId)
    assertThat(path.fileName.toString())
      .isEqualTo("【Vocalist】Test Song with -【Producer】[NicoNicoDouga sm123456].txt")
    path.deleteIfExists()
  }
}
