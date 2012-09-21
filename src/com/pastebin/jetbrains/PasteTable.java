package com.pastebin.jetbrains;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ui.TableUtil;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ui.ColumnInfo;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * @author Yuri Denison
 * @date 9/18/12
 */
public class PasteTable extends JBTable {
  public PasteTable(final PasteTableModel model) {
    super(model);
    getColumnModel().setColumnMargin(0);
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    setShowGrid(false);
    setStriped(true);
  }

  public void setColumnWidth(final int columnIndex, final int width) {
    TableColumn column = getColumnModel().getColumn(columnIndex);
    column.setMinWidth(width);
    column.setMaxWidth(width);
  }

  @Override
  protected TableRowSorter<TableModel> createRowSorter(TableModel model) {
    return new DefaultColumnInfoBasedRowSorter(model);
  }

  @Override
  protected boolean isSortOnUpdates() {
    return false;
  }

  public void setValueAt(final Object aValue, final int row, final int column) {
    super.setValueAt(aValue, row, column);
    repaint(); //in order to update invalid plugins
  }

  public TableCellRenderer getCellRenderer(final int row, final int column) {
    final ColumnInfo columnInfo = ((PasteTableModel) getModel()).getColumnInfos()[column];
    return columnInfo.getRenderer(getObjectAt(row));
  }

  public Object[] getElements() {
    return ((PasteTableModel) getModel()).pastes.toArray();
  }

  public Paste getObjectAt(int row) {
    return ((PasteTableModel) getModel()).getObjectAt(convertRowIndexToModel(row));
  }

  public void select(IdeaPluginDescriptor... descriptors) {
    PasteTableModel tableModel = (PasteTableModel) getModel();
    getSelectionModel().clearSelection();
    for (int i = 0; i < tableModel.getRowCount(); i++) {
      Paste pasteAt = tableModel.getObjectAt(i);
      if (ArrayUtil.find(descriptors, pasteAt) != -1) {
        final int row = convertRowIndexToView(i);
        getSelectionModel().addSelectionInterval(row, row);
      }
    }
    TableUtil.scrollSelectionToVisible(this);
  }

  public Paste getSelectedObject() {
    Paste selected = null;
    if (getSelectedRowCount() > 0) {
      selected = getObjectAt(getSelectedRow());
    }
    return selected;
  }
}
