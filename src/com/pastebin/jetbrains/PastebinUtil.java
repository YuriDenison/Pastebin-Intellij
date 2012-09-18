package com.pastebin.jetbrains;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.text.StringUtil;
import com.pastebin.jetbrains.ui.PastebinLoginDialog;
import com.pastebin.jetbrains.ui.PastebinSubmitDialog;
import org.apache.commons.httpclient.NameValuePair;
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


  public static List<Paste> getTrendPasteList() throws PastebinException {
    return getPasteList(RequestUtil.constructTrendsParameters());
  }

  public static List<Paste> getUserPasteList(final int limit) throws PastebinException {
    return getPasteList(RequestUtil.constructListParameters(PastebinSettings.getInstance().getLoginId(), limit));
  }

  @Nullable
  private static List<Paste> getPasteList(@Nullable NameValuePair[] pairs) throws PastebinException {
    try {
      final Element rootElement = new SAXBuilder(false).build(new StringReader(RequestUtil.request(pairs))).getRootElement();
      final List pastes = rootElement.getChildren(PastebinBundle.message(PastebinBundle.message("dom.paste")));
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
      showNotification(PastebinBundle.message("failure"), PastebinBundle.message("network.error"), false);
      LOG.debug(e.getMessage());
      return null;
    } catch (IOException e) {
      showNotification(PastebinBundle.message("failure"), PastebinBundle.message("network.error"), false);
      LOG.debug(e.getMessage());
      return null;
    }
  }

  public static String getRawPasteText(String key) {
    return RequestUtil.getRawPasteText(key);
  }


  private static void copyToClipboard(String str) {
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
      String response = RequestUtil.request(login, password);
      PastebinSettings.getInstance().setLoginID(response);
      return true;
    } catch (PastebinException e) {
      PastebinSettings.getInstance().setLoginID(null);
      return false;
    } catch (Exception ignored) {
    }
    return false;
  }


  public static void submitPaste(Project project, String text) {
    final PastebinSettings settings = PastebinSettings.getInstance();
    final PastebinSubmitDialog submitDialog = new PastebinSubmitDialog(project, text);
    submitDialog.show();
    if (!submitDialog.isOK()) {
      return;
    }
    final Paste paste = submitDialog.getPaste();

    String userKey = null;
    if (paste.getAccessType() != Paste.AccessType.UNLISTED) {
      final boolean logged = checkCredentials(settings.getLogin(), settings.getPassword());
      if (logged) {
        userKey = settings.getLoginId();
      } else {
        PastebinLoginDialog loginDialog = new PastebinLoginDialog(project);
        loginDialog.show();
        if (loginDialog.isOK()) {
          userKey = settings.getLoginId();
        }
      }
    }
    try {
      final String response =
          RequestUtil.request(RequestUtil.constructCreateParameters(paste, userKey));
      final boolean clipboard = settings.getCopyToClipboard();
      showNotification(PastebinBundle.message("success"),
          PastebinBundle.message("paste.created", response) +
              (clipboard ? PastebinBundle.message("clipboard.copied") : ""), true);
      if (clipboard) {
        copyToClipboard(response);
      }
    } catch (PastebinException e1) {
      showNotification(PastebinBundle.message("failure"), e1.getMessage(), false);
    }
  }

  public static void showNotification(String title, String text, boolean success) {
    Notifications.Bus.notify(new Notification("Pastebin", title, text, success ? NotificationType.INFORMATION : NotificationType.ERROR));
  }

  public static boolean areCredentialsEmpty() {
    PastebinSettings settings = PastebinSettings.getInstance();
    return StringUtil.isEmptyOrSpaces(settings.getLogin()) || StringUtil.isEmptyOrSpaces(settings.getPassword());
  }
}
