package mikufan.cx.vvd.commonkt.batch

import org.jeasy.batch.core.reader.RecordReader
import org.jeasy.batch.core.record.Record

/**
 * Transform a [RecordReader] into an [Iterator], this iterator is not thread safe
 * @date 2021-12-10
 * @author CX无敌
 */
class RecordReaderIterator<P> internal constructor(
  private val reader: RecordReader<P>
) : Iterator<Record<P>> {
  var next: Record<P>? = null

  override fun hasNext(): Boolean {
    next = if (next != null) next else reader.readRecord()
    return next != null
  }

  override fun next(): Record<P> {
    hasNext()
    val toReturn = next!!
    next = null
    return toReturn
  }
}

fun <P> RecordReader<P>.toIterator() = RecordReaderIterator(this)
