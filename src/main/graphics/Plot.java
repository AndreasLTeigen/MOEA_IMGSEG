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

    public static void PlotObjectiveValues(List<Chromosome> chroms) {
        int width = 400, height = 400;
        XYSeries d = new XYSeries("data");
        chroms.forEach(c -> d.add(c.objectiveValues.get(0), c.objectiveValues.get(1)));

        XYSeriesCollection data = new XYSeriesCollection();
        data.addSeries(d);

        JFreeChart chart = ChartFactory.createScatterPlot("Generations", "overall-deviation", "connectivity", data);

        ChartPanel panel = new ChartPanel(chart);
        JFrame frame = new JFrame("aaa");

        frame.setSize(width, height);

        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }
}
