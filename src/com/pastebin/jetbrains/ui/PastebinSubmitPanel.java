package com.pastebin.jetbrains.ui;

import com.intellij.ui.ScrollPaneFactory;
import com.pastebin.jetbrains.Paste;
import com.pastebin.jetbrains.PastebinBundle;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Set;

/**
 * @author Yuri Denison
 * @date 13.09.12
 */
public class PastebinSubmitPanel extends JPanel {
  private static final int WIDTH = 500;
  private static final int HEIGHT = 250;
  private static final String SELECTED_LANGUAGE = "None";

  private JPanel codePanel;
  private JPanel optionsPanel;

  private JLabel syntaxLabel;
  private JLabel exposureLabel;
  private JLabel expirationLabel;
  private JLabel nameLabel;

  private JComboBox syntaxBox;
  private JComboBox exposureBox;
  private JComboBox expirationBox;
  private JTextField nameField;

  private JTextArea code;

  public PastebinSubmitPanel(final String text) {
    super(new BorderLayout());

    initCodePanel(text);
    initOptionsPanel();
    add(codePanel, BorderLayout.CENTER);
    add(optionsPanel, BorderLayout.SOUTH);
  }

  private void initOptionsPanel() {
    initSyntax();
    initExposure();
    initExpiration();
    initName();
    optionsPanel = new JPanel(new GridLayout(2, 4, 5, 5));
    optionsPanel.add(syntaxLabel);
    optionsPanel.add(syntaxBox);
    optionsPanel.add(exposureLabel);
    optionsPanel.add(exposureBox);
    optionsPanel.add(expirationLabel);
    optionsPanel.add(expirationBox);
    optionsPanel.add(nameLabel);
    optionsPanel.add(nameField);
  }

  private void initName() {
    nameLabel = new JLabel(PastebinBundle.message("label.name"));
    nameField = new JTextField();
  }

  private void initExpiration() {
    expirationLabel = new JLabel(PastebinBundle.message("label.expiration"));
    expirationBox = new JComboBox(Paste.ExpireDate.values());
    expirationBox.setSelectedItem(Paste.ExpireDate.NEVER);
  }

  private void initExposure() {
    exposureLabel = new JLabel(PastebinBundle.message("label.exposure"));
    exposureBox = new JComboBox(Paste.AccessType.values());
    exposureBox.setSelectedItem(Paste.AccessType.UNLISTED);
  }

  private void initSyntax() {
    syntaxLabel = new JLabel(PastebinBundle.message("label.syntax"));
    syntaxBox = new JComboBox();
    final Set<String> langSet = Paste.languages.keySet();
    final String[] lang = langSet.toArray(new String[langSet.size()]);
    Arrays.sort(lang);
    for (String s : lang) {
      syntaxBox.addItem(s);
    }
    syntaxBox.setSelectedItem(SELECTED_LANGUAGE);
  }

  private void initCodePanel(String text) {
    codePanel = new JPanel();
    code = new JTextArea(text);
    final JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(code);
    scrollPane.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    codePanel.setAlignmentX(CENTER_ALIGNMENT);
    codePanel.setAlignmentY(CENTER_ALIGNMENT);
    codePanel.add(scrollPane);
  }

  public Paste getPaste() {
    return new Paste(code.getText(),
        nameField.getText(),
        (String) syntaxBox.getSelectedItem(),
        (Paste.ExpireDate) expirationBox.getSelectedItem(),
        (Paste.AccessType) exposureBox.getSelectedItem());
  }
}
