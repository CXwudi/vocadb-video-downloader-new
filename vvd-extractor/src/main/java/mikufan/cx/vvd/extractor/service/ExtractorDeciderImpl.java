package mikufan.cx.vvd.extractor.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException;
import mikufan.cx.vvd.extractor.label.ExtractContext;
import mikufan.cx.vvd.extractor.label.ValidationPhase;
import mikufan.cx.vvd.extractor.service.extractor.NiconicoM4aAudioExtractor;
import mikufan.cx.vvd.extractor.service.extractor.YoutubeOpusAudioExtractor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.groups.Default;

/**
 * @author CX无敌
 * @date 2020-12-29
 */
@Service @Slf4j @Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExtractorDeciderImpl implements ExtractorDecider, BeanFactoryAware {

  BeanFactory beanFactory;

  @Override
  public ExtractContext getProperExtractorAndAudioExt(
      @Validated({ValidationPhase.One.class, Default.class}) ExtractContext extractContext) {
    var fileName = extractContext.getSongResource().getPvFileName();
    var extension = fileName.substring(fileName.lastIndexOf('.'));
    switch (extension){
      case ".mp4":
      case ".flv": return ExtractContext.builder()
          .audioExtractor(beanFactory.getBean(NiconicoM4aAudioExtractor.class))
          .audioExtension(".m4a")
          .build();
      case ".mkv": return ExtractContext.builder()
          .audioExtractor(beanFactory.getBean(YoutubeOpusAudioExtractor.class))
          // just encapsulate opus in ogg so that wangyiyun can support it
          .audioExtension(".ogg")
          .build();
      default: throw new RuntimeVocaloidException(String.format("can not get the audio extractor for file type %s", extension));
    }
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }
}
