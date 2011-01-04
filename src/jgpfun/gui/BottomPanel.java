package jgpfun.gui;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.RangeType;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author hansinator
 */
public class BottomPanel extends JPanel {

    public final InfoPanel infoPanel = new InfoPanel();

    public BottomPanel(ActionListener speedListener, XYSeries foodChartData, XYSeries progSizeChartData, XYSeries realProgSizeChartData) {
        /*
         * Food chart
         */

        XYSeriesCollection xyDataset = new XYSeriesCollection(foodChartData);
        JFreeChart foodChart = ChartFactory.createXYLineChart(null, null, null, xyDataset, PlotOrientation.VERTICAL, false, false, false);
        XYPlot xyPlot = foodChart.getXYPlot();
        
        NumberAxis axis = (NumberAxis)xyPlot.getRangeAxis();
        axis.setRangeType(RangeType.POSITIVE);
        axis.setDefaultAutoRange(new Range(0.0, 250.0));
        axis.setAutoRangeMinimumSize(250.0);
        axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        axis.setLowerMargin(0.0);
        axis.setUpperMargin(0.0);
        axis.setTickLabelFont(axis.getTickLabelFont().deriveFont(11.0f));

        axis = (NumberAxis)xyPlot.getDomainAxis();
        axis.setRangeType(RangeType.POSITIVE);
        axis.setDefaultAutoRange(new Range(0.0, 500.0));
        axis.setAutoRangeMinimumSize(500.0);
        axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        axis.setLowerMargin(0.0);
        axis.setUpperMargin(0.0);
        axis.setTickLabelFont(axis.getTickLabelFont().deriveFont(11.0f));

        ChartPanel foodChartPanel = new ChartPanel(foodChart);
        foodChartPanel.setPreferredSize(new Dimension(0, 200));
        foodChartPanel.setMaximumDrawWidth(2000);

        /*
         * Prog size chart
         */

        xyDataset = new XYSeriesCollection(progSizeChartData);
        xyDataset.addSeries(realProgSizeChartData);
        JFreeChart progSizeChart = ChartFactory.createXYLineChart(null, null, null, xyDataset, PlotOrientation.VERTICAL, false, false, false);
        xyPlot = progSizeChart.getXYPlot();

        axis = (NumberAxis)xyPlot.getRangeAxis();
        axis.setRangeType(RangeType.POSITIVE);
        axis.setDefaultAutoRange(new Range(0.0, 255.0));
        axis.setAutoRangeMinimumSize(255.0);
        axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        axis.setLowerMargin(0.0);
        axis.setUpperMargin(1.0);
        axis.setTickLabelFont(axis.getTickLabelFont().deriveFont(11.0f));

        axis = (NumberAxis)xyPlot.getDomainAxis();
        axis.setRangeType(RangeType.POSITIVE);
        axis.setDefaultAutoRange(new Range(0.0, 500.0));
        axis.setAutoRangeMinimumSize(500.0);
        axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        axis.setLowerMargin(0.0);
        axis.setUpperMargin(0.0);
        axis.setTickLabelFont(axis.getTickLabelFont().deriveFont(11.0f));

        ChartPanel progSizeChartPanel = new ChartPanel(progSizeChart);
        foodChartPanel.setPreferredSize(new Dimension(0, 200));
        foodChartPanel.setMaximumDrawWidth(2000);

        JPanel groupPanel = new JPanel();
        groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.Y_AXIS));
        groupPanel.setAlignmentY(TOP_ALIGNMENT);
        groupPanel.setAlignmentX(LEFT_ALIGNMENT);
        groupPanel.setPreferredSize(new Dimension(132, 200));
        groupPanel.add(infoPanel);
        groupPanel.add(new ControlPanel(speedListener));

        setPreferredSize(new Dimension(0, 200));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(groupPanel);
        add(foodChartPanel);
        add(progSizeChartPanel);
    }

}
