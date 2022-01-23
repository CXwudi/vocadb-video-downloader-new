package mikufan.cx.vvd.extractor.service;

import mikufan.cx.vvd.extractor.util.TestEnvHolder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author CX无敌
 * @date 2020-12-29
 */
@SpringBootTest(properties = {
    "io.input-directory=D:/11134/Videos/Vocaloid Coding POC/Project VD test/2019年V家新曲 sample PVs 2"
})
@Disabled
class IOServiceImplTest extends TestEnvHolder {
  @Autowired
  private IOService ioService;

  @Test
  void testRead(){
    var allSongsToBeExtracted = ioService.getAllSongsToBeExtracted();
  }
}