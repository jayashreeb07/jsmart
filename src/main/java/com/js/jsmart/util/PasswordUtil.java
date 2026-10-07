package com.js.jsmart.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * BCrypt password utility (jBCrypt). Never store plaintext.
 */
public final class PasswordUtil {
  private PasswordUtil() { }

  /**
   * Hash a raw password.
   * @param raw raw password
   * @return BCrypt hash
   */
  public static String hash(String raw) {
    return BCrypt.hashpw(raw, BCrypt.gensalt(10));
  }

  /**
   * Verify a raw password against a hash.
   * @param raw raw password
   * @param hash stored hash
   * @return true if match
   */
  public static boolean verify(String raw, String hash) {
    if (raw == null || hash == null) {
      return false;
    }
    try {
      return BCrypt.checkpw(raw, hash);
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
