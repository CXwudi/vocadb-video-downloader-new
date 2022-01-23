package mikufan.cx.vvd.extractor.service.tagger;

import mikufan.cx.vvd.extractor.model.ExtractStatus;

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

  /**
   * do the tagging, this should only modify the audio file, no other file creation or deletion
   * @param audioFile input audio file
   * @param thumbnailFile thumbnail file
   * @param resourceFile resourceFile
   * @param infoFile infoFile
   * @return status
   * @throws InterruptedException if ctrl+c happened
   */
  ExtractStatus handleTagging(
      Path audioFile, Path thumbnailFile,
      Path resourceFile, Path infoFile) throws InterruptedException;
}
