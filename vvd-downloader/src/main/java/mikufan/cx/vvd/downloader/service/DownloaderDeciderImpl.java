package mikufan.cx.vvd.downloader.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException;
import mikufan.cx.vvd.downloader.config.EnablementConfig;
import mikufan.cx.vvd.downloader.service.downloader.*;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Service;

/**
 * @author CX无敌
 * @date 2020-12-20
 */
@Service @Slf4j
@RequiredArgsConstructor
public class DownloaderDeciderImpl implements DownloaderDecider, BeanFactoryAware {

  private static final String NICO_PURE_YOUTUBE_DL = "nico-pure-youtube-dl";
  private static final String NICO_UNSAFE_IDM_YOUTUBE_DL = "nico-unsafe-idm-youtube-dl";
  private static final String YOUTUBE = "youtube";
  private static final String BILIBILI = "bilibili";

  private BeanFactory beanFactory;

  private final EnablementConfig enablementConfig;

  /**
   * without guessing the extension again in download service
   * @param pvService
   * @return
   */
  @Override
  public PvDownloader getSuitableDownloader(String pvService) {
    var enablementForService = enablementConfig.getEnablementForService(pvService).orElseThrow(
        () -> new RuntimeVocaloidException(String.format("Can not find an enablement for %s", pvService))
    );
    switch (enablementForService){
      case NICO_PURE_YOUTUBE_DL: return beanFactory.getBean(NicoPureYoutubeDlDownloader.class);
      case NICO_UNSAFE_IDM_YOUTUBE_DL: return beanFactory.getBean(NicoUnsafeIdmYoutubeDlDownloader.class);
      case YOUTUBE: return beanFactory.getBean(YoutubeYoutubeDlDownloader.class);
      case BILIBILI: return beanFactory.getBean(BilibiliYoutubeDlDownloader.class);
      default: throw new RuntimeVocaloidException(
          String.format("Can not find a downloader for pv service %s with enablement %s", pvService, enablementForService)
      );
    }
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }
}
