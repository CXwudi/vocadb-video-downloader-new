package mikufan.cx.vvd.downloader.util

import mikufan.cx.vocadbapiclient.model.PVService
import mikufan.cx.vocadbapiclient.model.PVServices

/**
 * @date 2021-06-18
 * @author CX无敌
 */

fun PVServices.Constant.toPVService() = PVService.valueOf(this.value)

fun PVService.toPVServices() = PVServices.Constant.valueOf(this.value)
