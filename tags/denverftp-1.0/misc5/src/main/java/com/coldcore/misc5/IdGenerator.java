/**
 * Date: Feb 5, 2002
 * Time: 1:02:43 PM
 * Unic ID generator (numbers and letters)
 * Generates unique IDs that contain numbers, big and slmall letters.
 */
package com.coldcore.misc5;

public class IdGenerator {

  private IdGenerator() {}


  /** Generates an ID of random length.
   *  @param minLength The min length of a generated ID.
   *  @param maxLength The max length of a generated ID.
   *  @return A generated ID.
   */
  public static String generate(int minLength, int maxLength) {
    String id = "";
    int length = calcLength(minLength, maxLength);

    for (int z = 0; z < length; z++) {
      char c    = 0;
      int  type = (int)(Math.random()*3);
      switch (type) { //48-57  65-90  97-122
        case 0: c = (char)(Math.random()*10+48);  break;
        case 1: c = (char)(Math.random()*26+65);  break;
        case 2: c = (char)(Math.random()*26+97);  break;
      }
      id += c;
    }

    return id;
  }


  /** Generates an ID of random length that contains just numbers.
   *  @param minLength The min length of a generated ID.
   *  @param maxLength The max length of a generated ID.
   *  @return A generated ID.
   */
  public static String generateNumbers(int minLength, int maxLength) {
    String id = "";
    int length = calcLength(minLength, maxLength);

    for (int z = 0; z < length; z++)
      id += (char)(Math.random()*10+48);

    return id;
  }


  /** Calculate random length */
  private static int calcLength(int minLength, int maxLength) {
    return minLength+(int)((maxLength-minLength+1)*Math.random());
  }
}
