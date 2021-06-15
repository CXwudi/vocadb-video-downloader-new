package mikufan.cx.vvd.downloader.config.downloader;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Charles Chen 101035684
 * @date 2020-12-18
 */
public interface ConfigHelper {

  static Map<String, String> fixConfigWithDash(Map<String, String> rawConfig){
    return rawConfig.entrySet().stream().collect(
        Collectors.toMap(entry -> "-" + entry.getKey(), Map.Entry::getValue));
  }
}
