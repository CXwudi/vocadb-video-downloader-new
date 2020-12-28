package mikufan.cx.vvd.common.threading;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author CX无敌
 * @date 2020-12-27
 */
public interface ThreadUtil {

  /**
   * create a {@link ThreadPoolExecutor} for running max number of all threads concurrently
   * @param maxThreadCount thread count
   * @param threadBaseName thread base name
   * @return {@link ThreadPoolExecutor}
   */
  static ThreadPoolExecutor getFixedThreadPoolExecutor(int maxThreadCount, String threadBaseName){
    return new ThreadPoolExecutor(maxThreadCount, maxThreadCount,
        1000, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(0),
        ThreadFactoryVocaloid.withBaseName(threadBaseName));
  }

  /**
   * create a {@link ThreadPoolExecutor} for holding max number of threads,
   * but at most {@code poolSize} threads run concurrently
   *
   * @param poolSize at most how many threads run concurrently
   * @param maxThreadCount max thread count
   * @param threadBaseName thread base name
   * @return {@link ThreadPoolExecutor}
   */
  static ThreadPoolExecutor getFixedThreadPoolExecutor(int poolSize, int maxThreadCount, String threadBaseName){
    return new ThreadPoolExecutor(poolSize, poolSize,
        1000, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(maxThreadCount - poolSize),
        ThreadFactoryVocaloid.withBaseName(threadBaseName));
  }
}
