package mikufan.cx.vvd.downloader.component

import io.kotest.matchers.shouldNotBe
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.downloader.util.SpringBootTestWithTestProfile
import mikufan.cx.vvd.downloader.util.SpringShouldSpec
import org.apache.tika.Tika
import org.junit.jupiter.api.Disabled
import kotlin.io.path.Path

/**
 * @date 2022-01-23
 * @author CX无敌
 */
@SpringBootTestWithTestProfile(
  customProperties = ["logging.level.mikufan.cx.vvd.downloader.component=trace"]
)
@Disabled("has too many local file directory that not working in CI")
class MimeTypeDetectionPoc : SpringShouldSpec({
  val tika = Tika()
  log.trace { "trace is enabled" }

  val detectType2 = fun(pathStr: String) {
    val path = Path(pathStr)

    val type = tika.detect(path.toFile())
    log.debug { "type = \n$type" }
    type.toString() shouldNotBe "application/octet-stream"
  }

  xcontext("about file type") {
    should("detect basic mkv video") {
      detectType2("D:\\coding-workspace\\Vocaloid Coding POC\\Test Videos\\To be continued... - 初音ミク - 青屋夏生 - video.mkv")
    }

    should("detect basic mkv video without extension") {
      detectType2("D:\\coding-workspace\\Vocaloid Coding POC\\Test Videos\\To be continued... - 初音ミク - 青屋夏生 - video - Copy")
    }

    should("detect basic mkv video with wrong extension") {
      detectType2("D:\\coding-workspace\\Vocaloid Coding POC\\Test Videos\\To be continued... - 初音ミク - 青屋夏生 - video - Copy - Copy.mp4")
    }

    should("detect webp image") {
      detectType2("D:\\coding-workspace\\Vocaloid Coding POC\\Test Videos\\To be continued... - 初音ミク - 青屋夏生 - pic.webp")
    }

    should("detect ts video file") { // only this one is wrongly detected
      detectType2("D:\\coding-workspace\\Vocaloid Coding POC\\Test Videos\\Fexxxn feat. 音街ウナ「ラブ・バルーン」MV.ts")
    }

    should("detect mp4") {
      detectType2("D:\\coding-workspace\\Vocaloid Coding POC\\Test Videos\\【初音ミクNT】 ふたつカゲボウシ【Notzan ACT】-video.mp4")
    }

    should("detect mp4 without extension") {
      detectType2("D:\\coding-workspace\\Vocaloid Coding POC\\Test Videos\\【初音ミクNT】 ふたつカゲボウシ【Notzan ACT】-video - Copy")
    }

    should("detect webm") {
      detectType2("D:\\coding-workspace\\Vocaloid Coding POC\\Test Videos\\「Koplo」 YOASOBI - Tracing That Dream -Cover Hatsune Miku- 「TEGRA39 Remix」 [AE48xdHpnUE].webm")
    }

    should("detect webm without extension") {
      detectType2("D:\\coding-workspace\\Vocaloid Coding POC\\Test Videos\\「Koplo」 YOASOBI - Tracing That Dream -Cover Hatsune Miku- 「TEGRA39 Remix」 [AE48xdHpnUE] - Copy")
    }

    should("detect webm with wrong extension") {
      detectType2("D:\\coding-workspace\\Vocaloid Coding POC\\Test Videos\\「Koplo」 YOASOBI - Tracing That Dream -Cover Hatsune Miku- 「TEGRA39 Remix」 [AE48xdHpnUE] - Copy - Copy.mp4")
    }

    should("detect svg file") {
      detectType2("D:\\coding-workspace\\Vocaloid Coding POC\\Test Videos\\github-brands.svg")
    }
  }
})

private val log = KInlineLogging.logger()
