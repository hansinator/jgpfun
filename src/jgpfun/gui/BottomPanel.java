package jgpfun.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
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

    public BottomPanel(ActionListener speedListener, XYSeries chartData) {
        XYSeriesCollection xyDataset = new XYSeriesCollection(chartData);
        JFreeChart chart = ChartFactory.createXYLineChart(null, null, null, xyDataset, PlotOrientation.VERTICAL, false, false, false);
        XYPlot xyPlot = chart.getXYPlot();
        
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

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(0, 200));
        chartPanel.setMaximumDrawWidth(2000);

        setPreferredSize(new Dimension(0, 200));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(chartPanel);
        add(infoPanel);
        add(new ControlPanel(speedListener));
    }

}
