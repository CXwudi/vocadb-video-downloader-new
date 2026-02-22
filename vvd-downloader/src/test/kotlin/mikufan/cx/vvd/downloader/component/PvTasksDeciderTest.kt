package mikufan.cx.vvd.downloader.component

import mikufan.cx.vvd.commonkt.vocadb.api.model.PVService
import mikufan.cx.vvd.commonkt.vocadb.api.model.PVType
import mikufan.cx.vvd.downloader.model.VSongTask
import mikufan.cx.vvd.downloader.util.SpringBootDirtyTestWithTestProfile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestInstance
import org.jeasy.batch.core.record.Record

abstract class PvTasksDeciderBaseTest(
  private val labelsReader: LabelsReader,
  private val songInfoLoader: SongInfoLoader,
  private val pvTasksDecider: PvTasksDecider
) {
  protected fun buildTestList(): List<Record<VSongTask>> {
    val labels = mutableListOf<Record<VSongTask>>()
    while (true) {
      val record = labelsReader.readRecord() ?: break
      labels.add(record)
    }
    return labels.map { pvTasksDecider.processRecord(songInfoLoader.processRecord(it)) }
  }
}

@SpringBootDirtyTestWithTestProfile(
  customProperties = [
    "io.input-directory=src/test/resources/Hatsune Miku Magical Mirai 2021-label",
    "config.preference.pv-preference=Bilibili, NicoNicoDouga, Youtube",
    "config.preference.try-next-pv-service-on-fail=true",
    "config.preference.try-all-original-pvs-before-reprinted-pvs=false"
  ]
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PvTasksDeciderWithServiceAndTypeSortingOrderTest(
  labelsReader: LabelsReader,
  songInfoLoader: SongInfoLoader,
  pvTasksDecider: PvTasksDecider,
) : PvTasksDeciderBaseTest(labelsReader, songInfoLoader, pvTasksDecider) {

  @TestFactory
  fun sortingTests(): List<DynamicTest> {
    val testList = buildTestList()
    return testList.flatMap { task ->
      val pvs = task.payload.parameters.pvCandidates
      val songName = task.payload.parameters.songForApiContract?.defaultName ?: "Null name"
      assertThat(pvs).isNotNull()
      val pvList = pvs!!

      listOf(
        dynamicTest("correctly sort pv services for $songName") {
          val remainder = pvList
            .dropWhile { it.service == PVService.BILIBILI }
            .dropWhile { it.service == PVService.NICONICODOUGA }
            .dropWhile { it.service == PVService.YOUTUBE }
          assertThat(remainder).isEmpty()
        },
        dynamicTest("correctly sort pv types for each pv services for $songName") {
          for (pvService in listOf(PVService.BILIBILI, PVService.NICONICODOUGA, PVService.YOUTUBE)) {
            val pvsForService = pvList.filter { it.service == pvService }
            val remainder = pvsForService
              .dropWhile { it.pvType == PVType.ORIGINAL }
              .dropWhile { it.pvType == PVType.OTHER }
              .dropWhile { it.pvType == PVType.REPRINT }
            assertThat(remainder).isEmpty()
          }
        }
      )
    }
  }
}

@SpringBootDirtyTestWithTestProfile(
  customProperties = [
    "io.input-directory=src/test/resources/Hatsune Miku Magical Mirai 2021-label",
    "config.preference.pv-preference=Bilibili, NicoNicoDouga, Youtube",
    "config.preference.try-next-pv-service-on-fail=true",
    "config.preference.try-all-original-pvs-before-reprinted-pvs=true"
  ]
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PvTasksDeciderWithTypeAndServiceSortingOrderTest(
  labelsReader: LabelsReader,
  songInfoLoader: SongInfoLoader,
  pvTasksDecider: PvTasksDecider,
) : PvTasksDeciderBaseTest(labelsReader, songInfoLoader, pvTasksDecider) {

  @TestFactory
  fun sortingTests(): List<DynamicTest> {
    val testList = buildTestList()
    return testList.flatMap { task ->
      val pvs = task.payload.parameters.pvCandidates
      val songName = task.payload.parameters.songForApiContract?.defaultName ?: "Null name"
      assertThat(pvs).isNotNull()
      val pvList = pvs!!

      listOf(
        dynamicTest("correctly sort pv types for $songName") {
          val remainder = pvList
            .dropWhile { it.pvType == PVType.ORIGINAL }
            .dropWhile { it.pvType == PVType.OTHER }
            .dropWhile { it.pvType == PVType.REPRINT }
          assertThat(remainder).isEmpty()
        },
        dynamicTest("correctly sort pv services for each pv types for $songName") {
          for (pvType in listOf(PVType.ORIGINAL, PVType.OTHER, PVType.REPRINT)) {
            val pvsForType = pvList.filter { it.pvType == pvType }
            val remainder = pvsForType
              .dropWhile { it.service == PVService.BILIBILI }
              .dropWhile { it.service == PVService.NICONICODOUGA }
              .dropWhile { it.service == PVService.YOUTUBE }
            assertThat(remainder).isEmpty()
          }
        }
      )
    }
  }
}
