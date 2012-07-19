package com.pastebin.jetbrains;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.project.Project;
import com.pastebin.jetbrains.ui.PasteSubmitDialog;

/**
 * @author Yuri Denison
 * @date 12.07.12
 */
public class CreatePasteActionHandler extends EditorActionHandler {
  private static final String GROUP_ID = "Pastebin Notification";

  @Override
  public void execute(Editor editor, DataContext dataContext) {
    final Object obj = dataContext.getData("project");
    final Project project = obj != null && obj instanceof Project ? (Project) obj : null;
    final SelectionModel selectionModel = editor.getSelectionModel();
    final String text = selectionModel.hasSelection()
        ? selectionModel.getSelectedText()
        : editor.getDocument().getText();
    new PasteSubmitDialog(project, text).show();
  }
}
