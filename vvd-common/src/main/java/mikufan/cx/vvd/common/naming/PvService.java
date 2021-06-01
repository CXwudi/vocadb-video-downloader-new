package mikufan.cx.vvd.common.naming;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import mikufan.cx.vvd.common.exception.ThrowableFunction;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author CX无敌
 * @date 2020-12-20
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PvService {
  public static final Set<String> ALL_SERVICES;
  public static final Set<String> ALL_SUPPORTED_SERVICES;

  static {
    var fields = PvServiceStr.class.getDeclaredFields();
    ALL_SERVICES = Arrays.stream(fields)
        .map(ThrowableFunction.toFunction(field -> (String)field.get(null)))
        .collect(Collectors.toUnmodifiableSet());

    ALL_SUPPORTED_SERVICES = Set.of(PvServiceStr.NICONICO, PvServiceStr.YOUTUBE, PvServiceStr.BILIBILI);
  }
}
