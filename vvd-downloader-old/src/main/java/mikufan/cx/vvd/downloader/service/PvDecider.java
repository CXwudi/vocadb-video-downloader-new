package mikufan.cx.vvd.downloader.service;

import mikufan.cx.vvd.common.vocadb.model.PV;

import java.util.List;

/**
 * Choose the preferred PV to be download, base on configuration
 * @author CX无敌
 * @date 2020-12-20
 */
public interface PvDecider {

  PV choosePreferredPv(List<PV> pvs);
}
