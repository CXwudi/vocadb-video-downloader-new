package mikufan.cx.vvd.downloader.component

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.downloader.util.SpringBootTestWithTestProfile
import org.apache.tika.Tika
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

/**
 * @date 2022-01-23
 * @author CX无敌
 */
@SpringBootTestWithTestProfile(
  customProperties = ["logging.level.mikufan.cx.vvd.downloader.component=trace"]
)
@Disabled("has too many local file directory that not working in CI")
class MimeTypeDetectionPoc {
  private val tika = Tika()

  private fun detectType2(pathStr: String) {
    val path = Path(pathStr)

    val type = tika.detect(path.toFile())
    log.debug { "type = \n$type" }
    assertThat(type.toString()).isNotEqualTo("application/octet-stream")
  }

  @Test
  fun detectBasicMkvVideo() {
    detectType2("D:\\coding-workspace\\Vocaloid Coding POC\\Test Videos\\To be continued... - 初音ミク - 青屋夏生 - video.mkv")
  }

  @Test
  fun detectBasicMkvVideoWithoutExtension() {
    detectType2("D:\\coding-workspace\\Vocaloid Coding POC\\Test Videos\\To be continued... - 初音ミク - 青屋夏生 - video - Copy")
  }

  @Test
  fun detectBasicMkvVideoWithWrongExtension() {
    detectType2("D:\\coding-workspace\\Vocaloid Coding POC\\Test Videos\\To be continued... - 初音ミク - 青屋夏生 - video - Copy - Copy.mp4")
  }

  @Test
  fun detectWebpImage() {
    detectType2("D:\\coding-workspace\\Vocaloid Coding POC\\Test Videos\\To be continued... - 初音ミク - 青屋夏生 - pic.webp")
  }

  @Test
  fun detectTsVideoFile() {
    detectType2("D:\\coding-workspace\\Vocaloid Coding POC\\Test Videos\\Fexxxn feat. 音街ウナ「ラブ・バルーン」MV.ts")
  }

  @Test
  fun detectMp4() {
    detectType2("D:\\coding-workspace\\Vocaloid Coding POC\\Test Videos\\【初音ミクNT】 ふたつカゲボウシ【Notzan ACT】-video.mp4")
  }

  @Test
  fun detectMp4WithoutExtension() {
    detectType2("D:\\coding-workspace\\Vocaloid Coding POC\\Test Videos\\【初音ミクNT】 ふたつカゲボウシ【Notzan ACT】-video - Copy")
  }

  @Test
  fun detectWebm() {
    detectType2("D:\\coding-workspace\\Vocaloid Coding POC\\Test Videos\\「Koplo」 YOASOBI - Tracing That Dream -Cover Hatsune Miku- 「TEGRA39 Remix」 [AE48xdHpnUE].webm")
  }

  @Test
  fun detectWebmWithoutExtension() {
    detectType2("D:\\coding-workspace\\Vocaloid Coding POC\\Test Videos\\「Koplo」 YOASOBI - Tracing That Dream -Cover Hatsune Miku- 「TEGRA39 Remix」 [AE48xdHpnUE] - Copy")
  }

  @Test
  fun detectWebmWithWrongExtension() {
    detectType2("D:\\coding-workspace\\Vocaloid Coding POC\\Test Videos\\「Koplo」 YOASOBI - Tracing That Dream -Cover Hatsune Miku- 「TEGRA39 Remix」 [AE48xdHpnUE] - Copy - Copy.mp4")
  }

  @Test
  fun detectSvgFile() {
    detectType2("D:\\coding-workspace\\Vocaloid Coding POC\\Test Videos\\github-brands.svg")
  }
}

private val log = KInlineLogging.logger()
