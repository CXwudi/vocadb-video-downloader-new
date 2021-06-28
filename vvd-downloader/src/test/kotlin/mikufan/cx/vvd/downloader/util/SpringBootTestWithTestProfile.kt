package mikufan.cx.vvd.downloader.util

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.annotation.AliasFor
import org.springframework.test.context.ActiveProfiles


/**
 * @date 2021-06-27
 * @author CX无敌
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@SpringBootTest
@ActiveProfiles
annotation class SpringBootTestWithTestProfile(
  @get:AliasFor(annotation = ActiveProfiles::class, attribute = "profiles")
  val activeProfiles: Array<String> = ["test"],
  @get:AliasFor(annotation = SpringBootTest::class, attribute = "properties")
  val customProperties: Array<String> = []
)