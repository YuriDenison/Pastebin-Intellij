package com.pastebin.jetbrains.util;

import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.concurrency.SwingWorker;
import com.pastebin.jetbrains.Paste;
import com.pastebin.jetbrains.PastebinBundle;
import com.pastebin.jetbrains.PastebinException;
import com.pastebin.jetbrains.PastebinSettings;
import com.pastebin.jetbrains.ui.PastebinLoginDialog;
import com.pastebin.jetbrains.ui.PastebinSubmitDialog;
import org.apache.commons.httpclient.NameValuePair;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
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

  public static final Icon ICON = IconLoader.getIcon("/com/pastebin/jetbrains/res/pastebin.png", PastebinUtil.class);
  public static final Icon DELETE_ICON = AllIcons.Actions.Delete;
  public static final Icon REFRESH_ICON = AllIcons.Actions.Refresh;
  public static final Icon BROWSER_ICON = AllIcons.Xml.Browsers.Chrome16;

  public static final String PASTEBIN = "Pastebin";
  private static final String DOM_START = "<list>";
  private static final String DOM_END = "</list>";


  public static List<Paste> getTrendPasteList() throws PastebinException {
    return getPasteList(RequestUtil.constructTrendsParameters());
  }

  public static List<Paste> getUserPasteList(final int limit) throws PastebinException {
    return getPasteList(RequestUtil.constructListParameters(PastebinSettings.getInstance().getLoginId(), limit));
  }

  public static boolean deletePaste(final String key, final String userKey) throws PastebinException {
    final String request = RequestUtil.request(RequestUtil.constructDeleteParameters(userKey, key));
    return request != null;
  }

  @Nullable
  private static List<Paste> getPasteList(@Nullable NameValuePair[] pairs) throws PastebinException {
    try {
      final String request = RequestUtil.request(pairs);
      final Element rootElement = new SAXBuilder(false).build(new StringReader(DOM_START + request + DOM_END)).getRootElement();
      final List pastes = rootElement.getChildren(PastebinBundle.message("dom.paste"));
      final List<Paste> list = new ArrayList<Paste>();
      for (Object o : pastes) {
        if (!(o instanceof Element)) {
          continue;
        }
        final Element element = (Element) o;
        String name = element.getChildText(PastebinBundle.message("dom.title"));
        if (name == null || name.isEmpty()) {
          name = PastebinBundle.message("name.untitled");
        }
        final String key = element.getChildText(PastebinBundle.message("dom.key"));
        final String url = element.getChildText(PastebinBundle.message("dom.url"));
        final String language = element.getChildText(PastebinBundle.message("dom.language"));
        final int hits = Integer.parseInt(element.getChildText(PastebinBundle.message("dom.hits")));
        final long date = Long.parseLong(element.getChildText(PastebinBundle.message("dom.date")));
        final Paste.AccessType accessType = Paste.AccessType.getAccessType(Integer.parseInt(element.getChildText(PastebinBundle.message("dom.access.type"))));
        final Paste.ExpireDate expireDate = Paste.ExpireDate.getExpireDate(element.getChildText(PastebinBundle.message("dom.expire.date")));

        list.add(new Paste(name, language, expireDate, accessType, key, date, hits, url));
      }
      return list;
    } catch (JDOMException e) {
      showNotification(PastebinBundle.message("failure"), PastebinBundle.message("network.error"), false, true);
      LOG.debug(e.getMessage());
      return null;
    } catch (IOException e) {
      showNotification(PastebinBundle.message("failure"), PastebinBundle.message("network.error"), false, true);
      LOG.debug(e.getMessage());
      return null;
    }
  }

  public static String getRawPasteText(String key) {
    return RequestUtil.getRawPasteText(key);
  }


  private static void copyToClipboard(String str) {
    CopyPasteManager.getInstance().setContents(new StringSelection(str));
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
      PastebinSettings.getInstance().setLogin(login);
      PastebinSettings.getInstance().setPassword(password);
      PastebinSettings.getInstance().setLoginID(response);
      return true;
    } catch (PastebinException e) {
      PastebinSettings.getInstance().setLoginID(null);
      return false;
    } catch (Exception ignored) {
    }
    return false;
  }


  public static void submitPaste(final Project project, String text, final boolean inSettings) {
    final PastebinSettings settings = PastebinSettings.getInstance();
    final PastebinSubmitDialog submitDialog = new PastebinSubmitDialog(project, text);
    submitDialog.show();
    if (!submitDialog.isOK()) {
      return;
    }
    final Paste paste = submitDialog.getPaste();

    ApplicationManager.getApplication().invokeLater(new Runnable() {
      @Override
      public void run() {
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
        final String finalUserKey = userKey;

        new SwingWorker() {
          private Boolean result = false;
          private boolean clipboard = false;
          private String response;
          private String error = "";

          @Override
          public Object construct() {
            try {
              response =
                  RequestUtil.request(RequestUtil.constructCreateParameters(paste, finalUserKey));
              result = true;
              if (response == null) {
                result = false;
                return null;
              }
              clipboard = settings.getCopyToClipboard();
              if (clipboard) {
                copyToClipboard(response);
              }
            } catch (PastebinException e1) {
              result = null;
              error = e1.getMessage();
            }
            return null;
          }

          @Override
          public void finished() {
            if (result == null) {
              showNotification(PastebinBundle.message("failure"), error, false, inSettings);
              return;
            }
            if (result) {
              showNotification(PastebinBundle.message("success"),
                  PastebinBundle.message("paste.created", response) +
                      (clipboard ? PastebinBundle.message("clipboard.copied") : ""), true, inSettings);

            } else {
              showNotification(PastebinBundle.message("failure"), PastebinBundle.message("network.error"), false, inSettings);
            }
          }
        }.start();
      }
    });
  }

  public static void showNotification(String title, String text, boolean success, boolean inSettings) {
    if (!inSettings) {
      Notifications.Bus.notify(new Notification("Pastebin", title, text, success ? NotificationType.INFORMATION : NotificationType.ERROR));
    } else {
      Messages.showInfoMessage(text, title);
    }
  }

  public static boolean areCredentialsEmpty() {
    PastebinSettings settings = PastebinSettings.getInstance();
    return StringUtil.isEmptyOrSpaces(settings.getLogin()) || StringUtil.isEmptyOrSpaces(settings.getPassword());
  }
}
