package com.intellij.plugins.pastebin;

import org.jetbrains.annotations.NonNls;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Yuri Denison
 * @date 12.07.12
 */
public class PastebinUtil {
  @NonNls
  private static final String API_KEY = "d42e7b2a43c3bd1149d2bbdae06730dd";
  private static final String API_OPTION = "paste";
  private static final int API_PASTE_PRIVATE = 1;
  private static final String PASTE_URL = "http://pastebin.com/api/api_post.php";

  public static String createPaste(final String text) throws IOException {
    final URLConnection connection = new URL(PASTE_URL).openConnection();
    connection.setDoOutput(true);
    final OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
    final String query = constructQuery(text);
    out.write(query);
    out.write("\r\n");
    out.flush();
    out.close();

    final BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    final String str = br.readLine();
    br.close();
    return str;
  }

  private static String constructQuery(final String text) {
    final StringBuilder sb = new StringBuilder();
    sb.append("api_option=" + API_OPTION).append("&");
    sb.append("api_paste_private=" + API_PASTE_PRIVATE).append("&");
    sb.append("api_dev_key=" + API_KEY).append("&");
    sb.append("api_paste_code=").append(text);
    return sb.toString();
  }
}
