package mikufan.cx.vvd.downloader.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.downloader.service.downloader.PvDownloader;
import org.springframework.beans.BeansException;
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

  private BeanFactory beanFactory;


  @Override
  public PvDownloader getSuitableDownloader(String pvService) {

    return null;
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }
}
