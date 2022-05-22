package mikufan.cx.vvd.downloader.config.downloader

import mikufan.cx.vvd.downloader.config.enablement.EnablementValidator

/**
 * the base configuration that all downloader configuration will use
 *
 * this interface would be an empty interface, but it needs at least one field to make the
 * validation of [EnablementValidator] works
 *
 * @date 2021-06-25
 * @author CX无敌
 */

interface DownloaderBaseConfig {
  /**
   * usually, this is the command line seperated in list of string to execute the external downloader process
   *
   * but for downloader without the need of external process, this can be empty
   */
  val launchCmd: List<String>
}
