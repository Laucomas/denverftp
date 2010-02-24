/**
 * Date: 07.04.2002
 * Time: 18:01:48
 * Operations on HTML format.
 */

package com.coldcore.misc5;

import java.util.LinkedHashMap;
import java.util.Map;

public class Html {

  private Html() {}


  /** Convert HTML to plain text
   *  @param htmlText HTML to convert
   *  @param repl Replacements map
   *  @return Plain text
   */
  public static String htmlToPlain(String htmlText, Map<String,String> repl) {
    if (htmlText == null) return null;

    Map<String,String> rep = new LinkedHashMap<String, String>();
    rep.put("&nbsp;", " ");
    rep.put("&amp;", "&");
    rep.put("&quot;", "\"");
    rep.put("\n", "");
    rep.put("\r", "");
    rep.put("<p>", "\n");
    rep.put("<br>", "\n");
    if (repl != null) rep.putAll(repl);

    StringReaper reaper = new StringReaper(htmlText);
    for (String s : rep.keySet())
      reaper.replaceIgnoreCase(s, rep.get(s));
    reaper.remove("<", ">", true);

    return reaper.getContent();
  }


  /** Convenience method
   * @see com.coldcore.misc5.Html#htmlToPlain(String, java.util.Map<java.lang.String,java.lang.String>)
   */
  public static String htmlToPlain(String htmlText) {
    return htmlToPlain(htmlText, null);
  }


  /** Convert plain text into HTML format, so it can be included into HTML
   *  @param plainText Plain text to convert
   *  @param repl Replacements map
   *  @return Text in HTML format
   */
  public static String convertPlain(String plainText, Map<String,String> repl) {
    if (plainText == null) return null;

    Map<String,String> rep = new LinkedHashMap<String, String>();
    rep.put("&", "&amp;");
    rep.put("<", "&lt;");
    rep.put(">", "&gt;");
    rep.put("\"", "&quot;");
    rep.put("\n", "<br>");
    rep.put("\r", "");
    if (repl != null) rep.putAll(repl);

    //Replace all symbols that will not be correctly displayed in browser.
    StringReaper reaper = new StringReaper(plainText);
    for (String s : rep.keySet())
      reaper.replace(s, rep.get(s));
    String htmlText = reaper.getContent();

    //We will replace all spaces in a row with "&nbsp;", but not the first one.
    StringBuffer sb = new StringBuffer();
    boolean replace = false;
    char[] ca = htmlText.toCharArray();
    for (int loop = 0; loop < ca.length; loop++)
      if (ca[loop] != ' ') {
        //Not space, append it and there is no need to replace the next space.
        sb.append(ca[loop]);
        replace = false;
      } else {
        //Space, replace it if the previous char was also space.
        if (replace) sb.append("&nbsp;");
        else {
          sb.append(ca[loop]);
          replace = true;
        }
      }

    return sb.toString();
  }


  /** Convenience method
   * @see com.coldcore.misc5.Html#convertPlain(String, java.util.Map<java.lang.String,java.lang.String>)
   */
  public static String convertPlain(String plainText) {
    return convertPlain(plainText, null);
  }


  /** Encode all symbols in a plain text with HTML encoding (&#1077;&#1088; etc.), so it can be included into HTML
   *  @param plainText Plain text to convert
   *  @return Encoded string
   */
  public static String encodePlain(String plainText) {
    return toHtmlEnc(plainText, true);
  }


  /** Convert string to HTML encoding (&#1077;&#1088; etc.)
   *  @param str String to convert
   *  @param encodeAll If TRUE then all characters will be encoded
   *  @return Converted string
   */
  private static String toHtmlEnc(String str, boolean encodeAll) {
    if (str == null) return null;

    StringBuffer sb = new StringBuffer();
    char[] ca = str.toCharArray();
    boolean enc = true;
    for (char c : ca) {
      if (c == '&') enc = false; //Do not encode &lt; &amp; &#1088; etc.
      sb.append(enc || encodeAll ? "&#"+(int)c+";" : ""+c);
      if (c == ';') enc = true;
    }

    return sb.toString();
  }


  /** Reverse HTML encoding (&#1077;&#1088; etc.)
   *  @param str Encoded string.
   *  @return Reversed text.
   */
  public static String reverseHtmlEnc(String str) {
    if (str == null) return null;

    StringReaper reaper = new StringReaper(str);
    String[] values = reaper.getValues("&#", ";");
    for (String value : values)
      try {
        int i = Integer.parseInt(str);
        reaper.replace("&#"+value+";", ""+(char)i); //Looks like HTML encoded, remove surrounding "&#" and ";".
      } catch (Exception e) {} //Skip this one - not encoded

    return reaper.getContent();
  }


  /** Encode HTML content (not tags) with HTML encoding (&#1077;&#1088; etc.)
   * @param html HTML
   * @return Encoded HTML
   */
  public static String encodeHtml(String html) {
    html = html.trim();
    String str = html;
    if (!html.startsWith("<")) str = "<>"+str;
    if (!html.endsWith(">")) str = str+"<>";

    StringReaper sr = new StringReaper(str);
    String[] values = sr.getValues(">", "<");
    for (String value : values)
      sr.replace(">"+value+"<", ">"+toHtmlEnc(value, false)+"<");

    str = sr.getContent();
    if (!html.startsWith("<")) str = str.substring(2);
    if (!html.endsWith(">")) str = str.substring(0, str.length()-2);

    return str;
  }
}