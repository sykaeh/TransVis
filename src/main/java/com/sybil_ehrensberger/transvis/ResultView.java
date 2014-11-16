package com.sybil_ehrensberger.transvis;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.jfree.chart.*;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.TextAnchor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author Sybil Ehrensberger
 */
public class ResultView {
    private static List<Color> colors = new ArrayList<>();

    private JLabel general_info;
    private JPanel results_panel;
    private JTextPane transcript_list;
    private JFrame results_frame;
    private String x_axis_label;

    /**
     * Public constructor for a ResultView
     *
     * @param g     the graph type to be displayed
     * @param phase the selected phase (i.e. orientation, drafting)
     * @param xAxis the label for the x-Axis
     */
    public ResultView(GraphType g, String phase, String xAxis) {

        results_frame = new JFrame("ResultView");
        results_frame.setContentPane(results_panel);
        results_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Time [in sec]
        x_axis_label = xAxis + " [in sec]";
        setInfo(g.name() + ": " + phase);
        results_frame.setTitle(g.name() + ": " + phase);

        colors.addAll(Arrays.asList(Color.BLACK, Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA,
                Color.CYAN, Color.ORANGE, Color.GRAY, Color.PINK));

    }

    /**
     * Set the general info for the graph displayed.
     *
     * @param info the info text to be displayed
     */
    public void setInfo(String info) {
        general_info.setText(info);
    }


    /**
     * Generate the xy plot and diplay it in the results window
     *
     * @param data      the dataset
     * @param list      the annotations for the different types
     * @param processes list of all of the transcripts used
     * @param ymax      the maximum y value
     */
    public void drawGraph(XYSeriesCollection data, List<Object[]> list,
                          List<String> processes, double ymax) {

        JFreeChart chart = ChartFactory.createXYLineChart(
                null, // title
                x_axis_label, // x-axis label
                null, // y-axis label
                data, // dataset
                PlotOrientation.VERTICAL, // orientation
                true, // legend
                false, // tooltips
                false); //urls

        chart.setTextAntiAlias(true);

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.lightGray);

        // set y-axis
        ValueAxis yax = plot.getRangeAxis();
        yax.setRange(0, ymax - 0.5);
        yax.setVisible(false);

        // set x-axis
        ValueAxis xax = plot.getDomainAxis();
        xax.setRange(0, xax.getUpperBound() + 1);
        //xax.setLabelFont(new Font("Dialog", Font.BOLD, 14));
        //xax.setTickLabelFont(new Font("Dialog", Font.TRUETYPE_FONT, 12));

        XYTextAnnotation annot;
        for (Object[] o : list) {
            String text = (String) o[0];
            double ypos = (Integer) o[1];

            annot = new XYTextAnnotation(text, 5, ypos);
            annot.setTextAnchor(TextAnchor.CENTER_LEFT);
            annot.setFont(new Font("Dialog", Font.BOLD, 14));
            plot.addAnnotation(annot);
        }


        float lineWidth = 2.5f;
        BasicStroke stroke = new BasicStroke(lineWidth);

        LegendItemCollection chartLegend = new LegendItemCollection();
        Shape shape = new Rectangle(10, 2);

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        Color c;
        for (int n = 0; n < processes.size(); n++) {
            if (n < 9) {
                c = colors.get(n);
            } else {
                c = getRandomColor();
            }
            for (int i = 0; i < list.size(); i++) {
                int seriesnum = n * list.size() + i;
                renderer.setSeriesShapesVisible(seriesnum, false);

                renderer.setSeriesPaint(seriesnum, c);
                renderer.setSeriesStroke(seriesnum, stroke);

            }
            chartLegend.add(new LegendItem(processes.get(n), null, null, null, shape, c));
            addProcessName(processes.get(n), c);
        }
        plot.setRenderer(renderer);
        plot.setFixedLegendItems(chartLegend);

        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        display(chart);

    }

    private void display(JFreeChart chart) {

        ChartPanel graph_panel = new ChartPanel(chart);
        graph_panel.setPreferredSize(new Dimension(700, 600));
        graph_panel.setVisible(true);

        graph_panel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        results_panel.add(graph_panel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                new Dimension(700, 600), new Dimension(700, 600), null, 1, false));
        // new Dimension(width, height)

        results_frame.pack();
        results_frame.setVisible(true);
    }

    private void addProcessName(String name, Color c) {

        String start_tag = "<span style=\"rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")\">";
        String end_tag = "</span><br>";

        //transcript_list.setText(transcript_list.getText() + start_tag + name + end_tag);
        transcript_list.setText(transcript_list.getText() + name + "\n");

    }

    private Color getRandomColor() {

        Random numGen = new Random();
        Color new_color = new Color(numGen.nextInt(256), numGen.nextInt(256), numGen.nextInt(256));
        int n = 0;
        while (colors.contains(new_color) && n < 25) {
            new_color = new Color(numGen.nextInt(256), numGen.nextInt(256), numGen.nextInt(256));
            n++;
        }
        colors.add(new_color);
        return new_color;
    }

}
