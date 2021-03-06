package de.hansinator.fun.jgp.gui;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.RangeType;
import org.jfree.data.xy.XYSeriesCollection;

import de.hansinator.fun.jgp.simulation.WorldSimulation;

/**
 * 
 * @author hansinator
 */
public class BottomPanel extends JPanel
{

	public final SimulationInfoPanel infoPanel;

	public BottomPanel(WorldSimulation simulator, EvoStats evoStats)
	{
		infoPanel = new SimulationInfoPanel(simulator, evoStats);
		
		/*
		 * Food chart
		 */

		XYSeriesCollection xyDataset = new XYSeriesCollection(evoStats.fitnessChartData);
		JFreeChart foodChart = ChartFactory.createXYLineChart(null, null, null, xyDataset, PlotOrientation.VERTICAL,
				false, false, false);
		XYPlot xyPlot = foodChart.getXYPlot();

		NumberAxis axis = (NumberAxis) xyPlot.getRangeAxis();
		axis.setRangeType(RangeType.POSITIVE);
		axis.setDefaultAutoRange(new Range(0.0, 250.0));
		axis.setAutoRangeMinimumSize(250.0);
		axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		axis.setLowerMargin(0.0);
		axis.setUpperMargin(0.0);
		axis.setTickLabelFont(axis.getTickLabelFont().deriveFont(11.0f));

		axis = (NumberAxis) xyPlot.getDomainAxis();
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

		xyDataset = new XYSeriesCollection(evoStats.genomeSizeChartData);
		xyDataset.addSeries(evoStats.realGenomeSizeChartData);
		JFreeChart progSizeChart = ChartFactory.createXYLineChart(null, null, null, xyDataset,
				PlotOrientation.VERTICAL, false, false, false);
		xyPlot = progSizeChart.getXYPlot();

		axis = (NumberAxis) xyPlot.getRangeAxis();
		axis.setRangeType(RangeType.POSITIVE);
		axis.setDefaultAutoRange(new Range(0.0, 255.0));
		axis.setAutoRangeMinimumSize(255.0);
		axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		axis.setLowerMargin(0.0);
		axis.setUpperMargin(0.01);
		axis.setTickLabelFont(axis.getTickLabelFont().deriveFont(11.0f));

		axis = (NumberAxis) xyPlot.getDomainAxis();
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

		/*
		 * Info & Control panel
		 */

		JPanel groupPanel = new JPanel();
		groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.Y_AXIS));
		groupPanel.setAlignmentY(TOP_ALIGNMENT);
		groupPanel.setAlignmentX(LEFT_ALIGNMENT);
		groupPanel.add(infoPanel);
		groupPanel.add(new ControlPanel(simulator));

		/*
		 * Configure this panel
		 */

		JSplitPane graphPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, foodChartPanel, progSizeChartPanel);
		graphPane.setResizeWeight(0.5);
		setPreferredSize(new Dimension(0, 200));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(groupPanel);
		add(graphPane);
	}

}
