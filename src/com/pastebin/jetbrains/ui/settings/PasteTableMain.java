package com.pastebin.jetbrains.ui.settings;

import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.ui.FilterComponent;
import com.intellij.ui.GuiUtils;
import com.intellij.ui.ScrollPaneFactory;
import com.pastebin.jetbrains.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import java.awt.*;
import java.net.URL;

/**
 * @author Yuri Denison
 * @date 9/18/12
 */
public class PasteTableMain {
  private JPanel main;
  private JPanel tablePanel;
  private JPanel codePanel;
  private JPanel toolbarPanel;

  private JTextPane codePane;
  private ActionToolbar actionToolbar;

  protected PasteTable pasteTable;
  protected PasteTableModel pasteModel;
  private FilterComponent filter;

  public PasteTableMain() {
    GuiUtils.replaceJSplitPaneWithIDEASplitter(main);
    initCodePanel();

    codePane.addHyperlinkListener(new MyHyperlinkListener());

    JScrollPane installedScrollPane = createTable();
    installTableActions(pasteTable);

    tablePanel.add(installedScrollPane);

    toolbarPanel.setLayout(new BorderLayout());
    actionToolbar = ActionManager.getInstance().createActionToolbar(PastebinBundle.message("action.toolbar.title"), getActionGroup(), true);
    final JComponent component = actionToolbar.getComponent();
    toolbarPanel.add(component, BorderLayout.WEST);
//    toolbarPanel.add(filter, BorderLayout.EAST);
  }

  private void installTableActions(final PasteTable pasteTable) {
    pasteTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        final Paste[] pastes = pasteTable.getSelectedObjects();
        updateCodePanel(pastes != null && pastes.length == 1 ? pastes[0] : null,
            filter.getFilter(), codePane);
        actionToolbar.updateActionsImmediately();
      }
    });
  }

  private void updateCodePanel(Paste paste, String filter, JTextPane codePane) {
    //TODO: assync
    codePane.setText(paste.getText());
  }

  public JPanel getMainPanel() {
    return main;
  }

  private JScrollPane createTable() {
    pasteModel = new PasteTableModel();
    pasteTable = new PasteTable(pasteModel);
    pasteTable.getTableHeader().setReorderingAllowed(false);
    pasteTable.setColumnWidth(PasteColumnInfo.COLUMN_HITS, 50);
    pasteTable.setColumnWidth(PasteColumnInfo.COLUMN_DATE, 60);

    return ScrollPaneFactory.createScrollPane(pasteTable);
  }

  private ActionGroup getActionGroup() {
    DefaultActionGroup actionGroup = new DefaultActionGroup();
    actionGroup.add(new RefreshAction());
    actionGroup.add(Separator.getInstance());
    actionGroup.add(new SubmitNewPasteAction());
    actionGroup.add(new FilterCategoryAction());
    return actionGroup;
  }

  private void initCodePanel() {
    final JPanel titlePanel = new JPanel(new BorderLayout());
    titlePanel.add(new JLabel(PastebinBundle.message("panel.code.title")), BorderLayout.LINE_START);
    titlePanel.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.CENTER);
    titlePanel.add(new CodePanelActionsToolBar(), BorderLayout.LINE_END);
    codePane = new JTextPane();
    final JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(codePane);
    codePanel.add(titlePanel, BorderLayout.NORTH);
    codePanel.add(scrollPane, BorderLayout.CENTER);
  }

  public static class MyHyperlinkListener implements HyperlinkListener {
    public void hyperlinkUpdate(HyperlinkEvent e) {
      if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
        JEditorPane pane = (JEditorPane) e.getSource();
        if (e instanceof HTMLFrameHyperlinkEvent) {
          HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
          HTMLDocument doc = (HTMLDocument) pane.getDocument();
          doc.processHTMLFrameHyperlinkEvent(evt);
        } else {
          URL url = e.getURL();
          if (url != null) {
            BrowserUtil.launchBrowser(url.toString());
          }
        }
      }
    }
  }

  private class FilterCategoryAction extends ComboBoxAction implements DumbAware {
    @Override
    public void update(AnActionEvent e) {
      super.update(e);
      final String category = pasteModel.getCategory();
      e.getPresentation().setText("Category: " + category);
    }

    @NotNull
    @Override
    protected DefaultActionGroup createPopupActionGroup(JComponent button) {
      final String[] availableCategories = pasteModel.getAvailableCategories();
      final DefaultActionGroup gr = new DefaultActionGroup();
      for (final String availableCategory : availableCategories) {
        gr.add(createFilterByCategoryAction(availableCategory));
      }
      return gr;
    }

    private AnAction createFilterByCategoryAction(final String availableCategory) {
      return new AnAction(availableCategory) {
        @Override
        public void actionPerformed(AnActionEvent e) {
          final String f = filter.getFilter().toLowerCase();
          pasteModel.setCategory(availableCategory, f);
        }
      };
    }
  }

  protected class RefreshAction extends DumbAwareAction {
    public RefreshAction() {
      super(PastebinBundle.message("reload.list", pasteModel.getCategory()),
          PastebinBundle.message("reload.list", pasteModel.getCategory()).toLowerCase(),
          AllIcons.Vcs.Refresh);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
      pasteModel.updateModel();
      filter.setFilter("");
    }
  }

  public class SubmitNewPasteAction extends DumbAwareAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
      PastebinUtil.submitPaste(anActionEvent.getProject(), "");
    }

    public SubmitNewPasteAction() {
      super(PastebinBundle.message("submit.title"), PastebinBundle.message("submit.title").toLowerCase(), PastebinUtil.ICON);
    }
  }
}
