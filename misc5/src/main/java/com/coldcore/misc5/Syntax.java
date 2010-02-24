/**
 * Date: 07.04.2002
 * Time: 18:01:48
 * Checks syntax based on regulas expressions.
 */
package com.coldcore.misc5;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Syntax {

  public static final String EMAIL_REGEXP = "([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]"
                                          + "{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))"
                                          + "([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)";

  public static final String IP_REGEXP = "([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})";


  private Syntax() {}


  /** Check syntax
   *  @param str String to check
   *  @param regexp Regular expression that defines syntax rules
   */
  public static boolean check(String str, String regexp) {
    Pattern pattern = Pattern.compile(regexp);
    Matcher matcher = pattern.matcher(str);
    return matcher.matches();
  }
}
