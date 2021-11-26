package mikufan.cx.vvd.downloader.util

import mikufan.cx.vocadbapiclient.model.PVService
import mikufan.cx.vocadbapiclient.model.PVServices

typealias PVServicesEnum = PVServices.Constant

/**
 * @date 2021-06-18
 * @author CX无敌
 */

fun PVServicesEnum.toPVService() = PVService.valueOf(this.value.uppercase())

fun PVService.toPVServicesEnum(): PVServicesEnum = PVServicesEnum.valueOf(this.value.uppercase())
