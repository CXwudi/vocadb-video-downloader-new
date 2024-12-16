package mikufan.cx.vvd.extractor.component.tagger.impl

import mikufan.cx.executil.runCmd
import mikufan.cx.executil.sync
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.extractor.component.tagger.base.BaseAudioTagger
import mikufan.cx.vvd.extractor.component.util.MediaFormatChecker
import mikufan.cx.vvd.extractor.config.EnvironmentConfig
import mikufan.cx.vvd.extractor.config.IOConfig
import mikufan.cx.vvd.extractor.config.ProcessConfig
import mikufan.cx.vvd.extractor.model.VSongTask
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path
import java.time.format.DateTimeFormatter
import java.util.concurrent.ThreadPoolExecutor
import kotlin.io.path.deleteExisting
import kotlin.io.path.div

/**
 *
 *
 * @author CXwudi with love to Miku
 * 2024-12-15
 */
@Component
class MkaAudioTagger(
  ioConfig: IOConfig,
  environmentConfig: EnvironmentConfig,
  private val processConfig: ProcessConfig,
  @Qualifier("taggerThreadPool") private val threadPool: ThreadPoolExecutor,
  private val mediainfoChecker: MediaFormatChecker,
) : BaseAudioTagger() {
  private val mkvpropeditLaunchCmd = environmentConfig.mkvpropeditLaunchCmd
  private val inputDirectory = ioConfig.inputDirectory

  override val name: String = "Mka Audio Tagger"

  override fun tryTag(audioFile: Path, allInfo: VSongTask) {
    // 1. build the xml file
    val xmlContent = buildXmlContent(allInfo)
    val xmlFile = writeXmlFile(xmlContent)
    // 2. run the command tagger
    runTagCommand(audioFile, xmlFile)
    // 3. delete the xml file and build the command for embedding the thumbnail
    xmlFile.deleteExisting()
    val thumbnailFile = inputDirectory / allInfo.label.thumbnailFileName
    // 4. identity the mimetype of the thumbnail file
    val imageType = mediainfoChecker.checkImageType(thumbnailFile)
    // 4. run the command embedding the thumbnail
    runEmbedCommand(audioFile, thumbnailFile, imageType)
  }

  private fun writeXmlFile(xmlContent: String): Path {
    val tempFile = Files.createTempFile("mka-tags-", ".xml")
    Files.writeString(tempFile, xmlContent)
    log.info { "Created temp xml file $tempFile" }
    return tempFile
  }

  private fun runTagCommand(audioFile: Path, xmlFile: Path) {
    val command = buildList {
      addAll(mkvpropeditLaunchCmd)
      add(audioFile.toString())
      add("--tags")
      add("all:$xmlFile")
    }
    log.info { "Running mkvpropedit to tag $audioFile: ${command.joinToString(" ", "`", "`")}" }
    runCommand(command)
  }

  private fun runEmbedCommand(audioFile: Path, thumbnailFile: Path, imageType: String) {
    val command = buildList {
      addAll(mkvpropeditLaunchCmd)
      add(audioFile.toString())
      add("--attachment-name")
      add("cover.$imageType")
      add("--attachment-mime-type")
      add("image/$imageType")
      add("--attachment-description")
      add("cover image")
      add("--add-attachment")
      add(thumbnailFile.toString())
    }
    log.info { "Running mkvpropedit to embed $thumbnailFile: ${command.joinToString(" ", "`", "`")}" }
    runCommand(command)
  }

  private fun runCommand(command: List<String>) {
    val process = runCmd(command)
    process.sync(processConfig.timeout, processConfig.unit, threadPool) {
      onStdOutEachLine {
        if (it.isNotBlank()) {
          log.info { it }
        }
      }
      onStdErrEachLine {
        if (it.isNotBlank()) {
          log.debug { it }
        }
      }
    }
    process.exitValue().let {
      if (it != 0) {
        throw IllegalStateException("Command failed with exit code $it")
      }
    }
  }


  private fun buildXmlContent(allInfo: VSongTask): String {
    val songInfo = requireNotNull(allInfo.parameters.songForApiContract) { "songForApiContract must not be null" }
    val label = allInfo.label
    val songName = songInfo.defaultName
    log.info { "Building xml content for $songName" }
    val artistsString = requireNotNull(songInfo.artistString) { "artist string is null" }
    val producers = artistsString.split("feat.")[0].trim()
    val pvId = label.vocaDbPvId
    val dateString = songInfo.publishDate.format(DateTimeFormatter.ISO_DATE)

    val vocaDbId = songInfo.id
    val downloaderName = label.downloaderName
    val extractorName = requireNotNull(allInfo.parameters.chosenAudioExtractor) { "null audio extractor for $songName? " }
      .map { it.name } // get the name of the audio extractor
      .orElse("No Extractor") // if the optional is null, it means the audio itself is there, not from extraction

    // Find the PV info
    val pvInfo = songInfo.pvs.find { it.id == pvId }
    val pvUrl = pvInfo?.url ?: "No PV URL"

    // Build album info if present
    val albumInfo = if (songInfo.albums.isNotEmpty()) {
      val albumNames = songInfo.albums.joinToString(", ") { it.name }
      "Albums [$albumNames]"
    } else {
      null
    }

    return """
        <?xml version="1.0" encoding="UTF-8"?>
        <Tags>
          <Tag>
            <Targets>
              <TargetTypeValue>50</TargetTypeValue>
            </Targets>
            <Simple>
              <Name>GENRE</Name>
              <String>VOCALOID</String>
            </Simple>
            ${if (albumInfo != null) """
            <Simple>
              <Name>INCLUDED BY</Name>
              <String>$albumInfo</String>
            </Simple>
            """ else ""}
          </Tag>
          <Tag>
            <Targets>
              <TargetTypeValue>30</TargetTypeValue>
            </Targets>
            <Simple>
              <Name>TITLE</Name>
              <String>$songName</String>
            </Simple>
            <Simple>
              <Name>ARTIST</Name>
              <String>$artistsString</String>
            </Simple>
            <Simple>
              <Name>DATE_RECORDED</Name>
              <String>$dateString</String>
            </Simple>
            <Simple>
              <Name>COMMENT</Name>
              <String>All rights belong to $producers</String>
            </Simple>
            <Simple>
              <Name>DOWNLOADED BY</Name>
              <String>$downloaderName</String>
            </Simple>
            <Simple>
              <Name>PV URL</Name>
              <String>$pvUrl</String>
            </Simple>
            <Simple>
              <Name>EXTRACTED BY</Name>
              <String>$extractorName</String>
            </Simple>
            <Simple>
              <Name>TAGS EDITED BY</Name>
              <String>$name</String>
            </Simple>
            <Simple>
              <Name>TAGS PROVIDED BY</Name>
              <String>VocaDB (https://vocadb.net/S/$vocaDbId)</String>
            </Simple>
            <Simple>
              <Name>MADE BY</Name>
              <String>CXwudi's vocadb-video-downloader-new (https://github.com/CXwudi/vocadb-video-downloader-new)</String>
            </Simple>
          </Tag>
        </Tags>
      """.trimIndent()
  }
}


private val log = KInlineLogging.logger()
