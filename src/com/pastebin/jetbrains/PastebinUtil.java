package com.pastebin.jetbrains;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.net.HttpConfigurable;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yuri Denison
 * @date 12.07.12
 */
public class PastebinUtil {
  private static final Logger LOG = Logger.getInstance(PastebinUtil.class);

  public static final String PASTEBIN = "Pastebin";
  public static final Icon ICON = IconLoader.getIcon("res/pastebin.png", PastebinUtil.class);
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

  public List<Paste> getTrendPasteList() throws PastebinException {
    return getPasteList(constructTrendsParameters());
  }

  public List<Paste> getUserPasteList(final int limit) throws PastebinException {
    return getPasteList(constructListParameters(PastebinSettings.getInstance().getLoginId(), limit));
  }

  @Nullable
  private List<Paste> getPasteList(NameValuePair[] pairs) throws PastebinException {
    try {
      final Element rootElement = new SAXBuilder(false).build(new StringReader(request(pairs))).getRootElement();
      final List pastes = rootElement.getChildren(PastebinBundle.message("paste"));
      final List<Paste> list = new ArrayList<Paste>();
      for (Object o : pastes) {
        if (!(o instanceof Element)) {
          continue;
        }
        final Element element = (Element) o;
        final String name = element.getAttributeValue(PastebinBundle.message("dom.title"));
        final String key = element.getAttributeValue(PastebinBundle.message("dom.key"));
        final String url = element.getAttributeValue(PastebinBundle.message("dom.url"));
        final String language = element.getAttributeValue(PastebinBundle.message("dom.language"));
        final int hits = Integer.parseInt(element.getAttributeValue(PastebinBundle.message("dom.hits")));
        final long date = Long.parseLong(element.getAttributeValue(PastebinBundle.message("dom.date")));
        final Paste.AccessType accessType = Paste.AccessType.getAccessType(Integer.parseInt(element.getAttributeValue(PastebinBundle.message("dom.access.type"))));
        final Paste.ExpireDate expireDate = Paste.ExpireDate.getExpireDate(element.getAttributeValue(PastebinBundle.message("dom.expire.date")));

        list.add(new Paste(name, language, expireDate, accessType, key, date, hits, url));
      }
      return list;
    } catch (JDOMException e) {
      LOG.debug(e.getMessage());
      return null;
    } catch (IOException e) {
      LOG.debug(e.getMessage());
      return null;
    }
  }

  public static String getRawPasteText(String key) {
    final HttpClient client = new HttpClient();
    final HttpMethod res = new PostMethod(RAW_URL + key);
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

  @Nullable
  public static String request(NameValuePair[] pairs) throws PastebinException {
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

  public static String request(String login, String password) throws PastebinException {
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

  private static NameValuePair[] constructTrendsParameters() {
    return new NameValuePair[]{
        new NameValuePair("api_option", "trends"),
        new NameValuePair("api_dev_key", API_KEY)
    };
  }

  private static NameValuePair[] constructListParameters(final String userKey, final int limit) {
    return new NameValuePair[]{
        new NameValuePair("api_option", "list"),
        new NameValuePair("api_dev_key", API_KEY),
        new NameValuePair("api_user_key", userKey),
        new NameValuePair("api_results_limit", String.valueOf(limit))
    };
  }

  public static NameValuePair[] constructCreateParameters(final Paste paste, @Nullable final String userKey) {
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

  public static void copyToClipboard(String str) {
    StringSelection selection = new StringSelection(str);
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
  }

  public static boolean checkCredentials(final String login, final String password) {
    if (login == null && password == null && areCredentialsEmpty()) {
      return false;
    }
    if (login != null && password != null) {
      return testConnection(login, password);
    }
    PastebinSettings settings = PastebinSettings.getInstance();
    return testConnection(settings.getLogin(), settings.getPassword());
  }

  private static boolean testConnection(String login, String password) {
    try {
      String response = request(login, password);
      PastebinSettings.getInstance().setLoginID(response);
      return true;
    } catch (PastebinException e) {
      PastebinSettings.getInstance().setLoginID(null);
      return false;
    } catch (Exception ignored) {
    }
    return false;
  }

  public static void showNotification(String title, String text, boolean success) {
    Notifications.Bus.notify(new Notification("Pastebin", title, text, success ? NotificationType.INFORMATION : NotificationType.ERROR));
  }

  public static boolean areCredentialsEmpty() {
    PastebinSettings settings = PastebinSettings.getInstance();
    return StringUtil.isEmptyOrSpaces(settings.getLogin()) || StringUtil.isEmptyOrSpaces(settings.getPassword());
  }
}
