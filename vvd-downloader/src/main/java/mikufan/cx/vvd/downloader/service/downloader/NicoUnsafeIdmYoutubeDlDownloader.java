package mikufan.cx.vvd.downloader.service.downloader;

import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.YoutubeDLRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.ProcessUtil;
import mikufan.cx.vvd.downloader.config.downloader.NicoUnsafeIdmYoutubeDlConfig;
import org.apache.commons.lang3.mutable.MutableObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

  @Override
  public String getName() {
    return "Niconico Video Downloader by IDM and youtube-dl (unsafe)";
  }

  @Override
  public DownloadStatus download(String url, Path dir, String fileName) throws InterruptedException {
    try {
      //1. download thumbnail using youtube-dl
      var downloadThumbnailStatus = downloadThumbnail(url, dir, fileName);
      if (!downloadThumbnailStatus.isSucceed()){
        return downloadThumbnailStatus;
      }
      //2. get the real url
      var realUrlHolder = new MutableObject<String>(null);
      var getUrlStatus = getUrl(url, realUrlHolder);
      if (!getUrlStatus.isSucceed()){
        return getUrlStatus;
      }
      //3. thing to be done before calling IDM
      deleteIfExist(dir, fileName);
      terminateExistingIdmProcess();
      //4. call IDM to download PV
      return realDownload(realUrlHolder.getValue(), dir, fileName);
    } catch (YoutubeDLException e) {
      if (e.getCause() instanceof InterruptedException){
        throw (InterruptedException) e.getCause();
      } else {
        log.error("YoutubeDLException in download method", e);
        return DownloadStatus.failure(e.getMessage());
      }
    } catch (IOException e) {
      log.error("IOException in download method", e);
      return DownloadStatus.failure(e.getMessage());
    }
  }

  private DownloadStatus downloadThumbnail(String url, Path dir, String fileName) throws YoutubeDLException {
    log.debug("First, download the thumbnail");
    var youtubeDlRequest = new YoutubeDLRequest(
        url,
        dir.toAbsolutePath().toString(),
        config.getYoutubeDlPath().toAbsolutePath().toString());

    var baseFileName = fileName.substring(0, fileName.lastIndexOf('.'));
    youtubeDlRequest
        .setOptions(config.getYoutubeDlOptions())
        .setOption("-o", baseFileName)
        .setOption("--skip-download")
        .setOption("--write-thumbnail");

    var youtubeDlResponse = YoutubeDL.execute(youtubeDlRequest);

    if (youtubeDlResponse.isSuccess() && Files.exists(dir.resolve(baseFileName + ".jpg"))){
      return DownloadStatus.success();
    } else {
      return DownloadStatus.failure(
          String.format("Can not find the downloaded thumbnail or download fails, see error message below%n%s",
              youtubeDlResponse.getErr()));
    }
  }

  /**
   * execute youtube-dl to retrive the real url
   * @param url the base url of a PV
   * @param realUrlHolder a mutable holder for getting the real url
   * @return successful download status if youtube-dl return code is 0
   */
  private DownloadStatus getUrl(String url, MutableObject<String> realUrlHolder) throws YoutubeDLException {
    log.debug("Then, get the video url");
    var youtubeDlRequest = new YoutubeDLRequest(url, null, config.getYoutubeDlPath().toAbsolutePath().toString());
    youtubeDlRequest
        .setOptions(config.getYoutubeDlOptions())
        .setOption("--get-url", null);

    var youtubeDlResponse = YoutubeDL.execute(youtubeDlRequest);
    if (youtubeDlResponse.isSuccess()){
      var realUrl = youtubeDlResponse.getOut();
      log.debug("Url get✔: {}", realUrl);
      realUrlHolder.setValue(realUrl);
      return DownloadStatus.success();
    } else {
      var str = String.format("Fail to extract download url for pv %s", url);
      log.error(str);
      return DownloadStatus.failure(str);
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

  private DownloadStatus realDownload(String url, Path dir, String fileName) throws InterruptedException, IOException {
    var idmPb = new ProcessBuilder(
        config.getIdmPath().toAbsolutePath().toString(),
        "/d", url,
        "/p", dir.toAbsolutePath().toString(),
        "/f", fileName,
        //no question and quite after download
        "/n", "/q");
    log.debug("Then, start downloading using IDM with commands {}", idmPb.command());
    var isFinished = ProcessUtil.runProcess(idmPb.start(), 2, TimeUnit.MINUTES, log::info, log::debug);
    if (!isFinished){
      var str = String.format("Failed to download %s using IDM in 2 minutes", fileName);
      log.error(str);
      return DownloadStatus.failure(str);
    } else if (Files.notExists(dir.resolve(fileName))){
      return DownloadStatus.failure(String.format("Can not find the downloaded file %s", fileName));
    } else {
      return DownloadStatus.success();
    }
  }
}
