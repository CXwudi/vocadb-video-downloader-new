package mikufan.cx.vvd.downloader.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException;
import mikufan.cx.vvd.common.vocadb.model.PV;
import mikufan.cx.vvd.downloader.util.PvTypeComparator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author CX无敌
 * @date 2020-12-20
 */
@Service @Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PvDeciderImpl implements PvDecider {

  List<String> preference;

  public PvDeciderImpl(@Value("${downloader.config.pv-preference}") List<String> preference) {
    this.preference = preference;
  }

  @Override
  public PV choosePreferredPv(List<PV> pvs) {
    var serviceToPvs = pvs.stream().collect(Collectors.groupingBy(PV::getService));
    for (var preferredService: preference){
      var pvList = serviceToPvs.get(preferredService);
      if (!CollectionUtils.isEmpty(pvList)){
        pvList.sort(PvTypeComparator.INSTANCE);
        return pvList.get(0);
      }
    }
    throw new RuntimeVocaloidException("Should have at least one PV downloadable, otherwise, pv-task-producer has a bug");
  }
}
