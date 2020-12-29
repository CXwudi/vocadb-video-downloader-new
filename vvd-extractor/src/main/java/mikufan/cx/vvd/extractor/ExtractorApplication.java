package mikufan.cx.vvd.extractor;

import mikufan.cx.vvd.extractor.service.MainService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * @author CX无敌
 * @date 2020-12-28
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class ExtractorApplication {
  public static void main(String[] args) {
    var applicationContext = SpringApplication.run(ExtractorApplication.class, args);
    applicationContext.getBean(MainService.class).run();
    System.exit(SpringApplication.exit(applicationContext));
  }
}
