package com.pastebin.jetbrains.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.pastebin.jetbrains.PastebinUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class PasteSubmitPanel extends DialogWrapper {
  private JPanel contentPanel;
  private JButton buttonOK;
  private JTextField textField;
  private JPanel optionsPanel;
  private JTextField languageField;
  private JComboBox syntaxComboBox;
  private JTextField accessField;
  private JComboBox accessComboBox;
  private JTextField expireField;
  private JComboBox expireComboBox;
  private JTextField nameField;
  private JTextField nameInputField;
  private JPanel buttonPanel;
  private JPanel mainPanel;

  public PasteSubmitPanel(final Project project, final String text) {
    super(project, true);
    setModal(true);
    getRootPane().setDefaultButton(buttonOK);

    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onOK();
      }
    });

    contentPanel.registerKeyboardAction(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    initTextField(text);
    initLanguageChooser();
    initAccessChooser();
    initExpirationChooser();
    initNameField();
  }

  private void initNameField() {
    nameField.setText("Paste name / title:");
    nameInputField.setText("");
  }

  private void initExpirationChooser() {
    expireField.setText("Paste expiration:");
    for (PastebinUtil.ExpireDate expireDate : PastebinUtil.ExpireDate.values()) {
      expireComboBox.addItem(expireDate);
    }
    expireComboBox.setSelectedItem(PastebinUtil.ExpireDate.NEVER);
  }

  private void initAccessChooser() {
    accessField.setText("Paste exposure:");
    for (PastebinUtil.AccessType accessType : PastebinUtil.AccessType.values()) {
      accessComboBox.addItem(accessType);
    }
    accessComboBox.setSelectedItem(PastebinUtil.AccessType.UNLISTED);
  }

  private void initLanguageChooser() {
    languageField.setText("Syntax highlighting:");
    for (String str : PastebinUtil.languages.keySet()) {
      syntaxComboBox.addItem(str);
    }
    syntaxComboBox.setSelectedItem("None");
  }

  private void initTextField(String text) {
    textField.setText(text);
  }

  private void onOK() {
    try {
      PastebinUtil.createRequest(PastebinUtil.constructCreateParameters(
          "paste",
          textField.getText(),
          null,
          nameInputField.getText(),
          syntaxComboBox.getSelectedItem().toString(),
          (PastebinUtil.AccessType) accessComboBox.getSelectedItem(),
          (PastebinUtil.ExpireDate) expireComboBox.getSelectedItem()
      ));
    } catch (Exception e) {
      e.printStackTrace();
    }
    dispose();
  }

  @Override
  protected JComponent createCenterPanel() {
    return optionsPanel;
  }

  @Override
  public JComponent getPreferredFocusedComponent() {
    return textField;
  }
}
