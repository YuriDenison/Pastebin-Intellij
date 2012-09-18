package com.pastebin.jetbrains.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.ui.Messages;
import com.pastebin.jetbrains.PastebinBundle;
import com.pastebin.jetbrains.PastebinUtil;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
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
}
