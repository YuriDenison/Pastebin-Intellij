package com.pastebin.jetbrains.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.pastebin.jetbrains.PastebinBundle;
import com.pastebin.jetbrains.PastebinSettings;
import com.pastebin.jetbrains.util.PastebinUtil;

import javax.swing.*;

/**
 * @author Yuri Denison
 * @date 8/28/12
 */
public class PastebinLoginDialog extends DialogWrapper {
  private PastebinLoginPanel myPanel;

  public PastebinLoginDialog(Project project) {
    super(project, true);

    myPanel = new PastebinLoginPanel(this);

    PastebinSettings settings = PastebinSettings.getInstance();
    myPanel.setLogin(settings.getLogin());
    myPanel.setPassword(settings.getPassword());

    setTitle(PastebinBundle.message("login.pastebin"));
    setOKButtonText(PastebinBundle.message("login"));
    init();
  }

  @Override
  protected JComponent createCenterPanel() {
    return myPanel.getPanel();
  }

  protected Action[] createActions() {
    return new Action[]{getOKAction(), getCancelAction()};
  }

  @Override
  public JComponent getPreferredFocusedComponent() {
    return myPanel.getPreferrableFocusComponent();
  }

  @Override
  protected void doOKAction() {
    String login = myPanel.getLogin();
    String password = myPanel.getPassword();
    if (PastebinUtil.checkCredentials(login, password)) {
      PastebinSettings settings = PastebinSettings.getInstance();
      settings.setLogin(login);
      settings.setPassword(password);
      super.doOKAction();
    } else {
      setErrorText(PastebinBundle.message("cannot-login"));
    }
  }

  public void clearErrors() {
    setErrorText(null);
  }
}
