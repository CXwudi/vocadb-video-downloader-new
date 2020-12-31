package mikufan.cx.vvd.extractor.service.extractor;

import mikufan.cx.vvd.extractor.label.ExtractStatus;

import java.nio.file.Path;

/**
 * @author CX无敌
 * @date 2020-12-29
 */
public interface AudioExtractor {

  /**
   * name of the extractor
   * @return name of the extractor
   */
  String getName();

  /**
   * Download the pv and the thumbnail from specific url to specific directory with specific file name <br/>
   *
   * <h3>Implementation Note:</h3>
   * The function must not throw exception except for {@link InterruptedException}.
   * Instead, implementation must always return a {@link ExtractStatus}.
   * If the result status is not success, that is {@link ExtractStatus#isSucceed()} return {@code false},
   * a description or reason should be set in {@link ExtractStatus#description}.
   * Such description can be {@link Throwable#getMessage()}.<br/>
   * The implementation must respect that if a extraction is success,
   * the given directory and fileName in the parameters should able to
   * correctly locate the audio file that is extracted by this method.
   * Failing to do so will cause the program unable to find the audio extracted. <br/>
   * Also, it's not the goal to implement retry mechanism inside downloader class. <br/>
   * Instead, the service that using this extractor should handle retry mechanism. <br/>
   * When {@link InterruptedException} is thrown, the JVM should stop ASAP. <br/>
   * A mixin interface should not override this method. <br/>
   * 
   * @param pv the input pv file
   * @param directory the output directory
   * @param fileName the name of the output file in output directory
   * @return extraction status
   * @throws InterruptedException if ctrl+c happened
   */
  ExtractStatus extractAudio(Path pv, Path directory, String fileName) throws InterruptedException;
}
