package mikufan.cx.vvd.downloader.config.downloader

import mikufan.cx.vvd.commonkt.vocadb.api.model.PVService
import org.springframework.context.annotation.Condition
import org.springframework.context.annotation.ConditionContext
import org.springframework.core.type.AnnotatedTypeMetadata

/**
 * all downloader condition runs this logic, only difference is the pv service type and downloader name
 * @date 2021-07-18
 * @author CX无敌
 */

sealed interface DownloaderBaseCondition : Condition {
  /**
   * Determine if this downloader should be enabled
   * @param context the condition context
   * @param metadata the metadata of the [class][org.springframework.core.type.AnnotationMetadata]
   * or [method][org.springframework.core.type.MethodMetadata] being checked
   * @return `true` if the condition matches and the component can be registered,
   * or `false` to veto the annotated component's registration
   */
  override fun matches(context: ConditionContext, metadata: AnnotatedTypeMetadata): Boolean {
    val environment = context.environment
    // we are not logging here because this base condition is used on both
    // configuration properties classes and downloader implementation classes.
    // which will end up logging twice

//    if (!res) {
//      log.debug { "Skip loading $downloaderName downloader for $pvServices" }
//    }
    return environment.getProperty("config.preference.pv-preference", "")
      .contains(pvServices.toString()) &&
        environment.getProperty("config.enablement.$pvServices", "")
          .contains(downloaderName)
  }

  val pvServices: PVService
  val downloaderName: String
}
