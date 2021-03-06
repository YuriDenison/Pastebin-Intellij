package com.pastebin.jetbrains.ui;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.pastebin.jetbrains.PastebinSettings;
import com.pastebin.jetbrains.ui.settings.PastebinSettingsPanel;
import com.pastebin.jetbrains.util.PastebinUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author Yuri Denison
 * @date 12.07.12
 */
public class PastebinSettingsConfigurable implements SearchableConfigurable {

  private PastebinSettingsPanel mySettingsPane;
  private final PastebinSettings mySettings;

  public PastebinSettingsConfigurable() {
    mySettings = PastebinSettings.getInstance();
  }

  public String getDisplayName() {
    return PastebinUtil.PASTEBIN;
  }

  public Icon getIcon() {
    return PastebinUtil.ICON;
  }

  @NotNull
  public String getHelpTopic() {
    return "settings.pastebin";
  }

  public JComponent createComponent() {
    if (mySettingsPane == null) {
      mySettingsPane = new PastebinSettingsPanel(mySettings.getCopyToClipboard());
    }
    reset();
    return mySettingsPane.getPanel();
  }

  public boolean isModified() {
    return mySettingsPane == null
        || !mySettings.getLogin().equals(mySettingsPane.getLogin())
        || !mySettings.getPassword().equals(mySettingsPane.getPassword())
        || !mySettings.getCopyToClipboard() == mySettingsPane.getCopyToClipboard();
  }

  public void apply() throws ConfigurationException {
    if (mySettingsPane != null) {
      mySettings.setLogin(mySettingsPane.getLogin());
      mySettings.setPassword(mySettingsPane.getPassword());
      mySettings.setCopyToClipboard(mySettingsPane.getCopyToClipboard());
    }
  }

  public void reset() {
    if (mySettingsPane != null) {
      mySettingsPane.setLogin(mySettings.getLogin());
      mySettingsPane.setPassword(mySettings.getPassword());
      mySettingsPane.setCopyToClipboard(mySettings.getCopyToClipboard());
    }
  }

  public void disposeUIResources() {
    mySettingsPane = null;
  }

  @NotNull
  public String getId() {
    return getHelpTopic();
  }

  public Runnable enableSearch(String option) {
    return null;
  }
}
