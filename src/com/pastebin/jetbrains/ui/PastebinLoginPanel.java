package com.pastebin.jetbrains.ui;

import com.intellij.ui.DocumentAdapter;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

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

  {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
    $$$setupUI$$$();
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer
   * >>> IMPORTANT!! <<<
   * DO NOT edit this method OR call it in your code!
   *
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    myPane = new JPanel();
    myPane.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
    final Spacer spacer1 = new Spacer();
    myPane.add(spacer1, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    myLoginTextField = new JTextField();
    myPane.add(myLoginTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    myPasswordField = new JPasswordField();
    myPane.add(myPasswordField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    final JLabel label1 = new JLabel();
    label1.setText("Login");
    myPane.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JLabel label2 = new JLabel();
    label2.setText("Password");
    myPane.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
  }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() {
    return myPane;
  }
}
