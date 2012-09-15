package com.pastebin.jetbrains.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.pastebin.jetbrains.Paste;
import com.pastebin.jetbrains.PastebinBundle;

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

  public Paste getPaste() {
    return myPanel.getPaste();
  }
}
