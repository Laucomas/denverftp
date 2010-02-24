/**
 * Date: Dec 15, 2001
 * Time: 7:10:33 PM
 * Operations with string.
 * This class performs substring replacement, substring extraction between two substrings and other
 * string operations. It also supports ignoreCase methods, that makes search and replacement in
 * string more complete (Note: when using ignoreCase method content does not goes to lowecase, search
 * will take place if lowercased content, but substrings will be returned from original content).
 */
package com.coldcore.misc5;

import java.util.ArrayList;

public class StringReaper {

  private String content = null; //Original content
  private String searchContent = null; //Search will be performed upon this content (original.toLowercase() for ignoreCase methods)


  public StringReaper() {}


  public StringReaper(String content) {
    setContent(content);
  }


  /** Returns modified content (if some modifications were made) */
  public String getContent() {
    return this.content;
  }


  /** Sets new content */
  public void setContent(String content) {
    this.content = content;
    searchContent = content;
  }


  /** Replaces oldString with newString.
   * <p>Note:<p>
   * If newString is empty or null, oldString will be removed.<br>
   * If oldString is empty or null, then nothing will be replaced.
   */
  public void replace(String oldString, String newString) {
    if (oldString == null || oldString.equals("")) return;

    if (content != null) {
      StringBuffer sb = new StringBuffer();
      int cursor = 0;
      int index;
      int oldLength = oldString.length();
      //Do we need to replace or remove ?
      boolean addNew = true;
      if (newString == null || newString.equals("")) addNew = false; //Simply remove

      do {
        //Find string that needs to be replaced/removed in "search content"
        index = searchContent.indexOf(oldString, cursor);
        if (index != -1) {
          //Found ! Now get substring from cursor position (can be last found occurance of this string) and
          //current index in original contentand append it to StringBuffer
          String str = content.substring(cursor, index);
          sb.append(str);
          //If we need to replace it with other string then append StringBuffer with new string
          if (addNew) sb.append(newString);
          //Move cursor to new position (to the end of replaced/removed string)
          cursor = index + oldLength;
        }
      } while (index != -1);

      try { //Add end of original content
        String str = content.substring(cursor, content.length());
        sb.append(str);
      } catch (Exception e) {}

      //Set new "original content" and update "serach content"
      content = sb.toString();
      searchContent = content;
    }
  }


  /** Same as replace() method only ignoreCase */
  public void replaceIgnoreCase(String oldString, String newString) {
    if (content == null) return;
    //Lowercase "search content" and oldString and call "replace" method
    searchContent = content.toLowerCase();
    if (oldString != null) oldString = oldString.toLowerCase();
    replace(oldString, newString);
  }


  /** Exctract values from content.
   * Syntax is the same as in getValues() method.
   */
  private String[] extractValues(String from, String till) {
    if (content != null) {
      ArrayList<String> a = new ArrayList<String>();
      int length = content.length();
      int cursor = 0;
      int index = 0;
      int end;
      int fromLength = 0;
      int tillLength = 0;
      if (from != null) fromLength = from.length();
      if (till != null) tillLength = till.length();

      do {
        //Move index forward till next occurance of "from" in "search content"
        if (fromLength != 0) index = searchContent.indexOf(from, cursor);
        else index = cursor; //No "from" defined
        //Find an occurance of "till" in "search content" after index (next occurance of "from", or after cursor if no "from" were defined)
        if (tillLength != 0) end = searchContent.indexOf(till,  index + fromLength);
        else end = length; //No "till" defined

        if (fromLength == 0) {
          //No "from" were defined
          if (end == -1) end = length;
          //Set new cursor position (next "till" will be searched begining from this cursor position)
          cursor = end + tillLength;
        } else cursor = end; //Set new cursor position (next "from" will be searched begining from this cursor position)

        if (index != -1 && end != -1) {
          //"from" and "till" were found ! Now to take substring that lay between them in "original content" and add it to vector
          String str = content.substring(index + fromLength, end);
          a.add(str);
        } else cursor = length; //No more "from" or "till"
      } while (cursor < length); //Till there is no more "from" or "till"
      //Convert vector with gained values into String[]
      return a.toArray(new String[a.size()]);
    }
    return null;
  }


  /** Gets all values between <code>from</code> and <code>till</code> with
   * ignored case (may return null).
   * <p>
   * Note:
   * <p>
   * If <code>from</code> is <code>null or ""</code> and <code>till</code>
   * has a value then method returns all strings that begin and end
   * with <code>till</code> including begining and end of content.
   * <p>
   * If <code>till</code> is <code>null or ""</code> and <code>from</code>
   * has a value then methos return content from first
   *  found <code>from</code>.
   * <p>
   * If both parametrs are <code>null or ""</code> content will be returned.
   */
  public String[] getValues(String from, String till) {
    if (content != null) {
      //Lowercase "search content", "from" and "till" and call "extractValues" method
      searchContent = content.toLowerCase();
      if (from != null) from = from.toLowerCase();
      if (till != null) till = till.toLowerCase();
      String[] values = extractValues(from, till);
      //Restore search content from lowercase
      searchContent = content;
      return values;
    }
    return null;
  }


  /** Removes text between <code>from</code> and <code>till</code>
   * included or excluded. Case is ignored.<br>
   * Syntax is the same as for getValues() method
   * NOTE When replacing from "AAcccAAnnnAAgggAA" from "AA" till "AA", "ccc", "nnn" and "ggg" will be replaced too,
   * this method deletes everything that starts and ends with "AA" and does not cares about position because
   * it uses getValues method, to avoid this do not use same values in "from" and "till", use " AA" and "AA ".
   */
  public void remove(String from, String till, boolean include) {
    if (content == null) return;

    //Get all values
    String[] values = getValues(from, till);
    if (values == null) return;

    if (from == null) {
      from = "";
      if (till != null) content += till;
      searchContent = content;
    }

    if (till == null) till = "";

    //Replace them
    for (String value : values) {
      String str = from + value + till;

      if (include) replace(str, null);
      else replace(str, from + till);
      searchContent = content;
    }

    //Restore content
    searchContent = content;
  }


  /** Count words
   * @param word Word to count
   * @return Amount
   */
  public int countWords(String word) {
    if (content == null) return 0;

    int count  = 0;
    int cursor = 0;
    int i;
    while ((i = content.indexOf(word, cursor)) != -1) {
      cursor = i + word.length();
      count++;
    }

    return count;
  }
}
