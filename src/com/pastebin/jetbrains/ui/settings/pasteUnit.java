package com.pastebin.jetbrains.ui.settings;

import com.intellij.ui.SideBorder;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;

/**
 * @author Yuri Denison
 * @date 16.09.12
 */
public class PasteUnit {
  private JPanel mainPanel;

  public PasteUnit() {
    mainPanel.setBorder(BorderFactory.createCompoundBorder(
        new SideBorder(UIUtil.getPanelBackground().darker(), SideBorder.BOTTOM),
        BorderFactory.createEmptyBorder(0, 5, 10, 5)));
  }
}
