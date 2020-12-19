package mikufan.cx.vvd.common.exception;

import java.util.function.Supplier;

/**
 * Basically a {@link Supplier} that can throw exp
 * @author CX无敌
 */
@FunctionalInterface
public interface ThrowableSupplier<T> {

  /**
   * Gets a result, or throw an exception
   *
   * @return a result
   * @throws Exception an exception
   */
  T get() throws Exception;


  /**
   * convert it to the instance of {@link Supplier}
   */
  static <T> Supplier<T> toSupplier(ThrowableSupplier<T> f){
    return () -> {
      try {
        return f.get();
      } catch (Exception ex) {
        return SneakyThrow.theException(ex);
      }
    };
  }

  /**
   * the instance method of {@link ThrowableSupplier#toSupplier(ThrowableSupplier)}
   */
  default Supplier<T> toSupplier(){
    return toSupplier(this);
  }
}
