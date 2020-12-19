package mikufan.cx.vvd.downloader.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author CX无敌
 * @date 2020-12-19
 */
@Service @Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MainRunner implements Runnable{

  IOManager ioManager;

  /**
   * main entry method
   */
  @Override
  public void run() {
    var allSongsToBeDownloadedInOrder = ioManager.getAllSongsToBeDownloadedInOrder();

  }
}
