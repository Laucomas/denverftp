/**
 * Date: Jan 6, 2002
 * Time: 2:27:31 AM
 * General operation related to data encryption and decryption.
 */
package com.coldcore.misc5;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {

  public enum Mode {
    ENCRYPT(Cipher.ENCRYPT_MODE),
    DECRYPT(Cipher.DECRYPT_MODE);

    public int cipherMode;

    Mode(int cipherMode) {
     this.cipherMode = cipherMode;
   }
  }


  private Crypto() {}


  /** Create SecretKey object.
   *  @param key The byte array that contains the key.
   *  @param algorithm The name of the cipher algorithm.
   *  @return A created SecretKey object.
   */
  public static SecretKey createKey(byte[] key, String algorithm) {
    return new SecretKeySpec(key, algorithm);
  }


  /** Encrypt/decrypt data
   *  @param data The data to deal with.
   *  @param mode Encryption or decryption
   *  @param key The SecretKey ojbect used to encrypt/decrypt the data
   *  @param algorithm The name of the cipher algorithm
   *  @return Encrypted/decrypted data
   */
  public static byte[] action(byte[] data, Mode mode, SecretKey key, String algorithm) throws Exception {
    if (data == null || data.length == 0 || key == null || algorithm == null) return data;
    Cipher cipher = Cipher.getInstance(algorithm+"/ECB/PKCS5Padding");
    cipher.init(mode.cipherMode, key);
    return cipher.doFinal(data);
  }
}