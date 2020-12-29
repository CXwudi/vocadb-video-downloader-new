package mikufan.cx.vvd.extractor.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException;
import mikufan.cx.vvd.extractor.service.extractor.AudioExtractor;
import mikufan.cx.vvd.extractor.service.extractor.M4aAudioExtractor;
import mikufan.cx.vvd.extractor.service.extractor.OpusAudioExtractor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

/**
 * @author CX无敌
 * @date 2020-12-29
 */
@Service @Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExtractorDeciderImpl implements ExtractorDecider, BeanFactoryAware {

  BeanFactory beanFactory;

  @Override
  public AudioExtractor chooseExtractor(Path videoFile) {
    var fileName = videoFile.getFileName().toString();
    var extension = fileName.substring(fileName.lastIndexOf('.'));
    switch (extension){
      case ".mp4":
      case ".flv": return beanFactory.getBean(M4aAudioExtractor.class);
      case ".mkv": return beanFactory.getBean(OpusAudioExtractor.class);
      default: throw new RuntimeVocaloidException(String.format("can not get the audio extractor for file type %s", extension));
    }
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }
}
