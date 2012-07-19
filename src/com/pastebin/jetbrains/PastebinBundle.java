package com.pastebin.jetbrains;

import com.intellij.CommonBundle;
import com.intellij.reference.SoftReference;

import java.lang.ref.Reference;
import java.util.ResourceBundle;

/**
 * @author Yuri Denison
 * @date 12.07.12
 */
public class PastebinBundle {
  private static Reference<ResourceBundle> myBundle;

  private static final String BUNDLE = PastebinBundle.class.getPackage().getName() + ".Messages";

  public static String message(String key, Object... params) {
    return CommonBundle.message(getBundle(), key, params);
  }

  private static ResourceBundle getBundle() {
    ResourceBundle bundle = null;
    if (myBundle != null) {
      bundle = myBundle.get();
    }
    if (bundle == null) {
      bundle = ResourceBundle.getBundle(BUNDLE);
      myBundle = new SoftReference<ResourceBundle>(bundle);
    }
    return bundle;
  }
}
