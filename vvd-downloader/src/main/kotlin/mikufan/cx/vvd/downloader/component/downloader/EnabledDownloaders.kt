package mikufan.cx.vvd.downloader.component.downloader

import mikufan.cx.vvd.commonkt.vocadb.api.model.PVService
import mikufan.cx.vvd.downloader.component.downloader.base.BaseDownloader
import mikufan.cx.vvd.downloader.config.enablement.Enablement
import org.springframework.stereotype.Component

/**
 * Same as [Enablement], but instead of keeping downloaders' names,
 * this keeps the actual [BaseDownloader] beans
 */
@Component
class EnabledDownloaders(
  allEnabledDownloaders: List<BaseDownloader>,
  enablement: Enablement
) {

  private val pvServiceToDownloadersMap: Map<PVService, List<BaseDownloader>>

  init {
    pvServiceToDownloadersMap = allEnabledDownloaders
      .groupBy { it.targetPvService }
      .mapValues { (pvService, downloaders) ->
        val nameOrder = enablement[pvService].withIndex().associate { Pair(it.value, it.index) }
        downloaders.sortedBy { nameOrder[it.downloaderName] }
      }
  }

  fun getDownloaderForPvService(pvService: PVService): List<BaseDownloader> =
    pvServiceToDownloadersMap[pvService] ?: emptyList()
}
