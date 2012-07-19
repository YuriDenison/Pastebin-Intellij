package com.pastebin.jetbrains;

import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;

/**
 * @author Yuri Denison
 * @date 12.07.12
 */
public class CreatePasteAction extends EditorAction {
  protected CreatePasteAction(EditorActionHandler defaultHandler) {
    super(defaultHandler);
  }


  public CreatePasteAction() {
    super(new CreatePasteActionHandler());
  }
}
