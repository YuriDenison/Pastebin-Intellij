package com.pastebin.jetbrains.util;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.net.HttpConfigurable;
import com.pastebin.jetbrains.Paste;
import com.pastebin.jetbrains.PastebinException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yuri Denison
 * @date 9/18/12
 */
public class RequestUtil {
  private static final Logger LOG = Logger.getInstance(RequestUtil.class);
  private static final String API_KEY = "d42e7b2a43c3bd1149d2bbdae06730dd";
  private static final String POST_URL = "http://pastebin.com/api/api_post.php";
  private static final String LOGIN_URL = "http://pastebin.com/api/api_login.php";
  private static final String RAW_URL = "http://pastebin.com/raw.php?i=";

  private static HttpClient getClient() {
    final HttpClient client = new HttpClient();
    final HttpConfigurable proxyConfig = HttpConfigurable.getInstance();
    if (proxyConfig.USE_HTTP_PROXY) {
      client.getHostConfiguration().setProxy(proxyConfig.PROXY_HOST, proxyConfig.PROXY_PORT);
      if (proxyConfig.PROXY_AUTHENTICATION) {
        UsernamePasswordCredentials proxyCred = new UsernamePasswordCredentials(proxyConfig.PROXY_LOGIN, proxyConfig.getPlainProxyPassword());
        client.getState().setProxyCredentials(AuthScope.ANY, proxyCred);
      }
    }

    return client;
  }

  @Nullable
  protected static String request(@Nullable NameValuePair[] pairs) throws PastebinException {
    final HttpClient client = getClient();
    final HttpMethod res = new PostMethod(POST_URL);
    if (pairs != null) {
      ((PostMethod) res).setRequestBody(pairs);
    }
    final String s;
    try {
      client.executeMethod(res);
      s = res.getResponseBodyAsString();
    } catch (IOException e) {
      LOG.debug("Request failed: " + e.getMessage());
      return null;
    }
    if (s.startsWith("Bad")) {
      throw new PastebinException(s);
    } else {
      return s;
    }
  }

  protected static String request(String login, String password) throws PastebinException {
    final HttpClient client = getClient();
    final HttpMethod res = new PostMethod(LOGIN_URL);
    ((PostMethod) res).setRequestBody(new NameValuePair[]{
        new NameValuePair("api_dev_key", API_KEY),
        new NameValuePair("api_user_name", login),
        new NameValuePair("api_user_password", password)
    });
    final String s;
    try {
      client.executeMethod(res);
      s = res.getResponseBodyAsString();
    } catch (IOException e) {
      LOG.debug("Login request failed: " + e.getMessage());
      return null;
    }
    if (s.startsWith("Bad")) {
      throw new PastebinException(s);
    } else {
      return s;
    }
  }

  protected static NameValuePair[] constructTrendsParameters() {
    return new NameValuePair[]{
        new NameValuePair("api_option", "trends"),
        new NameValuePair("api_dev_key", API_KEY)
    };
  }

  protected static NameValuePair[] constructListParameters(final String userKey, final int limit) {
    return new NameValuePair[]{
        new NameValuePair("api_option", "list"),
        new NameValuePair("api_dev_key", API_KEY),
        new NameValuePair("api_user_key", userKey),
        new NameValuePair("api_results_limit", String.valueOf(limit))
    };
  }

  protected static NameValuePair[] constructCreateParameters(final Paste paste, @Nullable final String userKey) {
    final List<NameValuePair> list = new ArrayList<NameValuePair>();
    list.add(new NameValuePair("api_option", "paste"));
    list.add(new NameValuePair("api_dev_key", API_KEY));
    list.add(new NameValuePair("api_paste_code", paste.getText()));
    list.add(new NameValuePair("api_paste_private", paste.getAccessType().getPastebinCode()));
    list.add(new NameValuePair("api_paste_expire_date", paste.getExpireDate().getPastebinCode()));
    if (userKey != null) {
      list.add(new NameValuePair("api_user_key", userKey));
    }
    final String name = paste.getName();
    if (name != null) {
      list.add(new NameValuePair("api_paste_name", name));
    }
    final String format = paste.getLanguage();
    if (format != null) {
      list.add(new NameValuePair("api_paste_format", Paste.languages.get(format)));
    }

    return list.toArray(new NameValuePair[list.size()]);
  }

  protected static NameValuePair[] constructDeleteParameters(final String userKey, final String key) {
    return new NameValuePair[]{
        new NameValuePair("api_option", "delete"),
        new NameValuePair("api_dev_key", API_KEY),
        new NameValuePair("api_user_key", userKey),
        new NameValuePair("api_paste_key", key)
    };
  }


  protected static String getRawPasteText(String key) {
    final HttpClient client = getClient();
    final HttpMethod res = new GetMethod(RAW_URL + key);

    final String s;
    try {
      client.executeMethod(res);
      s = res.getResponseBodyAsString();
      return s;
    } catch (IOException e) {
      LOG.debug("RAW request failed: " + e.getMessage());
      return null;
    }
  }
}
