package  graphics;

import imgseg_representation.Chromosome;
import javafx.scene.chart.ScatterChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Plot {
    private int width = 400, height = 400;


    private XYSeriesCollection series = new XYSeriesCollection();
    private JFreeChart chart;

    private ChartPanel panel;
    private JFrame frame;

    private int frontInd = 0;

    public Plot() {

        chart = ChartFactory.createScatterPlot("Generations", "overall-deviation", "connectivity", series);
        panel = new ChartPanel(chart);

        frame = new JFrame("aaa");
        frame.setSize(width, height);

        frame.getContentPane().add(panel);

        frame.setVisible(true);
    }

    public void addParetoFront(List<Chromosome> front) {
        XYSeries d = new XYSeries(frontInd++);
        front.forEach(c -> d.add(c.objectiveValues.get(0), c.objectiveValues.get(1)));
        series.addSeries(d);
        //panel.repaint();
    }


    public static void PlotObjectiveValues(List<Chromosome> chroms) {
        Plot p = new Plot();
        p.addParetoFront(chroms);
    }
}
