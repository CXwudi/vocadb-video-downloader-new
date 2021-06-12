package mikufan.cx.vvd.common.label;

/**
 * Used for app context object to do validation of fields incrementally
 * @author CX无敌
 * @date 2020-12-31
 */
public interface ValidationPhase {
  interface One {}
  interface Two extends One {}
  interface Three extends Two{}
  interface Four extends Three {}
  interface Five extends Four {}

}
