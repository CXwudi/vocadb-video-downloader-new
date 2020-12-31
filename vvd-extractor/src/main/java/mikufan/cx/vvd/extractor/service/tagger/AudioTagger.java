package mikufan.cx.vvd.extractor.service.tagger;

import mikufan.cx.vvd.extractor.label.ExtractStatus;

import java.nio.file.Path;

/**
 * @author CX无敌
 * @date 2020-12-30
 */
public interface AudioTagger {


  /**
   * name of the extractor
   * @return name of the extractor
   */
  String getName();

  //FIXME: we need the new song resource pojo
  ExtractStatus handleTagging(Path audioFile, Path infoFile, Path thumbnailFile) throws InterruptedException;
}
