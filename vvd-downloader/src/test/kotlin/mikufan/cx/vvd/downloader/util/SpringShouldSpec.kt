package mikufan.cx.vvd.downloader.util

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension

/**
 * @date 2021-08-30
 * @author CX无敌
 */
abstract class SpringShouldSpec(body: ShouldSpec.() -> Unit = {}) : ShouldSpec(body) {
  override fun extensions() = listOf(SpringExtension)
}
