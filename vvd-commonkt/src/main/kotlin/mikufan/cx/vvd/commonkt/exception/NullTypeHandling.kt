package mikufan.cx.vvd.commonkt.exception

import mikufan.cx.vvd.common.exception.RuntimeVocaloidException


/**
 * @date 2021-06-03
 * @author CX无敌
 */
/**
 * turn any [Any?] to [Any]
 *
 * either returns itself or throw [RuntimeVocaloidException] if null
 */
inline fun <reified T> T?.requireNotNull(
  exp: Throwable? = null,
  lazyMsg: () -> String = { "Required value is null" }
): T {
  return this ?: throw exp?.let { RuntimeVocaloidException(lazyMsg(), it) } ?: RuntimeVocaloidException(lazyMsg())
}

inline fun <reified T> T?.checkNotNull(
  exp: Throwable? = null,
  lazyMsg: () -> String = { "Required value is null" }
): T {
  return this ?: throw exp?.let { RuntimeVocaloidException(lazyMsg(), it) } ?: RuntimeVocaloidException(lazyMsg())
}