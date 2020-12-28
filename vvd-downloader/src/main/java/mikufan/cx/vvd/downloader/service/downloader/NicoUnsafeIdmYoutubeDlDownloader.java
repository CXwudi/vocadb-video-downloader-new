package mikufan.cx.vvd.downloader.service.downloader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.YoutubeDLRequest;
import com.sapher.youtubedl.mapper.VideoInfo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.ProcessUtil;
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException;
import mikufan.cx.vvd.downloader.config.downloader.NicoUnsafeIdmYoutubeDlConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author CX无敌
 * @date 2020-12-21
 */
@Service @Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class NicoUnsafeIdmYoutubeDlDownloader implements PvDownloader {

  private static final String IDM_PROCESS_NAME = "IDMan.exe";

  NicoUnsafeIdmYoutubeDlConfig config;

  ObjectMapper objectMapper;

  @Override
  public String getName() {
    return "Niconico Video Downloader by IDM and youtube-dl (unsafe)";
  }

  @Override
  public DownloadStatus downloadPvAndThumbnail(String url, Path dir, String pvFileName, String thumbnailFileName) throws InterruptedException {
    try {
      //1. get video info
      var videoInfoHolder = new MutableObject<VideoInfo>(null);
      var retrieveStatus = getVideoInfo(url, videoInfoHolder);
      if (!retrieveStatus.isSucceed()){
        return retrieveStatus;
      }
      //2. using video info to guide the downloadPvAndThumbnail of thumbnail and pv, using 2 thread
      return downloadByVideoInfo(videoInfoHolder.getValue(), dir, pvFileName, thumbnailFileName);
    } catch (YoutubeDLException e) {
      if (e.getCause() instanceof InterruptedException){
        throw (InterruptedException) e.getCause();
      } else {
        log.error("YoutubeDLException in downloadPvAndThumbnail method", e);
        return DownloadStatus.failure(e.getMessage());
      }
    } catch (ExecutionException e) {
      log.error("ExecutionException in downloadPvAndThumbnail method", e);
      return DownloadStatus.failure(e.getCause().getMessage());
    }
  }

  private DownloadStatus getVideoInfo(String url, MutableObject<VideoInfo> videoInfoHolder) throws YoutubeDLException {
    // Build request
    log.info("First, retrieving pv info for {}", url);
    var request = new YoutubeDLRequest(url, null, config.getYoutubeDlPath().toAbsolutePath().toString());
    request
        .setOptions(config.getYoutubeDlOptions())
        .setOption("--dump-json")
        .setOption("--no-playlist");

    var response = YoutubeDL.execute(request, null, log::debug);

    // Parse result
    VideoInfo videoInfo;

    try {
      videoInfo = objectMapper.readValue(response.getOut(), VideoInfo.class);
    } catch (IOException e) {
      throw new YoutubeDLException("Unable to parse video information: " + e.getMessage(), e);
    }

    if (!response.isSuccess()){
      return DownloadStatus.failure(String.format("Fail to get video info for %s", url));
    } else if (videoInfo.formats.isEmpty()){
      return DownloadStatus.failure(String.format("Can not find any downloadable url for %s", url));
    } else if (StringUtils.isBlank(videoInfo.thumbnail)){
      return DownloadStatus.failure(String.format("Can not find thumbnail url for %s", url));
    } else {
      log.info("Url get✔: {}", getRealUrlFromVideoInfo(videoInfo));
      log.info("Thumbnail get✔: {}", videoInfo.thumbnail);
      videoInfoHolder.setValue(videoInfo);
      return DownloadStatus.success();
    }
  }

  /**
   * Under so many formats of downloadable urls, get the one indicated by videoInfo.format
   */
  private String getRealUrlFromVideoInfo(VideoInfo videoInfo){
    return videoInfo.formats.stream()
        .filter(f -> f.format.equals(videoInfo.format))
        .findFirst().orElseThrow(
            () -> new RuntimeVocaloidException(
                String.format("The indicated video format for %s doesn't match the all available formats", videoInfo.format))
        ).url;
  }

  private DownloadStatus downloadByVideoInfo(VideoInfo videoInfo, Path dir, String fileName, String thumbnailFileName) throws ExecutionException, InterruptedException {
    var pvUrl = getRealUrlFromVideoInfo(videoInfo);
    var thumbnailUrl = videoInfo.thumbnail;
    var executorService =
        new ThreadPoolExecutor(2, 2,
            1000, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(2),
            r -> new Thread(r, "nico-unsafe-idm-youtube-dl"));

    //2.1. downloadPvAndThumbnail thumbnail
    var thumbnailDownloadFuture = executorService.submit(
        () -> downloadThumbnail(thumbnailUrl, dir, thumbnailFileName)
    );
    //2.2. downloadPvAndThumbnail pv
    var pvDownloadFuture = executorService.submit(
        () -> {
          deleteIfExist(dir, fileName);
          terminateExistingIdmProcess();
          return downloadPv(pvUrl, dir, fileName);
    });
    return DownloadStatus.merge(thumbnailDownloadFuture.get(), pvDownloadFuture.get());
  }


  private DownloadStatus downloadThumbnail(String url, Path dir, String fileName) throws IOException {
    log.debug("Then, download the thumbnail");
    var fullOutputPath = dir.resolve(fileName);

    FileUtils.copyURLToFile(new URL(url), fullOutputPath.toFile(), 30000, 3000000);

    if (Files.exists(fullOutputPath)){
      return DownloadStatus.success();
    } else {
      return DownloadStatus.failure(
          String.format("Can not find the downloaded thumbnail or downloadPvAndThumbnail fails, see error message below%n%s",
              "some str"));
    }
  }


  private void deleteIfExist(Path dir, String fileName) throws IOException {
    var targetFile = dir.resolve(fileName);
    if (Files.exists(targetFile)){
      Files.delete(targetFile);
      log.warn("Deleted existing file {}", targetFile);
    }
  }

  private void terminateExistingIdmProcess() throws InterruptedException, IOException {
    var taskKillPb = new ProcessBuilder("taskkill", "/F", "/FI", String.format("\"imagename eq %s\"", IDM_PROCESS_NAME));
    log.debug("Try killing existing IDM process with '{}'", taskKillPb.command());
    ProcessUtil.runShortProcess(taskKillPb.start(), log::info, log::debug);
  }

  /**
   * downloadPvAndThumbnail the pv using
   * @param url the real url for downloading the pv
   * @param dir output dir
   * @param fileName file name
   * @return success downloadPvAndThumbnail status if it is done within 2 minute limit time (due to niconico heartbeat issue)
   */
  private DownloadStatus downloadPv(String url, Path dir, String fileName) throws InterruptedException, IOException {
    var idmPb = new ProcessBuilder(
        config.getIdmPath().toAbsolutePath().toString(),
        "/d", url,
        "/p", dir.toAbsolutePath().toString(),
        "/f", fileName,
        //no question and quite after downloadPvAndThumbnail
        "/n", "/q");
    log.debug("Then, start downloading using IDM with commands {}", idmPb.command());
    var isFinished = ProcessUtil.runProcess(idmPb.start(), 2, TimeUnit.MINUTES, log::info, log::debug);
    if (!isFinished){
      var str = String.format("Failed to downloadPvAndThumbnail %s using IDM in 2 minutes", fileName);
      log.error(str);
      return DownloadStatus.failure(str);
    } else if (Files.notExists(dir.resolve(fileName))){
      return DownloadStatus.failure(String.format("Can not find the downloaded file %s", fileName));
    } else {
      return DownloadStatus.success();
    }
  }
}
