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
import java.util.*;
import java.util.List;

/**
 * @author Sybil Ehrensberger
 */
public class ResultView {
    private JLabel general_info;
    private JList processes_list;
    private JPanel results_panel;
    private JTextPane transcript_list;

    private JFrame results_frame;

    private static List<Color> colors = Arrays.asList(Color.BLACK, Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA,
            Color.CYAN, Color.ORANGE, Color.GRAY, Color.PINK);

    /**
     * Public constructor for a ResultView
     */
    public ResultView(GraphType g, String phase) {

        results_frame = new JFrame("ResultView");
        results_frame.setContentPane(results_panel);
        results_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        setInfo(g.name() + ": " + phase);
        results_frame.setTitle(g.name() + ": " + phase);

    }

    /**
     * Set the general info for the graph displayed.
     *
     * @param info the info text to be displayed
     */
    public void setInfo(String info) {
        general_info.setText(info);
    }


    public void drawGraph(XYSeriesCollection data, List<Object[]> list,
                          List<String> processes, double ymax) {

        JFreeChart chart = ChartFactory.createXYLineChart(
                null, "Time [in sec]", null, data,
                PlotOrientation.VERTICAL, true, false, false);

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.lightGray);
        ValueAxis yax = plot.getRangeAxis();
        yax.setRange(0, ymax - 0.5);
        yax.setVisible(false);

        ValueAxis xax = plot.getDomainAxis();
        xax.setRange(0, xax.getUpperBound() + 1);

        XYTextAnnotation annot;
        for (Object[] o : list) {
            String text = (String) o[0];
            double ypos = (Integer) o[1];

            annot = new XYTextAnnotation(text, 5, ypos);
            annot.setTextAnchor(TextAnchor.CENTER_LEFT);
            annot.setFont(new Font("SansSerif", Font.PLAIN, 12));
            plot.addAnnotation(annot);
        }

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        float lineWidth = 2.5f;
        BasicStroke stroke = new BasicStroke(lineWidth);


        LegendItemCollection chartLegend = new LegendItemCollection();
        Shape shape = new Rectangle(10, 2);

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

        System.out.println(start_tag + name + end_tag);
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        results_panel = new JPanel();
        results_panel.setLayout(new GridLayoutManager(2, 2, new Insets(10, 10, 10, 10), -1, -1));
        final JScrollPane scrollPane1 = new JScrollPane();
        results_panel.add(scrollPane1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(300, -1), new Dimension(300, -1), new Dimension(300, -1), 0, false));
        transcript_list = new JTextPane();
        transcript_list.setContentType("text/plain");
        transcript_list.setEditable(false);
        transcript_list.setText("");
        scrollPane1.setViewportView(transcript_list);
        general_info = new JLabel();
        general_info.setFont(new Font(general_info.getFont().getName(), Font.BOLD, 20));
        general_info.setText("Main Graph: Complete Process");
        results_panel.add(general_info, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final JLabel label1 = new JLabel();
        label1.setText("List of all Processes used:");
        results_panel.add(label1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return results_panel;
    }
}
