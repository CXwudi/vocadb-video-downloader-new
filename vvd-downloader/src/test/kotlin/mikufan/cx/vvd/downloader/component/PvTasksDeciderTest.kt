package mikufan.cx.vvd.downloader.component

import mikufan.cx.vvd.downloader.util.SpringBootTestWithTestProfile
import mikufan.cx.vvd.downloader.util.SpringShouldSpec
import org.springframework.test.annotation.DirtiesContext

@SpringBootTestWithTestProfile
@DirtiesContext
class PvTasksDeciderTest : SpringShouldSpec({

  xcontext("PV service & types order sorting") {
    TODO("Add tests for testing the basic sorting")
  }

  context("Only first pv service") {
  }

  context("No reprint pvs") {
  }

  context("Only first & no prints") {
  }

  context("try reprint after fails") {
  }
})
