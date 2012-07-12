package com.intellij.plugins.pastebin;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationType;
import com.intellij.notification.impl.NotificationsManagerImpl;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.project.Project;

import java.io.IOException;

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
    try {
      final String pasteURL = PastebinUtil.createPaste(text);
      final boolean ok = pasteURL.startsWith("http");
      NotificationsManagerImpl.doNotify(
          new Notification(GROUP_ID, "Pastebin",
              ok ? "Paste successfully created: " + pasteURL : "Failed creating paste: " + pasteURL,
              ok ? NotificationType.INFORMATION : NotificationType.WARNING),
          NotificationDisplayType.BALLOON,
          project
      );
    } catch (IOException e) {
      NotificationsManagerImpl.doNotify(
          new Notification(GROUP_ID, "Pastebin", "Error during creating paste", NotificationType.ERROR),
          NotificationDisplayType.BALLOON,
          project
      );
    }
  }
}
