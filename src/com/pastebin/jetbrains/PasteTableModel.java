package com.pastebin.jetbrains;

import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.SortableColumnModel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Yuri Denison
 * @date 9/18/12
 */
public class PasteTableModel extends AbstractTableModel implements SortableColumnModel {
  public static final String[] categories = new String[]{
      PastebinBundle.message("category.trends"),
      PastebinBundle.message("category.user")
  };
  private static final int NAME_COLUMN = 0;
  private static final int LIMIT = 20;

  protected ColumnInfo[] columns;


  private final RowSorter.SortKey softKey;
  protected List<Paste> pastes;
  private String category;

  public PasteTableModel() {
    columns = new ColumnInfo[]{
        new PasteColumnInfo(PasteColumnInfo.COLUMN_NAME, this),
        new PasteColumnInfo(PasteColumnInfo.COLUMN_DATE, this),
        new PasteColumnInfo(PasteColumnInfo.COLUMN_HITS, this)
    };
    pastes = new ArrayList<Paste>();
    softKey = new RowSorter.SortKey(NAME_COLUMN, SortOrder.ASCENDING);
  }

  @Override
  public ColumnInfo[] getColumnInfos() {
    return columns;
  }

  @Override
  public void setSortable(boolean b) {
  }

  @Override
  public boolean isSortable() {
    return true;
  }


  public Paste getObjectAt(int row) {
    return pastes.get(row);
  }

  @Override
  public Object getRowValue(int i) {
    return getObjectAt(i);
  }

  @Nullable
  @Override
  public RowSorter.SortKey getDefaultSortKey() {
    return softKey;
  }

  @Override
  public int getRowCount() {
    return pastes.size();
  }

  @Override
  public int getColumnCount() {
    return columns.length;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    return columns[columnIndex].valueOf(getObjectAt(rowIndex));
  }

  // TODO: implement filters
  protected void sort() {
    Collections.sort(pastes, columns[NAME_COLUMN].getComparator());
    fireTableDataChanged();
  }

  public void updateModel() {
    try {
      if (category.equals(categories[0])) {
        pastes = PastebinUtil.getTrendPasteList();
      } else if (category.equals(categories[1])) {
        pastes = PastebinUtil.getUserPasteList(LIMIT);
      }
    } catch (PastebinException e) {
      PastebinUtil.showNotification(PastebinBundle.message("failure"), e.getMessage(), false);
    }
  }

  public String[] getAvailableCategories() {
    return categories;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category, String filter) {
    this.category = category;
  }

}
