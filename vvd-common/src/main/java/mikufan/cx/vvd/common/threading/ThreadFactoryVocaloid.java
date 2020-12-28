package mikufan.cx.vvd.common.threading;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link ThreadFactory} for Vocaloid :)
 * @author CX无敌
 * @date 2020-12-27
 */
@RequiredArgsConstructor(staticName = "withBaseName")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ThreadFactoryVocaloid implements ThreadFactory {

  String baseThreadName;

  AtomicInteger counter = new AtomicInteger(0);

  @Override
  public Thread newThread(Runnable r) {
    return new Thread(r, baseThreadName + "-t" + counter.getAndIncrement());
  }
}
