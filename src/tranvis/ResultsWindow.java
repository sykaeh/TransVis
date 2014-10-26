package tranvis;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.ui.TextAnchor;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Class that displays the results (i.e. the graph, the list of sources
 * and the statistics) of the Translation Process Visualizer.
 *
 * @author Sybil Ehrensberger
 * @version 0.3
 */
public class ResultsWindow extends javax.swing.JFrame {

    private List<Color> colorlist = new LinkedList<Color>();

    /**
     * Creates new form ResultsWindow
     */
    public ResultsWindow() {
        initComponents();
        colorlist.add(Color.BLACK);
        colorlist.add(Color.RED);
        colorlist.add(Color.BLUE);
        colorlist.add(Color.GREEN);
        colorlist.add(Color.MAGENTA);
        colorlist.add(Color.CYAN);
        colorlist.add(Color.ORANGE);
        colorlist.add(Color.GRAY);
        colorlist.add(Color.PINK);

    }

    /**
     * Sets the given string in the field for the name.
     *
     * @param name string to be displayed.
     */
    public void setNameField(String name) {
        processNameField.setText(name);
    }

    public void setInfo(String info) {
        importantInfo.setText(info);
    }

    /**
     * Displays the given chart and adds the labels.
     *
     * @param chart the chart to be displayed.
     * @param list  the labels to be added
     */
    public void drawGraph(JFreeChart chart, List<Object[]> list,
                          List<String> processes, double ymax) {

        //chart.removeLegend();

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
                c = colorlist.get(n);
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
        }
        plot.setRenderer(renderer);
        plot.setFixedLegendItems(chartLegend);

        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new java.awt.Dimension(900, 630));
        panel.setVisible(true);

        jInternalFrame1.setContentPane(panel);
        jInternalFrame1.pack();
        jInternalFrame1.setVisible(true);
    }

    private Color getRandomColor() {

        Random numGen = new Random();
        Color new_color = new Color(numGen.nextInt(256), numGen.nextInt(256), numGen.nextInt(256));
        int n = 0;
        while (colorlist.contains(new_color) && n < 25) {
            new_color = new Color(numGen.nextInt(256), numGen.nextInt(256), numGen.nextInt(256));
            n++;
        }
        colorlist.add(new_color);
        return new_color;
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jInternalFrame1 = new javax.swing.JInternalFrame();
        jScrollPane1 = new javax.swing.JScrollPane();
        processNameField = new javax.swing.JTextArea();
        importantInfo = new javax.swing.JLabel();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(tranvis.MainApp.class).getContext().getResourceMap(ResultsWindow.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N
        setSize(new java.awt.Dimension(1250, 550));

        jInternalFrame1.setBorder(null);
        jInternalFrame1.setName("jInternalFrame1"); // NOI18N
        jInternalFrame1.setVisible(true);

        org.jdesktop.layout.GroupLayout jInternalFrame1Layout = new org.jdesktop.layout.GroupLayout(jInternalFrame1.getContentPane());
        jInternalFrame1.getContentPane().setLayout(jInternalFrame1Layout);
        jInternalFrame1Layout.setHorizontalGroup(
                jInternalFrame1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(0, 924, Short.MAX_VALUE)
        );
        jInternalFrame1Layout.setVerticalGroup(
                jInternalFrame1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(0, 603, Short.MAX_VALUE)
        );

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        processNameField.setBackground(resourceMap.getColor("processNameField.background")); // NOI18N
        processNameField.setColumns(5);
        processNameField.setEditable(false);
        processNameField.setLineWrap(true);
        processNameField.setRows(2);
        processNameField.setName("processNameField"); // NOI18N
        jScrollPane1.setViewportView(processNameField);

        importantInfo.setText(resourceMap.getString("importantInfo.text")); // NOI18N
        importantInfo.setName("importantInfo"); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(layout.createSequentialGroup()
                                .addContainerGap()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(importantInfo)
                                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 924, Short.MAX_VALUE)
                                        .add(jInternalFrame1))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(layout.createSequentialGroup()
                                .addContainerGap()
                                .add(importantInfo)
                                .add(18, 18, 18)
                                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 66, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(18, 18, 18)
                                .add(jInternalFrame1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(48, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel importantInfo;
    private javax.swing.JInternalFrame jInternalFrame1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea processNameField;
    // End of variables declaration//GEN-END:variables
}
