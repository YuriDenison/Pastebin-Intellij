package com.pastebin.jetbrains.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.pastebin.jetbrains.PastebinBundle;
import com.pastebin.jetbrains.PastebinUtil;

import javax.swing.*;

/**
 * @author Yuri Denison
 * @date 8/28/12
 */
public class PastebinSubmitDialog extends DialogWrapper {
  private PastebinSubmitPanel myPanel;

  public PastebinSubmitDialog(Project project, String text) {
    super(project, true);
    myPanel = new PastebinSubmitPanel(text);

    setTitle(PastebinBundle.message("submit.title"));
    setOKButtonText(PastebinBundle.message("submit"));
    init();
  }

  @Override
  protected JComponent createCenterPanel() {
    return myPanel;
  }

  public String getCode() {
    return myPanel.getCode();
  }

  public PastebinUtil.ExpireDate getExpireDate() {
    return myPanel.getExpireDate();
  }

  public PastebinUtil.AccessType getExposure() {
    return myPanel.getExposure();
  }

  public String getSelectedLanguage() {
    return myPanel.getSelectedLanguage();
  }

  public String getName() {
    return myPanel.getName();
  }
}
