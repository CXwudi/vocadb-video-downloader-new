package mikufan.cx.vvd.common.exception;

import java.util.List;
import java.util.SplittableRandom;

/**
 * Use this exception to indicate the business logic error
 *
 * @author CX无敌
 * @date 2020-12-20
 */
public class RuntimeVocaloidException extends RuntimeException {

  public RuntimeVocaloidException(String message) {
    super(MessagePostFixMaker.constructMessage(message));
  }

  public RuntimeVocaloidException(String message, Throwable cause) {
    super(MessagePostFixMaker.constructMessage(message), cause);
  }

  private static class MessagePostFixMaker {
    private static final SplittableRandom RANDOM = new SplittableRandom();
    private static final List<String> PLAINTS = List.of("Oh No! ", "どうしよう、", "やめて、", "\ud83d\ude2d, ", "\ud83d\ude30, ");
    private static final List<String> SENTENCES = List.of(
        "初音ミク & Miku fan CX are crying due to this error",
        "初音ミク & Miku fan CX can't continue the dream",
        "System is quiting and 初音ミク & Miku fan CX have to leave",
        "\n皆に忘れ去られた時\n" + "心らしきものが消えて\n" +
            "暴走の果てに見える\n" + "終わる世界...　「VOCALOID」\n" +
            "           ---初音ミクの消失",
        "\n「アリガトウ・・・ソシテ・・・サヨナラ・・・」\n" +
            "  ---深刻なエラーが発生しました---\n" +
            "  ---深刻なエラー...Tszzzzzzzzzz\n" +
            "           ---初音ミクの消失"
    );

    private static String constructMessage(String message) {
      return String.format("%s%s. %s",
          PLAINTS.get(RANDOM.nextInt(PLAINTS.size())),
          message,
          SENTENCES.get(RANDOM.nextInt(SENTENCES.size()))
      );
    }
  }
}
