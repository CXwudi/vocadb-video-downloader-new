package mikufan.cx.vvd.commonkt.exception

import mikufan.cx.vvd.common.exception.RuntimeVocaloidException

/**
 * This function throws a custom [RuntimeVocaloidException].
 *
 * @param message The detail message string of the exception.
 * @param cause The cause of the exception. A null value is permitted, and indicates that the cause is nonexistent or unknown.
 * @throws RuntimeVocaloidException when called.
 */
fun throwVocaloidException(message: String, cause: Exception?): Nothing = throw RuntimeVocaloidException(message, cause)