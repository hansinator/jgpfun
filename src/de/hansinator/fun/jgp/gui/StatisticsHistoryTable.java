package de.hansinator.fun.jgp.gui;

import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

/**
 * 
 * @author hansinator
 */
public class StatisticsHistoryTable extends JTable
{

	public StatisticsHistoryTable(StatisticsHistoryModel model)
	{
		super(model);
		setColumnSelectionAllowed(false);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		FontMetrics fm = getFontMetrics(getFont());
		for (Enumeration<TableColumn> cols = columnModel.getColumns(); cols.hasMoreElements();)
		{
			final TableColumn col = cols.nextElement();
			col.setPreferredWidth(SwingUtilities.computeStringWidth(fm, col.getHeaderValue().toString()) + 10);
		}
	}

	private static class StatisticsHistoryEntry
	{

		private final int generation;

		private final int totalFood, averageFood;

		private final int averageProgramSize, averageRealProgramSize;

		private StatisticsHistoryEntry(int generation, int totalFood, int averageFood, int averageProgramSize,
				int averageRealProgramSize)
		{
			this.generation = generation;
			this.totalFood = totalFood;
			this.averageFood = averageFood;
			this.averageProgramSize = averageProgramSize;
			this.averageRealProgramSize = averageRealProgramSize;
		}

		private int getValueAt(int columnIndex)
		{
			switch (columnIndex)
			{
				case 0:
					return generation;
				case 1:
					return totalFood;
				case 2:
					return averageFood;
				case 3:
					return averageProgramSize;
				case 4:
					return averageRealProgramSize;
				default:
					throw new ArrayIndexOutOfBoundsException(columnIndex);
			}
		}

	}


	public static class StatisticsHistoryModel extends AbstractTableModel
	{

		private final ArrayList<StatisticsHistoryEntry> list = new ArrayList<StatisticsHistoryEntry>();

		private String[] columnNames = { "Gen", "Food", "Avg Food", "Avg Prg", "Avg Real Prg" };

		public void appendEntry(int generation, int totalFood, int averageFood, int averageProgramSize,
				int averageRealProgramSize)
		{
			list.add(new StatisticsHistoryEntry(generation, totalFood, averageFood, averageProgramSize,
					averageRealProgramSize));
			fireTableRowsInserted(0, 1);
		}

		public void clear()
		{
			int size = list.size();
			list.clear();
			fireTableRowsDeleted(0, size);
		}

		@Override
		public int getRowCount()
		{
			return list.size();
		}

		@Override
		public int getColumnCount()
		{
			return columnNames.length;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex)
		{
			return Integer.class;
		}

		@Override
		public String getColumnName(int column)
		{
			return columnNames[column];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			return list.get(list.size() - 1 - rowIndex).getValueAt(columnIndex);
		}

	}
}
