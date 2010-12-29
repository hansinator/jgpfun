package jgpfun.gui;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTable;
import jgpfun.gui.StatisticsHistoryTable.StatisticsHistoryModel;

/**
 *
 * @author hansinator
 */
public class StatisticsHistoryPanel extends JPanel {

    public StatisticsHistoryPanel(StatisticsHistoryModel statisticsHistory) {
        setLayout(new BorderLayout());

        JTable historyTable = new StatisticsHistoryTable(statisticsHistory);
        historyTable.setFillsViewportHeight(true);
        add(historyTable.getTableHeader(), BorderLayout.PAGE_START);
        add(historyTable, BorderLayout.CENTER);
    }

}
