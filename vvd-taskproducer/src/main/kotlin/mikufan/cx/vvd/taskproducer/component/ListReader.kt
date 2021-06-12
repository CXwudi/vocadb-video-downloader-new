package mikufan.cx.vvd.taskproducer.component

import mikufan.cx.vocadbapiclient.api.SongListApi
import mikufan.cx.vocadbapiclient.model.SongOptionalFields
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.commonkt.exception.orThrowVocaloidExp
import mikufan.cx.vvd.taskproducer.config.IOConfig
import mikufan.cx.vvd.taskproducer.model.Parameters
import mikufan.cx.vvd.taskproducer.model.VSongTask
import mu.KotlinLogging
import org.jeasy.batch.core.reader.RecordReader
import org.jeasy.batch.core.record.GenericRecord
import org.jeasy.batch.core.record.Header
import org.jeasy.batch.core.record.Record
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import java.time.LocalDateTime
import java.util.*
import javax.validation.constraints.Min
import kotlin.math.max
import mikufan.cx.vocadbapiclient.model.SongForApiContract as VSong

/**
 * @date 2021-05-29
 * @author CX无敌
 */
@Component @Validated
class ListReader(
  private val songListApi: SongListApi,
  ioConfig: IOConfig,
  @Min(0) @Value("\${config.api-page-size}") private val pageSize: Int
) : RecordReader<VSongTask> {

  private val listId = ioConfig.inputListId

  private val itr: Iterator<VSong> by lazy {
    object : Iterator<VSong> {
      var hasMore = true
      var startIdx = 0
      val queue: Queue<VSong> = LinkedList()

      override fun hasNext(): Boolean {
        return if (hasMore) {
          true
        } else {
          queue.isNotEmpty()
        }
      }

      override fun next(): VSong {
        if (queue.isEmpty()) { // lazy read the list when need
          log.debug { "start fetching $pageSize songs from index $startIdx" }
          val partialFindResult = readSongList(listId, startIdx, pageSize, SongOptionalFields(
            SongOptionalFields.Constant.ALBUMS,
            SongOptionalFields.Constant.PVS
          ))
          val partialList = partialFindResult.items!!
          log.debug { "read ${partialList.size} new songs" }
          var lastCount = 0
          // if api call returns empty result, then return null
          partialList.forEach {
            queue.add(it.song!!)
            lastCount = max(it.order.orThrowVocaloidExp("order is null"), lastCount) // order should always have
          // number in it
          }
          val totalCount = partialFindResult.totalCount.orThrowVocaloidExp("this should not be null as we set " +
              "getTotalCount = true, $partialFindResult")
          if (lastCount != totalCount) {
            log.debug { "lastCount = $lastCount, totalCount = $totalCount, more songs need to be fetched" }
            hasMore = true
            startIdx = lastCount
          } else {
            log.debug { "lastCount = $lastCount, totalCount = $totalCount, all songs fetched" }
            hasMore = false
          }
        }
        return queue.poll()
      }

      private fun readSongList(listId: Int, start: Int, maximum: Int, optionalFields: SongOptionalFields) =
        songListApi.apiSongListsListIdSongsGet(
          listId, null, null, null, null, null, null, null,
          start, maximum, true, null, null, optionalFields, null
        )
    }
  }

  private var currentRecordNumber: Long = 0

  override fun readRecord(): Record<VSongTask>? {
    val header = Header(++currentRecordNumber, "In-Memory Iterator", LocalDateTime.now())
    return if (itr.hasNext()) {
      val song = itr.next()
      log.info { "start processing ${song.defaultName}" }
      GenericRecord(header, VSongTask(
        VSongLabel.builder().order(currentRecordNumber).build(), // will add label filename once everything is done
        Parameters(song, currentRecordNumber)
      ))
    } else {
      null
    }
  }
}

private val log = KotlinLogging.logger {}
