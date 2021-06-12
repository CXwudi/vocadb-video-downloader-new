package mikufan.cx.vvd.commonkt.label

/**
 * To support non-blocking debugging purposes.
 *
 * App can use this class to collect or write all failed processing object into somewhere.
 *
 * so that when long running app is finished, we can get a clear look of all failure we have.
 *
 * @date 2021-06-12
 * @author CX无敌
 */
data class FailedObject<T> @JvmOverloads constructor(
  val obj: T,
  val exception: Exception? = null,
  val reason: String? = null
)
