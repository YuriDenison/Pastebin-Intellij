package com.pastebin.jetbrains;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.pastebin.jetbrains.ui.PastebinLoginDialog;
import com.pastebin.jetbrains.ui.PastebinSubmitDialog;

/**
 * @author Yuri Denison
 * @date 12.07.12
 */
public class CreatePasteAction extends AnAction {
  public CreatePasteAction() {
    super(null, null, PastebinUtil.ICON);
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    final Project project = LangDataKeys.PROJECT.getData(e.getDataContext());
    final Editor editor = LangDataKeys.EDITOR.getData(e.getDataContext());
    assert editor != null;
    final Document document = editor.getDocument();
    final SelectionModel selectionModel = editor.getSelectionModel();

    final String text = selectionModel.hasSelection() ? selectionModel.getSelectedText() : document.getText();
    final PastebinSubmitDialog submitDialog = new PastebinSubmitDialog(project, text);
    submitDialog.show();
    if (!submitDialog.isOK()) {
      return;
    }
    final Paste paste = submitDialog.getPaste();

    String userKey = null;
    if (paste.getAccessType() != Paste.AccessType.UNLISTED) {
      final PastebinSettings settings = PastebinSettings.getInstance();
      final boolean logged = PastebinUtil.checkCredentials(settings.getLogin(), settings.getPassword());
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
          PastebinUtil.request(PastebinUtil.constructCreateParameters(paste, userKey));
      PastebinUtil.showNotification(PastebinBundle.message("success"), PastebinBundle.message("paste.created", response), true);
      PastebinUtil.copyToClipboard(response);
    } catch (PastebinException e1) {
      PastebinUtil.showNotification(PastebinBundle.message("failure"), e1.getMessage(), false);
    }
  }
}
