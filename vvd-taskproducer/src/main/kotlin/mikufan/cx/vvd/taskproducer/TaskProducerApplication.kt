package mikufan.cx.vvd.taskproducer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

/**
 * @author CX无敌
 * @date 2021-02-12
 */

@SpringBootApplication
@ConfigurationPropertiesScan
class TaskProducerApplication

fun main(args: Array<String>) {
  runApplication<TaskProducerApplication>(*args)
}
