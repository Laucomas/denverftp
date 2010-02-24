package com.coldcore.coloradoftp.plugin.intellipack.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities.
 */
public class Util {

  /** Check syntax
   *  @param str String to check
   *  @param regexp Regular expression that defines syntax rules
   */
  public static boolean checkRegExp(String str, String regexp) {
    Pattern pattern = Pattern.compile(regexp);
    Matcher matcher = pattern.matcher(str);
    return matcher.matches();
  }

}
