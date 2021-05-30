package mikufan.cx.vvd.extractor.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException;
import mikufan.cx.vvd.common.label.ValidationPhase;
import mikufan.cx.vvd.extractor.model.ExtractContext;
import mikufan.cx.vvd.extractor.service.tagger.NiconicoM4aAudioTagger;
import mikufan.cx.vvd.extractor.service.tagger.YoutubeOpusAudioTagger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * @author CX无敌
 * @date 2020-12-31
 */
@Service @Slf4j @Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaggerDeciderImpl implements TaggerDecider, BeanFactoryAware {

  BeanFactory beanFactory;

  @Override
  public ExtractContext chooseTagger(
      @Validated(ValidationPhase.Three.class) ExtractContext context) {
    var audioFileExtension = context.getAudioExtension();
    var pvService = context.getSongResource().getPvService();
    var returnContextBuilder = ExtractContext.builder();
    if (".ogg".equals(audioFileExtension) && "Youtube".equals(pvService)){
      returnContextBuilder.audioTagger(beanFactory.getBean(YoutubeOpusAudioTagger.class));
    } else if (".m4a".equals(audioFileExtension)){
      returnContextBuilder.audioTagger(beanFactory.getBean(NiconicoM4aAudioTagger.class));
    } else {
      throw new RuntimeVocaloidException(
          String.format("Can not find supported audio tagger for file type %s and pv service %s",
              audioFileExtension, pvService));
    }
    return returnContextBuilder.build();
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }
}
