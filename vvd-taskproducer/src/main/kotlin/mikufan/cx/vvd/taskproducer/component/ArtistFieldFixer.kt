package mikufan.cx.vvd.taskproducer.component

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.taskproducer.component.api.VocaDbClient
import mikufan.cx.vvd.commonkt.vocadb.api.model.ArtistCategories
import mikufan.cx.vvd.commonkt.vocadb.api.model.ArtistForSongContract
import mikufan.cx.vvd.commonkt.vocadb.api.model.SongOptionalFields
import mikufan.cx.vvd.taskproducer.model.VSongTask
import mikufan.cx.vvd.taskproducer.util.OrderConstants
import org.apache.commons.lang3.StringUtils
import org.jeasy.batch.core.processor.RecordProcessor
import org.jeasy.batch.core.record.Record
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.*

/**
 * fix any song with an artist string with any of "various" or "unknown"
 * @date 2021-06-01
 * @author CX无敌
 */
@Component
@Order(OrderConstants.ARTIST_FIELD_FIXER_ORDER)
class ArtistFieldFixer(
  private val vocaDbClient: VocaDbClient
) : RecordProcessor<VSongTask, VSongTask> {

  companion object {
    @JvmStatic
    private val log = KInlineLogging.logger()
    const val VARIOUS = "Various"
    const val UNKNOWN = "Unknown"
  }

  override fun processRecord(record: Record<VSongTask>): Record<VSongTask> {
    val song = requireNotNull(record.payload.parameters.songForApiContract) { "VSong is null" }
    var artistStr = requireNotNull(song.artistString) { "artistString is null" }
    val artists = mutableListOf<ArtistForSongContract>()
    log.info { "Check, fix and cleanup song info for ${song.defaultName}" }
    // fix various
    if (artistStr.contains(VARIOUS, true)) {
      val songId = requireNotNull(song.id) { "song id is null" }
      val songWithArtists = vocaDbClient.getSongById(
        songId,
        SongOptionalFields.of(SongOptionalFields.Constant.ARTISTS)
      )
      artists.addAll(songWithArtists.artists)
      val newArtistStr = formProperArtistField(artists)
      log.debug { "replacing artist str '${song.artistString}' with '$newArtistStr'" }
      artistStr = newArtistStr
    }
    if (artistStr.contains(UNKNOWN, true)) {
      val newArtistStr = removeUnknown(artistStr)
      log.debug { "replaing artist str '$artistStr' with '$newArtistStr'" }
      artistStr = newArtistStr
    }

    // replacing back some artist related fields
    val songWithArtists = if (artists.isNotEmpty()) {
      song.copy(artists = artists)
    } else {
      song
    }
    record.payload.parameters.songForApiContract = songWithArtists.copy(artistString = artistStr)

    return record
  }

  internal fun formProperArtistField(artists: List<ArtistForSongContract>): String {
    val vocalist = artists.mapNotNull { artist ->
      val categories = requireNotNull(artist.categories) {
        "artist categories are null for vocalist candidate ${artist.name ?: "UNKNOWN"}"
      }
      if (!categories.enums.contains(ArtistCategories.Constant.VOCALIST)) {
        return@mapNotNull null
      }
      requireNotNull(artist.name) { "artist name is null for vocalist category" }
    }
    val producers = artists.mapNotNull { artist ->
      val categories = requireNotNull(artist.categories) {
        "artist categories are null for producer candidate ${artist.name ?: "UNKNOWN"}"
      }
      if (!StringUtils.containsAny(categories.toString(), "Producer", "Circle")) {
        return@mapNotNull null
      }
      requireNotNull(artist.name) { "artist name is null for producer category" }
    }
    // joinToString default use ", " as separator
    return "${producers.joinToString()} feat. ${vocalist.joinToString()}"
  }

  internal fun removeUnknown(artistStr: String): String {
    return if (artistStr.lowercase().contains("${UNKNOWN.lowercase()} producer")) {
      artistStr
    } else {
      artistStr
        .replace(UNKNOWN.lowercase(Locale.getDefault()), "")
        .replace(UNKNOWN.uppercase(Locale.getDefault()), "")
        .replace(UNKNOWN, "")
        .replace(" ()", "")
        .replace("()", "")
    }
  }
}
