package mikufan.cx.vvd.downloader.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException;
import mikufan.cx.vvd.common.vocadb.model.PV;
import mikufan.cx.vvd.downloader.config.preference.PvPreferenceConfig;
import mikufan.cx.vvd.downloader.util.PvTypeComparator;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author CX无敌
 * @date 2020-12-20
 */
@Service @Slf4j @Validated
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PvDeciderImpl implements PvDecider {

  PvPreferenceConfig pvPreferenceConfig;

  @Override
  public PV choosePreferredPv(List<PV> pvs) {
    var serviceToPvs = pvs.stream().collect(Collectors.groupingBy(PV::getService));
    for (var preferredService: pvPreferenceConfig.getPreference()){
      var pvList = serviceToPvs.get(preferredService);
      if (!CollectionUtils.isEmpty(pvList)){
        pvList.sort(PvTypeComparator.INSTANCE);
        return pvList.get(0);
      }
    }
    throw new RuntimeVocaloidException("Should have at least one PV downloadable, " +
        "otherwise, either pv-task-producer fail to filter the unavailable song, " +
        "or the pv-preference setting is wrong");
  }
}
