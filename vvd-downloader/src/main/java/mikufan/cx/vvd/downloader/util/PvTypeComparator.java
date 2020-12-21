package mikufan.cx.vvd.downloader.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import mikufan.cx.vvd.common.vocadb.model.PV;

import java.util.Comparator;

/**
 * @author CX无敌
 * @date 2020-12-20
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PvTypeComparator implements Comparator<PV> {

  public static final PvTypeComparator INSTANCE = new PvTypeComparator();

  private static final String ORIGINAL = "Original";

  @Override
  public int compare(PV pv1, PV pv2) {
    if (pv1.getPvType().equals(pv2.getPvType())){
      return 0;
    } else {
      if (ORIGINAL.equals(pv1.getPvType())){
        return -1;
      } else if (ORIGINAL.equals(pv2.getPvType())){
        return 1;
      } else {
        return 0;
      }
    }
  }
}
