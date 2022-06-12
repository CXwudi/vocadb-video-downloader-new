package mikufan.cx.vvd.extractor.service

import kotlinx.coroutines.runBlocking
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.extractor.component.LabelsReader
import org.springframework.stereotype.Service

/**
 * @date 2022-06-11
 * @author CX无敌
 */
@Service
class MainService(
  private val labelsReader: LabelsReader
) : Runnable {
  
  override fun run() = runBlocking {

  }
}

private val log = KInlineLogging.logger()
