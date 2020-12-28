package mikufan.cx.vvd.common.threading;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author CX无敌
 * @date 2020-12-27
 */
public interface ThreadUtil {

  /**
   * create a {@link ThreadPoolExecutor} for generated max number of threads, and run all threads concurrently
   * @param maxThreadCount thread count
   * @param threadBaseName thread base name
   * @return {@link ThreadPoolExecutor}
   */
  static ThreadPoolExecutor getFixedThreadPoolExecutor(int maxThreadCount, String threadBaseName){
    return new ThreadPoolExecutor(maxThreadCount, maxThreadCount,
        1000, TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(maxThreadCount),
        ThreadFactoryVocaloid.withBaseName(threadBaseName));
  }

  /**
   * create a {@link ThreadPoolExecutor} for generated max number of thread,
   * run at most {@code poolSize} threads concurrently
   *
   * @param poolSize at most how many threads run concurrently
   * @param maxThreadCount max thread count
   * @param threadBaseName thread base name
   * @return {@link ThreadPoolExecutor}
   */
  static ThreadPoolExecutor getFixedThreadPoolExecutor(int poolSize, int maxThreadCount, String threadBaseName){
    return new ThreadPoolExecutor(poolSize, poolSize,
        1000, TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(maxThreadCount - poolSize),
        ThreadFactoryVocaloid.withBaseName(threadBaseName));
  }
}
