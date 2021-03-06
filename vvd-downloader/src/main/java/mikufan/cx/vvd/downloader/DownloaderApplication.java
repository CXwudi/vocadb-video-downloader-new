package mikufan.cx.vvd.downloader;

import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.downloader.service.MainService;
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
    applicationContext.getBean(MainService.class).run();
    System.exit(SpringApplication.exit(applicationContext));
  }
}
