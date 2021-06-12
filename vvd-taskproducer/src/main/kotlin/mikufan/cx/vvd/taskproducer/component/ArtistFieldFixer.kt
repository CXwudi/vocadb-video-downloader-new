package mikufan.cx.vvd.taskproducer.component

import mikufan.cx.vocadbapiclient.api.SongApi
import mikufan.cx.vocadbapiclient.model.ArtistCategories
import mikufan.cx.vocadbapiclient.model.ArtistForSongContract
import mikufan.cx.vocadbapiclient.model.SongOptionalFields
import mikufan.cx.vvd.commonkt.exception.orThrowVocaloidExp
import mikufan.cx.vvd.taskproducer.model.VSongTask
import mu.KotlinLogging
import org.apache.commons.lang3.StringUtils
import org.jeasy.batch.core.processor.RecordProcessor
import org.jeasy.batch.core.record.Record
import org.springframework.stereotype.Component
import java.util.*

/**
 * fix any song with an artist string with any of "various" or "unknown"
 * @date 2021-06-01
 * @author CX无敌
 */
@Component
class ArtistFieldFixer(
  private val songApi: SongApi
) : RecordProcessor<VSongTask, VSongTask> {

  companion object {
    @JvmStatic
    private val log = KotlinLogging.logger {}
    const val VARIOUS = "Various"
    const val UNKNOWN = "Unknown"
  }

  override fun processRecord(record: Record<VSongTask>): Record<VSongTask> {
    val song = record.payload.parameters.songForApiContract.orThrowVocaloidExp("VSong is null")
    var artistStr = song.artistString!!
    val artists = mutableListOf<ArtistForSongContract>()
    // fix various
    if (artistStr.contains(VARIOUS, true)) {
      val songWithArtists = songApi.apiSongsIdGet(
        song.id, SongOptionalFields(SongOptionalFields.Constant.ARTISTS), null)
      artists.addAll(songWithArtists.artists
        .orThrowVocaloidExp("newly called ${songWithArtists.name} has a null artists list"))
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
    if (artists.isNotEmpty()) {
      song.artists = artists
    }
    song.artistString = artistStr

    return record
  }

  internal fun formProperArtistField(artists: List<ArtistForSongContract>): String {
    val vocalist = artists
      .filter { it.categories!!.enums
        .contains(ArtistCategories.Constant.VOCALIST) }
      .map { it.name!! }
      .toList()
    val producers = artists
      .filter { StringUtils.containsAny(it.categories!!.toString(), "Producer", "Circle") }
      .map { it.name!! }
      .toList()
    // joinToString default use ", " as separator
    return "${producers.joinToString()} feat. ${vocalist.joinToString()}"
  }

  internal fun removeUnknown(artistStr: String): String {
    return artistStr
      .replace(UNKNOWN.lowercase(Locale.getDefault()), "")
      .replace(UNKNOWN.uppercase(Locale.getDefault()), "")
      .replace(UNKNOWN, "")
      .replace(" ()", "")
      .replace("()", "")
  }
}
