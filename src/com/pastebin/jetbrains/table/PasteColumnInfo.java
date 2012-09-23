package com.pastebin.jetbrains.table;

import com.intellij.openapi.util.Comparing;
import com.intellij.util.text.DateFormatUtil;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.UIUtil;
import com.pastebin.jetbrains.Paste;
import com.pastebin.jetbrains.PastebinBundle;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Comparator;

/**
 * @author Yuri Denison
 * @date 9/18/12
 */
public class PasteColumnInfo extends ColumnInfo<Paste, String> {
  public static final int COLUMN_NAME = 0;
  public static final int COLUMN_DATE = 1;
  public static final int COLUMN_HITS = 2;

  public static final String[] COLUMNS = {
      PastebinBundle.message("column.title.name"),
      PastebinBundle.message("column.title.date"),
      PastebinBundle.message("column.title.hits"),
  };

  private final int columnIdx;
  private final PasteTableModel myModel;

  public PasteColumnInfo(int columnIdx, PasteTableModel model) {
    super(COLUMNS[columnIdx]);
    this.columnIdx = columnIdx;
    myModel = model;
  }

  @Nullable
  @Override
  public String valueOf(Paste paste) {
    switch (columnIdx) {
      case COLUMN_NAME:
        return paste.getName();
      case COLUMN_DATE:
        return DateFormatUtil.formatPrettyDate(paste.getDate() * 1000);
      case COLUMN_HITS:
        return String.valueOf(paste.getHits());
      default:
        return "";
    }
  }

  @Override
  public Class getColumnClass() {
    return columnIdx == COLUMN_HITS ? Integer.class : String.class;
  }

  @Nullable
  @Override
  public Comparator<Paste> getComparator() {
    switch (columnIdx) {
      case COLUMN_NAME:
        return new Comparator<Paste>() {
          @Override
          public int compare(Paste p1, Paste p2) {
            return Comparing.compare(p1.getName(), p2.getName());
          }
        };
      case COLUMN_DATE:
        return new Comparator<Paste>() {
          @Override
          public int compare(Paste p1, Paste p2) {
            return Comparing.compare(p1.getDate(), p2.getDate());
          }
        };
      case COLUMN_HITS:
        return new Comparator<Paste>() {
          @Override
          public int compare(Paste p1, Paste p2) {
            return Comparing.compare(p1.getHits(), p2.getHits());
          }
        };
      default:
        return null;
    }
  }

  @Nullable
  @Override
  public TableCellRenderer getRenderer(Paste paste) {
    return new PasteRenderer(paste, columnIdx);
  }

  private static class PasteRenderer extends DefaultTableCellRenderer {
    private final JLabel label = new JLabel();
    private final Paste paste;

    private final Font nameFont = UIUtil.getLabelFont();
    private final Font smallFont = UIUtil.getLabelFont(UIUtil.FontSize.SMALL);

    private PasteRenderer(final Paste paste, int column) {
      label.setFont(column == COLUMN_NAME ? nameFont : smallFont);
      label.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 2));
      this.paste = paste;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Component orig = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      final Color bg = orig.getBackground();
      final Color grayedFg = isSelected ? orig.getForeground() : Color.GRAY;
      label.setForeground(grayedFg);
      label.setBackground(bg);
      label.setOpaque(true);

      switch (column) {
        case COLUMN_NAME:
          label.setText(paste.getName());
          label.setHorizontalAlignment(SwingConstants.LEFT);
          break;
        case COLUMN_DATE:
          label.setText(DateFormatUtil.formatPrettyDate(paste.getDate() * 1000));
          label.setHorizontalAlignment(SwingConstants.RIGHT);
          break;
        case COLUMN_HITS:
          label.setText(paste.getHits() + "");
          label.setHorizontalAlignment(SwingConstants.RIGHT);
          break;
      }

      return label;
    }
  }
}
