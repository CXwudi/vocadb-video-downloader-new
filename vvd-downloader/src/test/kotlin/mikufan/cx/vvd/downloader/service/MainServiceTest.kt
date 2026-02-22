package mikufan.cx.vvd.downloader.service

import mikufan.cx.vvd.downloader.util.SpringBootTestWithTestProfile
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertDoesNotThrow

@SpringBootTestWithTestProfile(
  customProperties = [
    "io.input-directory=src/test/resources/2021年V家精品-tasks-test",
    "config.preference.try-next-pv-service-on-fail=true",
    "config.preference.max-retry-count=0", // disable retry in test
    "config.downloader.NicoNicoDouga.youtube-dl.launch-cmd=yt-dlp",
    "config.downloader.Youtube.youtube-dl.launch-cmd=yt-dlp",
    "config.downloader.Bilibili.youtube-dl.launch-cmd=yt-dlp",
  ]
)
class MainServiceTest(
  private val mainService: MainService
) {
  @Disabled("requires proper env")
  @Test
  fun runInProperEnv() {
    assertDoesNotThrow { mainService.run() }
  }
}
