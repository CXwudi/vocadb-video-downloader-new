package mikufan.cx.vvd.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.exception.ThrowableConsumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author CX无敌
 * @date 2020-12-27
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class ProcessUtil {

  public static int runShortProcess(
      Process process,
      ThrowableConsumer<String> stdoutHandler,
      ThrowableConsumer<String> stderrHandler) throws InterruptedException {
    var inputStream = process.getInputStream();
    var errorStream = process.getErrorStream();
    var executorService =
        new ThreadPoolExecutor(2, 2,
            1000, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(2),
            r -> new Thread(r, "subp-output"));

    executorService.execute(toStreamProcessingRunnable(inputStream, stdoutHandler));
    executorService.execute(toStreamProcessingRunnable(errorStream, stderrHandler));

    var returnCode = process.waitFor();
    executorService.shutdown();
    var isTerminated = executorService.awaitTermination(1, TimeUnit.HOURS);
    if (!isTerminated){
      log.error("Short process's threads didn't end in 1 hour");
    }
    return returnCode;
  }

  public static boolean runProcess(
      Process process,
      long timeout,
      TimeUnit unit,
      ThrowableConsumer<String> stdoutHandler,
      ThrowableConsumer<String> stderrHandler) throws InterruptedException {
    var inputStream = process.getInputStream();
    var errorStream = process.getErrorStream();
    var executorService =
        new ThreadPoolExecutor(2, 2,
            1000, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(2),
            r -> new Thread(r, "subp-output"));

    executorService.execute(toStreamProcessingRunnable(inputStream, stdoutHandler));
    executorService.execute(toStreamProcessingRunnable(errorStream, stderrHandler));

    var isFinished = process.waitFor(timeout, unit);
    executorService.shutdown();
    var isTerminated = executorService.awaitTermination(timeout, unit);
    if (!isTerminated || !isFinished){
      log.warn("The process timeout for {} {}", timeout, unit);
    }
    return isFinished;
  }

  private static Runnable toStreamProcessingRunnable(InputStream inputStream, ThrowableConsumer<String> stringConsumer){
    return () -> {
      try (var output = new BufferedReader(new InputStreamReader(inputStream))) {
        for (var outputLine = output.readLine(); (outputLine = output.readLine()) != null; ) {
          stringConsumer.toConsumer().accept(outputLine);
        }
      } catch (IOException e) {
        log.error("IOException happened", e);
      }
    };
  }
}
