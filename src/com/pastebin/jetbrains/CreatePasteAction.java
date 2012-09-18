package com.pastebin.jetbrains;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.project.Project;
import com.pastebin.jetbrains.ui.PastebinLoginDialog;
import com.pastebin.jetbrains.ui.PastebinSubmitDialog;

/**
 * @author Yuri Denison
 * @date 12.07.12
 */
public class CreatePasteAction extends EditorAction {
  public CreatePasteAction() {
    super(new PastebinHandler());
    getTemplatePresentation().setIcon(PastebinUtil.ICON);
  }

  @Override
  public void update(AnActionEvent e) {
    super.update(e);
    if (ActionPlaces.isPopupPlace(e.getPlace())) {
      e.getPresentation().setVisible(e.getPresentation().isEnabled());
    }
  }

  private static class PastebinHandler extends EditorActionHandler {
    @Override
    public void execute(Editor editor, DataContext dataContext) {
      final Project project = LangDataKeys.PROJECT.getData(dataContext);
      if (editor == null) {
        return;
      }
      final Document document = editor.getDocument();
      final SelectionModel selectionModel = editor.getSelectionModel();

      final String text = selectionModel.hasSelection() ? selectionModel.getSelectedText() : document.getText();
      PastebinUtil.submitPaste(project, text);
    }
  }
}
