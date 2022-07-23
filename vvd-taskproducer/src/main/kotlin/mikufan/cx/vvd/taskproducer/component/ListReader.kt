package mikufan.cx.vvd.taskproducer.component

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vocadbapiclient.api.SongListApi
import mikufan.cx.vocadbapiclient.model.SongOptionalFields
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.taskproducer.config.IOConfig
import mikufan.cx.vvd.taskproducer.config.SystemConfig
import mikufan.cx.vvd.taskproducer.model.Parameters
import mikufan.cx.vvd.taskproducer.model.VSongTask
import org.jeasy.batch.core.reader.RecordReader
import org.jeasy.batch.core.record.GenericRecord
import org.jeasy.batch.core.record.Header
import org.jeasy.batch.core.record.Record
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import java.time.LocalDateTime
import java.util.*
import kotlin.math.max
import mikufan.cx.vocadbapiclient.model.SongForApiContract as VSong

/**
 * @date 2021-05-29
 * @author CX无敌
 */
@Component
@Validated
class ListReader(
  songListApi: SongListApi,
  ioConfig: IOConfig,
  systemConfig: SystemConfig
) : RecordReader<VSongTask> {
  private val listId = ioConfig.inputListId
  private val pageSize = systemConfig.apiPageSize

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
          val partialFindResult = readSongList(
            listId,
            startIdx,
            pageSize,
            SongOptionalFields(
              SongOptionalFields.Constant.ALBUMS,
              SongOptionalFields.Constant.PVS
            )
          )
          val partialList = partialFindResult.items!!
          log.debug { "read ${partialList.size} new songs" }
          var lastCount = 0
          // if api call returns empty result, then return null
          partialList.forEach {
            queue.add(it.song!!)
            lastCount = max(requireNotNull(it.order) { "order is null" }, lastCount) // order should always have
            // number in it
          }
          val totalCount = requireNotNull(partialFindResult.totalCount) {
            "this should not be null as we set getTotalCount = true, $partialFindResult"
          }
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
    return if (itr.hasNext()) {
      // we don't really need header, but we just want to make it not-null
      val header = Header(++currentRecordNumber, "In-Memory Iterator", LocalDateTime.now())
      val song = itr.next()
      log.info { "Read ${song.defaultName}" }
      GenericRecord(
        header,
        VSongTask(
          // there are two places to store order because we need the order info to be saved in json file
          VSongLabel.builder().order(currentRecordNumber).build(), // will add label filename once everything is done
          Parameters(song)
        )
      )
    } else {
      null
    }
  }
}

private val log = KInlineLogging.logger()
