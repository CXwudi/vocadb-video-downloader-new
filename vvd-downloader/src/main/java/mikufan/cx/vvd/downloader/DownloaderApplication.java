package mikufan.cx.vvd.downloader;

import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.downloader.config.io.ApplicationIO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * @author Charles Chen 101035684
 * @date 2020-12-17
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@Slf4j
public class DownloaderApplication {
  public static void main(String[] args) {
    var applicationContext = SpringApplication.run(DownloaderApplication.class, args);
    var applicationIo = applicationContext.getBean(ApplicationIO.class);
    log.info("applicationIo = {}", applicationIo.getInputDirectory().toAbsolutePath().toString()
        + ", " + applicationIo.getOutputDirectory().toAbsolutePath().toString());
  }
}
