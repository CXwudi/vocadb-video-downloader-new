package mikufan.cx.vvd.extractor.component

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import mikufan.cx.vocadbapiclient.model.PVContract
import mikufan.cx.vocadbapiclient.model.PVService
import mikufan.cx.vocadbapiclient.model.SongForApiContract
import kotlin.io.path.deleteIfExists

class FinalRenamerTest : ShouldSpec({

  val fileRenamer = FinalRenamerCore()
  val vocadbPvId = 39
  val testSong = SongForApiContract().apply {
    defaultName = "Test Song with /"
    artistString = "Producer feat. Vocalist"
    pvs = listOf(
      PVContract().apply {
        id = vocadbPvId
        pvId = "sm123456"
        service = PVService.NICONICODOUGA
      }
    )
  }
  context("proper file name") {
    fileRenamer.generateProperName(testSong, vocadbPvId)
      .toString() shouldBe "【Vocalist】Test Song with -【Producer】[NicoNicoDouga sm123456]"
  }

  context("file renaming") {
    val tempFile = kotlin.io.path.createTempFile("test-", ".temp.txt")
    val path = fileRenamer.doProperRename(tempFile, testSong, vocadbPvId)
    path.fileName.toString() shouldBe "【Vocalist】Test Song with -【Producer】[NicoNicoDouga sm123456].txt"
    path.deleteIfExists()
  }
})
