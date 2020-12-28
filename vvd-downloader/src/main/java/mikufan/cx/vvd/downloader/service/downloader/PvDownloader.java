package mikufan.cx.vvd.downloader.service.downloader;

import java.nio.file.Path;

/**
 * @author CX无敌
 * @date 2020-12-20
 */
public interface PvDownloader {

  /**
   * name of the downloader
   * @return name of the downloader
   */
  String getName();

  /**
   * Download the pv and the thumbnail from specific url to specific directory with specific file name <br/>
   *
   * <h3>Implementation Note:</h3>
   * The function must not throw exception except for {@link InterruptedException}.
   * Instead, implementation must always return a {@link DownloadStatus}.
   * If the result status is not success, that is {@link DownloadStatus#isSucceed()} return {@code false},
   * a description or reason should be set in {@link DownloadStatus#description}.
   * Such description can be {@link Throwable#getMessage()}.<br/>
   * The implementation must respect that if a downloadPvAndThumbnail is success,
   * the given directory and pvFileName and thumbnailFileName in the parameters should able to
   * correctly locate the PV file and the thumbnail file that is downloaded by this method.
   * Failing to do so will cause the program unable to find the pv or thumbnail downloaded. <br/>
   * Also, it's not the goal to implement retry mechanism inside downloader class. <br/>
   * Instead, the service that using this downloader should handle retry mechanism,
   * unless a retry mechanism is necessary for making successful downloadPvAndThumbnail. <br/>
   * When {@link InterruptedException} is thrown, the JVM should stop ASAP. <br/>
   * A mixin interface should not override this method. <br/>
   *
   * @param url the url of where to watch the PV on website
   * @param dir in which directory is the pv file saved
   * @param pvFileName the file of the pv
   * @param thumbnailFileName
   * @return {@link DownloadStatus} indicating success of not, if fail, a reason or a description is set
   * @throws InterruptedException if ctrl+c happens
   */
  DownloadStatus downloadPvAndThumbnail(String url, Path dir, String pvFileName, String thumbnailFileName) throws InterruptedException;
}
