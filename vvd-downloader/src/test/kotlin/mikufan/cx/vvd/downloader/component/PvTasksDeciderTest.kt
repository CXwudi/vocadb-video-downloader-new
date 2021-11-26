package mikufan.cx.vvd.downloader.component

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vocadbapiclient.model.PVService
import mikufan.cx.vocadbapiclient.model.PVType
import mikufan.cx.vvd.downloader.model.VSongTask
import mikufan.cx.vvd.downloader.util.SpringBootDirtyTestWithTestProfile
import mikufan.cx.vvd.downloader.util.SpringShouldSpec
import org.jeasy.batch.core.record.Record

private val log = KInlineLogging.logger()

abstract class PvTasksDeciderBaseTest(
  private val labelsReader: LabelsReader,
  private val songInfoLoader: SongInfoLoader,
  private val pvTasksDecider: PvTasksDecider,
  body: ShouldSpec.(List<Record<VSongTask>>) -> Unit = {}
) : SpringShouldSpec({
  val list = buildList {
    lateinit var label: Record<VSongTask>
    while (labelsReader.readRecord()?.also { label = it } != null) {
      val thisLabel = label
      add(thisLabel)
    }
  }
  val testList = list.map {
    pvTasksDecider.processRecord(songInfoLoader.processRecord(it))
  }

  // log.debug { "List = ${testList.joinToString("\n===START OF LIST===\n", "\n", "\n===END OF LIST===")}" }

  body(testList)
})

@SpringBootDirtyTestWithTestProfile(
  customProperties = [
    "io.input-directory=src/test/resources/Hatsune Miku Magical Mirai 2021-label",
    "config.preference.pv-preference=Bilibili, NicoNicoDouga, Youtube",
    "config.preference.try-next-pv-service-on-fail=true",
    "config.preference.try-all-original-pvs-before-reprinted-pvs=false"
  ]
)
class PvTasksDeciderServiceAndTypeOnlySortingTest(
  labelsReader: LabelsReader,
  songInfoLoader: SongInfoLoader,
  pvTasksDecider: PvTasksDecider,
) : PvTasksDeciderBaseTest(labelsReader, songInfoLoader, pvTasksDecider, { testList ->

  context("sorting in defined pv services order") {
    for (task in testList) {
      val pvs = task.payload.parameters.pvCandidates!!
      val songName = task.payload.parameters.songForApiContract?.defaultName ?: "Null name"
      should("correctly sort pv services for $songName") {
        pvs
          .dropWhile { it.service == PVService.BILIBILI }
          .dropWhile { it.service == PVService.NICONICODOUGA }
          .dropWhile { it.service == PVService.YOUTUBE }
          .size shouldBe 0
      }

      should("correctly sort pv types for each pv services for $songName") {
        for (pvService in listOf(PVService.BILIBILI, PVService.NICONICODOUGA, PVService.YOUTUBE)) {
          val pvsForService = pvs.filter { it.service == pvService }
          pvsForService
            .dropWhile { it.pvType == PVType.ORIGINAL }
            .dropWhile { it.pvType == PVType.OTHER }
            .dropWhile { it.pvType == PVType.REPRINT }
            .size shouldBe 0
        }
      }
    }
  }
})
