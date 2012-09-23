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
import com.intellij.util.concurrency.SwingWorker;
import com.pastebin.jetbrains.Paste;
import com.pastebin.jetbrains.PastebinBundle;
import com.pastebin.jetbrains.PastebinException;
import com.pastebin.jetbrains.PastebinSettings;
import com.pastebin.jetbrains.table.PasteColumnInfo;
import com.pastebin.jetbrains.table.PasteTable;
import com.pastebin.jetbrains.table.PasteTableModel;
import com.pastebin.jetbrains.util.PastebinUtil;
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
 * @date 19.09.12
 */
public class PasteTablePanel extends JPanel {
  private static final int HITS_WIDTH = 70;
  private static final int DATE_WIDTH = 80;
  private static final int NAME_WIRTH = 200;
  private JPanel tablePanel;
  private JPanel codePanel;
  private JPanel toolbarPanel;

  private JTextPane codePane;
  private ActionToolbar actionToolbar;
  private Component codeActionsComponent;

  protected PasteTable pasteTable;
  protected PasteTableModel pasteModel;
  private FilterComponent filter;

  public PasteTablePanel() {
    super(new BorderLayout());
    toolbarPanel = new JPanel(new BorderLayout());
    add(toolbarPanel, BorderLayout.NORTH);
    tablePanel = new JPanel(new BorderLayout());
    tablePanel.setPreferredSize(new Dimension(HITS_WIDTH + DATE_WIDTH + NAME_WIRTH, -1));
    codePanel = new JPanel(new BorderLayout());
    add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tablePanel, codePanel), BorderLayout.CENTER);

    GuiUtils.replaceJSplitPaneWithIDEASplitter(this);
    initCodePanel();

    codePane.addHyperlinkListener(new MyHyperlinkListener());

    JScrollPane installedScrollPane = createTable();
    installTableActions(pasteTable);

    tablePanel.add(installedScrollPane, BorderLayout.CENTER);

    toolbarPanel.setLayout(new BorderLayout());
    actionToolbar = ActionManager.getInstance().createActionToolbar(PastebinBundle.message("action.toolbar.title"), getActionGroup(), true);
    final JComponent component = actionToolbar.getComponent();
    toolbarPanel.add(component, BorderLayout.WEST);
//    toolbarPanel.add(filter, BorderLayout.EAST);
  }

  private void installTableActions(final PasteTable pasteTable) {
    pasteTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        final Paste paste = pasteTable.getSelectedObject();
        updateCodePanel(paste, codePane);
        actionToolbar.updateActionsImmediately();
      }
    });
  }

  private void updateCodePanel(final Paste paste, final JTextPane codePane) {
    if (paste == null) {
      return;
    }
    setDownloadStatus(true);
    new SwingWorker() {
      private String text = "";

      @Override
      public Object construct() {
        codeActionsComponent = updateCodePanelActionsToolBar();
        codeActionsComponent.repaint();
        text = paste.getText();
        return text;
      }

      @Override
      public void finished() {
        codePane.setText(text);
        setDownloadStatus(false);
      }
    }.start();
  }

  private JScrollPane createTable() {
    pasteModel = new PasteTableModel();
    pasteTable = new PasteTable(pasteModel);
    pasteTable.getTableHeader().setReorderingAllowed(false);
    pasteTable.setColumnWidth(PasteColumnInfo.COLUMN_NAME, NAME_WIRTH);
    pasteTable.setColumnWidth(PasteColumnInfo.COLUMN_HITS, HITS_WIDTH);
    pasteTable.setColumnWidth(PasteColumnInfo.COLUMN_DATE, DATE_WIDTH);
//    pasteTable.setColumnWidth(PasteColumnInfo.COLUMN_NAME, 200);

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

  protected void setDownloadStatus(boolean status) {
    pasteTable.setPaintBusy(status);
  }

  private void initCodePanel() {
    final JPanel titlePanel = new JPanel(new BorderLayout());
    titlePanel.add(new JLabel(PastebinBundle.message("panel.code.title")), BorderLayout.LINE_START);
    titlePanel.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.CENTER);
    codeActionsComponent = updateCodePanelActionsToolBar();
    titlePanel.add(codeActionsComponent, BorderLayout.LINE_END);
    codePane = new JTextPane();
    final JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(codePane);
    codePanel.add(titlePanel, BorderLayout.NORTH);
    codePanel.add(scrollPane, BorderLayout.CENTER);
  }

  public Component updateCodePanelActionsToolBar() {
    final DefaultActionGroup group = new DefaultActionGroup();
    group.add(new LaunchBrowserAction());
    group.add(new DeletePasteAction());
    codeActionsComponent = ActionManager.getInstance().createActionToolbar(PastebinBundle.message("paste.toolbar.title"), group, true).getComponent();
    return codeActionsComponent;
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
//          final String f = filter.getFilter().toLowerCase();
          setDownloadStatus(true);
          new SwingWorker() {
            @Override
            public Object construct() {
              pasteModel.setCategory(availableCategory);
              pasteModel.updateModel();
              return null;
            }

            @Override
            public void finished() {
              codePane.setText("");
              pasteModel.fireTableDataChanged();
              setDownloadStatus(false);
            }
          }.start();
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
      setDownloadStatus(true);
      new SwingWorker() {
        @Override
        public Object construct() {
          pasteModel.updateModel();
          codePane.setText("");
          updateCodePanelActionsToolBar();
          return null;
        }

        @Override
        public void finished() {
          pasteModel.fireTableDataChanged();
          setDownloadStatus(false);
        }
      }.start();
//      filter.setFilter("");
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

  private class DeletePasteAction extends DumbAwareAction {
    private DeletePasteAction() {
      super(PastebinBundle.message("delete.paste"), PastebinBundle.message("delete.paste").toLowerCase(), AllIcons.Actions.Delete);
    }

    @Override
    public void update(AnActionEvent e) {
      super.update(e);
      e.getPresentation().setEnabled(pasteModel.getCategory().equals(PasteTableModel.categories[1]) && pasteTable.getSelectedRowCount() != 0);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
      setDownloadStatus(true);
      new SwingWorker() {
        @Override
        public Object construct() {
          final Paste paste = pasteTable.getSelectedObject();
          try {
            final boolean res = PastebinUtil.deletePaste(paste.getKey(), PastebinSettings.getInstance().getLoginId());
            final String title = res ? PastebinBundle.message("success") : PastebinBundle.message("failure");
            final String message = res ? PastebinBundle.message("paste.deleted") : PastebinBundle.message("network.error");
            PastebinUtil.showNotification(title, message, res);
          } catch (PastebinException e) {
            PastebinUtil.showNotification(PastebinBundle.message("failure"), e.getMessage(), false);
          }
          pasteModel.updateModel();
          return null;
        }

        @Override
        public void finished() {
          setDownloadStatus(false);
        }
      }.start();
    }
  }

  private class LaunchBrowserAction extends DumbAwareAction {
    private LaunchBrowserAction() {
      super(PastebinBundle.message("browser.action"), PastebinBundle.message("browser.action").toLowerCase(), AllIcons.Xml.Browsers.Explorer16);
    }

    @Override
    public void update(AnActionEvent e) {
      super.update(e);
      e.getPresentation().setEnabled(pasteTable.getSelectedRowCount() != 0);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
      BrowserUtil.launchBrowser(pasteTable.getSelectedObject().getUrl());
    }
  }
}
