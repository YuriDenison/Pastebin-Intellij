package com.pastebin.jetbrains.ui;

import com.intellij.ui.DocumentAdapter;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author Yuri Denison
 * @date 12.07.12
 */
public class PastebinLoginPanel {

  private JPanel myPane;

  private JTextField myLoginTextField;
  private JPasswordField myPasswordField;

  public PastebinLoginPanel(final PastebinLoginDialog dialog) {
    DocumentListener listener = new DocumentAdapter() {
      protected void textChanged(DocumentEvent documentEvent) {
        dialog.clearErrors();
      }
    };

    myLoginTextField.getDocument().addDocumentListener(listener);
    myPasswordField.getDocument().addDocumentListener(listener);
  }

  public JComponent getPanel() {
    return myPane;
  }

  public void setLogin(String login) {
    myLoginTextField.setText(login);
  }

  public void setPassword(String password) {
    myPasswordField.setText(password);
  }

  public String getLogin() {
    return myLoginTextField.getText().trim();
  }

  public String getPassword() {
    return String.valueOf(myPasswordField.getPassword());
  }

  public JComponent getPreferrableFocusComponent() {
    return myLoginTextField;
  }
}
