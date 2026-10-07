package com.js.jsmart.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.servlet.ServletContext;

/**
 * Environment/property based configuration. Never hardcode production secrets.
 */
public final class ConfigUtil {
  private static final Properties PROPS = new Properties();

  private ConfigUtil() { }

  /** Load app.properties from classpath once. */
  public static synchronized void init(ServletContext ctx) {
    if (!PROPS.isEmpty()) {
      return;
    }
    try (InputStream in = ConfigUtil.class.getClassLoader().getResourceAsStream("app.properties")) {
      if (in != null) {
        PROPS.load(in);
      }
    } catch (IOException e) {
      // defaults apply
    }
    if (ctx != null) {
      for (String k : new String[]{"db.url", "db.user", "db.password", "ai.chatbot.provider"}) {
        String v = ctx.getInitParameter(k);
        if (v != null) {
          PROPS.setProperty(k, v);
        }
      }
    }
  }

  /** Get property with env override (UPPER_SNAKE). */
  public static String get(String key, String def) {
    String envKey = key.toUpperCase().replace('.', '_').replace('-', '_');
    String env = System.getenv(envKey);
    if (env != null && !env.isEmpty()) {
      return env;
    }
    return PROPS.getProperty(key, def);
  }

  /** Set property (tests). */
  public static void set(String key, String val) {
    PROPS.setProperty(key, val);
  }
}
