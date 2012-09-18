package com.pastebin.jetbrains;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.util.PasswordUtil;
import com.intellij.openapi.util.text.StringUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Yuri Denison
 * @date 12.07.12
 */

@State(
    name = "PastebinSettings",
    storages = {
        @Storage(
            id = "main",
            file = "$APP_CONFIG$/pastebin_settings.xml"
        )}
)

public class PastebinSettings implements PersistentStateComponent<Element> {
  private static final String SETTINGS_TAG = "PastebinSettings";
  private static final String LOGIN = "Login";
  private static final String PASSWORD = "Password";
  private static final String CLIPBOARD = "CopyToClipboard";
  private static final String ID = "LoginId";

  private String myLogin;
  private String myPassword;
  private String loginID;
  private boolean copyToClipboard;

  public static PastebinSettings getInstance() {
    return ServiceManager.getService(PastebinSettings.class);
  }

  public Element getState() {
    if (StringUtil.isEmptyOrSpaces(myLogin) && StringUtil.isEmptyOrSpaces(myPassword)) {
      return null;
    }
    final Element element = new Element(SETTINGS_TAG);
    element.setAttribute(LOGIN, getLogin());
    element.setAttribute(PASSWORD, getEncodedPassword());
    element.setAttribute(ID, getLoginId());
    element.setAttribute(CLIPBOARD, String.valueOf(getCopyToClipboard()));
    return element;
  }

  public String getEncodedPassword() {
    return PasswordUtil.encodePassword(getPassword());
  }

  public void setEncodedPassword(final String password) {
    try {
      setPassword(PasswordUtil.decodePassword(password));
    } catch (NumberFormatException e) {
      // do nothing
    }
  }

  public void loadState(@NotNull final Element element) {
    try {
      setLogin(element.getAttributeValue(LOGIN));
      setEncodedPassword(element.getAttributeValue(PASSWORD));
      setLoginID(element.getAttributeValue(ID));
      setCopyToClipboard(Boolean.valueOf(element.getAttributeValue(CLIPBOARD)));
    } catch (Exception e) {
      // ignore
    }
  }

  @NotNull
  public String getLogin() {
    return myLogin != null ? myLogin : "";
  }

  @NotNull
  public String getPassword() {
    return myPassword != null ? myPassword : "";
  }

  public boolean getCopyToClipboard() {
    return copyToClipboard;
  }

  public String getLoginId() {
    return loginID != null ? loginID : "";
  }

  public void setLogin(String login) {
    myLogin = login != null ? login : "";
  }

  public void setPassword(String password) {
    myPassword = password != null ? password : "";
  }

  public void setLoginID(@Nullable final String loginID) {
    this.loginID = loginID != null ? loginID : "";
  }

  public void setCopyToClipboard(boolean b) {
    copyToClipboard = b;
  }
}


