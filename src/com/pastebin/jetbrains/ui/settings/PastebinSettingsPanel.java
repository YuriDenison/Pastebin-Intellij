package com.pastebin.jetbrains.ui.settings;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.pastebin.jetbrains.PastebinBundle;
import com.pastebin.jetbrains.PastebinUtil;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Yuri Denison
 * @date 12.07.12
 */
public class PastebinSettingsPanel {

  private JPanel myPane;

  private JTextField myLoginTextField;
  private JPasswordField myPasswordField;

  private JButton myTestButton;
  private JTextPane mySignupPane;
  private JCheckBox myClipboardCheckbox;
  private JPanel listPanel;

  public PastebinSettingsPanel(final boolean copyToClipboard) {
    String msg = PastebinBundle.message("signup.on.pastebin", "http://pastebin.com/login");
    mySignupPane.setText(msg);
    mySignupPane.setBackground(myPane.getBackground());
    mySignupPane.addHyperlinkListener(new HyperlinkListener() {
      public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
          BrowserUtil.launchBrowser(e.getURL().toExternalForm());
        }
      }
    });

    myTestButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        boolean result = PastebinUtil.checkCredentials(getLogin(), getPassword());
        Messages.showInfoMessage(
            result ? PastebinBundle.message("connection.success") : PastebinBundle.message("cannot.login"),
            result ? PastebinBundle.message("success") : PastebinBundle.message("failure"));
      }
    });

    myClipboardCheckbox.setText(PastebinBundle.message("clipboard.setting"));
    myClipboardCheckbox.setSelected(copyToClipboard);

    listPanel.setLayout(new BorderLayout());
    listPanel.add(new PasteTablePanel(), BorderLayout.CENTER);
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

  public boolean getCopyToClipboard() {
    return myClipboardCheckbox.isSelected();
  }

  public void setCopyToClipboard(boolean b) {
    myClipboardCheckbox.setSelected(b);
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
    myPane.setLayout(new GridLayoutManager(7, 3, new Insets(0, 0, 0, 0), -1, -1));
    final JLabel label1 = new JLabel();
    label1.setText("Login");
    myPane.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JLabel label2 = new JLabel();
    label2.setText("Password");
    myPane.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    myLoginTextField = new JTextField();
    myPane.add(myLoginTextField, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    myPasswordField = new JPasswordField();
    myPane.add(myPasswordField, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    mySignupPane = new JTextPane();
    mySignupPane.setContentType("text/html");
    mySignupPane.setEditable(false);
    mySignupPane.setText("<html>\n  <head>\n\n  </head>\n  <body>\n    <p style=\"margin-top: 0\">\n      \n    </p>\n  </body>\n</html>\n");
    myPane.add(mySignupPane, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    myTestButton = new JButton();
    myTestButton.setText("Test");
    myTestButton.setVerticalAlignment(0);
    myPane.add(myTestButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_NORTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    myClipboardCheckbox = new JCheckBox();
    myClipboardCheckbox.setText("");
    myPane.add(myClipboardCheckbox, new GridConstraints(4, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JSeparator separator1 = new JSeparator();
    myPane.add(separator1, new GridConstraints(3, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JSeparator separator2 = new JSeparator();
    myPane.add(separator2, new GridConstraints(5, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    listPanel = new JPanel();
    listPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
    myPane.add(listPanel, new GridConstraints(6, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
  }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() {
    return myPane;
  }
}
