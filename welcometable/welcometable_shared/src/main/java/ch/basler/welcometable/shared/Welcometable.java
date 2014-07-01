package ch.basler.welcometable.shared;

public class Welcometable {
  public static final String PM_WELCOME = unique("welcomeId");
  public static final String ATT_VIEW_TEXT = "viewText_";
  public static final String CMD_PUSH = unique("push");
  public static final String CMD_POLL = unique("poll");
  public static final String CMD_SYNC = unique("sync");

  private static String unique(String key) {
    return "Welcometable" + "." + key;
  }

  public static final int DISPLAYROWS = 5;
}
