package mikufan.cx.vvd.downloader.service

import io.kotest.assertions.throwables.shouldNotThrow
import mikufan.cx.vvd.downloader.util.SpringBootDirtyTestWithTestProfile
import mikufan.cx.vvd.downloader.util.SpringShouldSpec

@SpringBootDirtyTestWithTestProfile(
  customProperties = [
    "config.preference.try-next-pv-service-on-fail=true",
    "io.input-directory=src/test/resources/2021年V家精品-tasks-test",
    "config.downloader.NicoNicoDouga.youtube-dl.launch-cmd=yt-dlp",
    "config.downloader.Youtube.youtube-dl.launch-cmd=yt-dlp",
    "config.downloader.Bilibili.youtube-dl.launch-cmd=yt-dlp",
  ]
)
class MainServiceTest(
  private val mainService: MainService
) : SpringShouldSpec({
  xshould("run in the proper env") {
    shouldNotThrow<java.lang.Exception> { mainService.run() }
  }
})
