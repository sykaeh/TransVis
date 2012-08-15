/*
 * TranVis.java
 * 
 * Translation process Visualizer
 * 
 */
package transcriptvisualizer;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * The main class of the application.
 * @author Sybil Ehrensberger
 * @version 0.3
 */
public class MainApp extends SingleFrameApplication {
    
    private String info;

    private static MainView view;

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        view = new MainView(this);
        show(view);
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     * @param root 
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of TranscriptVisualizerApp
     */
    public static MainApp getApplication() {
        return Application.getInstance(MainApp.class);
    }

    /**
     * Main method launching the application.
     * @param args 
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        launch(MainApp.class, args);
    }

    /**
     * Method invoked when the "Display main graph" button is clicked.
     */
    @Action
    public void displayMainGraphClicked() {

        if (view.fileList.isEmpty()) {
            view.reportError("No files selected.");
        } else {
            List<XMLParser> parsers = parseFiles();
            if (view.getIndividualGraphs() && view.fileList.size() > 1) {
                for (XMLParser p : parsers) {
                    List<XMLParser> plist = new LinkedList<XMLParser>();
                    plist.add(p);
                    ResultsWindow r = new ResultsWindow();
                    displayMainGraph(plist, r);
                    r.setInfo("Main graph: " + info);
                    r.setVisible(true);
                }
            }
            ResultsWindow results = new ResultsWindow();
            displayMainGraph(parsers, results);
            results.setInfo("Main graph: " + info);
            results.setVisible(true);
        }
    }

    /**
     * Iterate through the list of files and parse each file.
     * @return a List of XMLParsers
     */
    private List<XMLParser> parseFiles() {
        
        int start = view.getStart();
        int end = view.getEnd();
        int stats = view.getStat();
        
        int type = 0;
        info = "Partial view (" + start + "sec - " + end + "sec)";
        if (view.getComplete()) {
            info = "Complete process";
            type = 1;
        } else if (view.getOrientationPhase()) {
            info = "Orientation phase";
            type = 2;
        } else if (view.getDraftingPhase()) {
            info = "Drafting phase";
            type = 3;
        } else if (view.getRevisionPhase()) {
            info = "Revision phase";
            type = 4;
        }
        
        List<XMLParser> parsers = new LinkedList<XMLParser>();
        for (File f : view.fileList) {
            XMLParser p = analyzeFile(f, type, start, end, stats);
            if (p != null) {
                parsers.add(p);
            }
        }
        return parsers;
    }

    /**
     * Method invoked when the "Display subgraph consults" button is clicked.
     */
    @Action
    public void displayConsultsGraphClicked() {

        if (view.fileList.isEmpty()) {
            view.reportError("No files selected.");
        } else {
            List<XMLParser> parsers = parseFiles();
            if (view.getIndividualGraphs() && view.fileList.size() > 1) {
                for (XMLParser p : parsers) {
                    List<XMLParser> plist = new LinkedList<XMLParser>();
                    plist.add(p);
                    ResultsWindow r = new ResultsWindow();
                    displayConsultsGraph(plist, r);
                    r.setInfo("Consults graph: " + info);
                    r.setVisible(true);
                }
            }
            ResultsWindow results = new ResultsWindow();
            displayConsultsGraph(parsers, results);
            results.setInfo("Consults graph: " + info);
            results.setVisible(true);
        }
    }

    /**
     * Method invoked when the "Display subgraph revisions" button is clicked.
     */
    @Action
    public void displayRevisionsGraphClicked() {
        if (view.fileList.isEmpty()) {
            view.reportError("No files selected.");
        } else {
            List<XMLParser> parsers = parseFiles();
            if (view.getIndividualGraphs() && view.fileList.size() > 1) {
                for (XMLParser p : parsers) {
                    List<XMLParser> plist = new LinkedList<XMLParser>();
                    plist.add(p);
                    ResultsWindow r = new ResultsWindow();
                    displayRevisionGraph(plist, r);
                    r.setInfo("Revision graph: " + info);
                    r.setVisible(true);
                }
            }
            ResultsWindow results = new ResultsWindow();
            displayRevisionGraph(parsers, results);
            results.setInfo("Revision graph: " + info);
            results.setVisible(true);
        }

    }

    /**
     * Method invoked when the "Display subgraph pauses" button is clicked.
     */
    @Action
    public void displayPausesGraphClicked() {
        if (view.fileList.isEmpty()) {
            view.reportError("No files selected.");
        } else {
            List<XMLParser> parsers = parseFiles();
            if (view.getIndividualGraphs() && view.fileList.size() > 1) {
                for (XMLParser p : parsers) {
                    List<XMLParser> plist = new LinkedList<XMLParser>();
                    plist.add(p);
                    ResultsWindow r = new ResultsWindow();
                    displayPausesGraph(plist, r);
                    r.setInfo("No activity graph: " + info);
                    r.setVisible(true);
                }
            }
            ResultsWindow results = new ResultsWindow();
            displayPausesGraph(parsers, results);
            results.setInfo("No activity graph: " + info);
            results.setVisible(true);
        }

    }

    /**
     * Method invoked when the "Display custom graph" button is clicked.
     */
    @Action
    public void displayCustomGraphClicked() {
        if (view.fileList.isEmpty()) {
            view.reportError("No files selected.");
        } else {
            List<XMLParser> parsers = parseFiles();
            if (view.getIndividualGraphs() && view.fileList.size() > 1) {
                for (XMLParser p : parsers) {
                    List<XMLParser> plist = new LinkedList<XMLParser>();
                    plist.add(p);
                    ResultsWindow r = new ResultsWindow();
                    displayCustomGraph(plist, r);
                    r.setInfo("Custom graph: " + info);
                    r.setVisible(true);
                }
            }
            ResultsWindow results = new ResultsWindow();
            displayCustomGraph(parsers, results);
            results.setInfo("Custom graph: " + info);
            results.setVisible(true);
        }

    }

    /**
     * Method invoked when the "Export data" button is clicked.
     */
    @Action
    public void exportDataClicked() {
        if (view.fileList.isEmpty()) {
            view.reportError("No files selected.");
        } else {
            List<XMLParser> parsers = parseFiles();
        }
    }

    /**
     * Method invoked when the "Export statistics" button is clicked.
     */
    @Action
    public void exportStatisticsClicked() {
        File saveStatsFile = view.showSaveFileChooser();
        while (saveStatsFile != null && !saveStatsFile.getAbsolutePath().endsWith(".xls")) {
            view.reportError("Please enter a filename with ending \".xls\"");
            saveStatsFile = view.showSaveFileChooser();
        }
        if (saveStatsFile != null) {
            List<XMLParser> parsers = parseFiles();
            try {
                ExcelDocument e = new ExcelDocument(parsers, saveStatsFile);
                e.makeExcelFile();
            } catch (Exception ex) {
                view.reportError(ex.getMessage());
            }
        }
    }

    /**
     * Generates a single graph with the corresponding statistical data
     * in a separate window.
     * @param f the file containing the transcript
     * @return the XMLParser generated
     */
    private XMLParser analyzeFile(File f, int type, int start, int end, int stats) {
        
        XMLParser parser = null;
        try {
            parser = new XMLParser(f, stats);
            String error = parser.check(type, start, end);
            if (!error.isEmpty()) {
                view.reportError("Error while parsing document "
                        + f.getName() + ": \n" + error);
            } else {
                parser.parse();
            }

        } catch (Exception ex) {
            view.reportError("Error while parsing document "
                    + f.getName() + ": \n" + ex.getMessage());
        }
        return parser;

    }

    /**
     * Displays the list of sources (for consults) and the name of the process.
     * @param parser the XMLParser containing all relevant data
     * @param results the window displaying all results.
     */
    private void displaySingleSources(XMLParser parser, ResultsWindow results) {
        results.setNameField(parser.name);
        String sources = "";
        for (Object[] o : parser.sourcesList) {
            sources = sources.concat(String.format("%s (%dx)\n",
                    o[0], (Integer) o[1]));
        }
        //results.setSourcesField(sources);
    }

    /**
     * From the individual list of sources, combine all of the sources and
     * calculate the total of processes that used each source.
     * @param parsers list of XMLParsers containing all the relevant information
     * @param results the window in which to display the results
     */
    private void displaycombinedSources(List<XMLParser> parsers,
            ResultsWindow results) {

        results.setNameField(String.format("Combined graph of %d processes",
                parsers.size()));
        String sources = "";
        List<String> slist = new LinkedList<String>();
        List<Integer> scount = new LinkedList<Integer>();

        for (XMLParser p : parsers) {
            for (Object[] o : p.sourcesList) {
                String s = (String) o[0];
                int index = slist.indexOf(s);
                if (index != -1) {
                    scount.set(index, scount.get(index) + 1);
                } else {
                    slist.add(s);
                    scount.add(1);
                }
            }
        }

        for (int i = 0; i < slist.size(); i++) {
            sources = sources.concat(String.format("%s (%d process(es))\n",
                    slist.get(i), scount.get(i)));
        }

        //results.setSourcesField(sources);
    }

    /**
     * Given the information in the parsers, add the times to the result
     * window r so that the main graph can be displayed.
     * @param parsers list of XMLParsers, each representing a separate file
     * @param r the result window where the graph should be displayed
     */
    private void displayMainGraph(List<XMLParser> parsers, ResultsWindow r) {

        int size = parsers.size() + 2;
        double step = 1.0 / size;
        String nameField = parsers.size() + " process(es): ";
        XYSeriesCollection data = new XYSeriesCollection();

        List<Object[]> annotList = new LinkedList<Object[]>();
        annotList.add(new Object[]{"No actions", 6}); // used to be pauses
        annotList.add(new Object[]{"Resources", 5}); // used to be consults
        annotList.add(new Object[]{"Typos", 4});
        annotList.add(new Object[]{"Revisions", 3});
        annotList.add(new Object[]{"TT writing", 2});
        annotList.add(new Object[]{"ST actions", 1});

        List<String> processNames = new LinkedList<String>();

        double pos = 2 * step;

        for (XMLParser p : parsers) {

            nameField += p.name + ", ";
            processNames.add(p.name);

            XYSeries pauses = new XYSeries("pauses " + p.name);
            // the pauses
            for (Tag t : p.pauses) {
                for (Integer[] times : t.times) {
                    addToProcess(pauses, times, pos + 5);
                }
            }
            data.addSeries(pauses);

            XYSeries consults = new XYSeries("consults " + p.name);
            // All consultations
            for (Tag t : p.consults) {
                for (Integer[] times : t.times) {
                    addToProcess(consults, times, pos + 4);
                }
            }
            data.addSeries(consults);

            XYSeries typos = new XYSeries("typos " + p.name);
            // All typos
            for (Integer[] times : p.typos.times) {
                addToProcess(typos, times, pos + 3);
            }
            data.addSeries(typos);

            XYSeries revisions = new XYSeries("revisions " + p.name);
            // All revisions
            for (Tag t : p.revisions) {
                for (Integer[] times : t.times) {
                    addToProcess(revisions, times, pos + 2);
                }
            }
            data.addSeries(revisions);

            XYSeries writing = new XYSeries("writing " + p.name);
            // All TT writing
            for (Integer[] times : p.writes.times) {
                addToProcess(writing, times, pos + 1);
            }
            for (Integer[] times : p.accepts.times) {
                addToProcess(writing, times, pos + 1);
            }
            data.addSeries(writing);

            XYSeries st = new XYSeries("ST " + p.name);
            // All ST actions
            for (Integer[] times : p.sourcetext.times) {
                addToProcess(st, times, pos + 0);
            }
            data.addSeries(st);

            pos += step;
        }

        r.setNameField(nameField.substring(0, nameField.lastIndexOf(",")));
        r.setTitle("Translation Process Visualizer: Main graph");
        JFreeChart chart = ChartFactory.createXYLineChart(
                null, "Time [in sec]", null, data,
                PlotOrientation.VERTICAL, true, false, false);
        r.drawGraph(chart, annotList, processNames);

    }

    /**
     * Given the information in the parsers, add the times to the result
     * window r so that the consults graph can be displayed.
     * @param parsers list of XMLParsers, each representing a separate file
     * @param r the result window where the graph should be displayed
     */
    private void displayConsultsGraph(List<XMLParser> parsers, ResultsWindow r) {

        if (parsers.isEmpty()) {
            return;
        }
        int size = parsers.size() + 2;
        double step = 1.0 / size;
        String nameField = parsers.size() + " process(es): ";

        XYSeriesCollection data = new XYSeriesCollection();
        int numtypes = parsers.get(0).consults.size();

        int ypos = numtypes;
        List<Object[]> annotList = new LinkedList<Object[]>();
        for (Tag t : parsers.get(0).consults) {
            annotList.add((new Object[]{t.name, ypos}));
            ypos--;
        }

        List<String> processNames = new LinkedList<String>();

        double pos = 2 * step;

        for (XMLParser p : parsers) {

            nameField += p.name + ", ";
            processNames.add(p.name);
            int i = numtypes - 1;
            // All consultations
            for (Tag t : p.consults) {
                XYSeries consults = new XYSeries(t.subtype + " " + p.name);
                for (Integer[] times : t.times) {
                    addToProcess(consults, times, i + pos);
                }
                data.addSeries(consults);
                i--;
            }

            pos += step;
        }
        r.setNameField(nameField.substring(0, nameField.lastIndexOf(",")));
        r.setTitle("Translation Process Visualizer: Consults graph");
        JFreeChart chart = ChartFactory.createXYLineChart(
                null, "Time [in sec]", null, data,
                PlotOrientation.VERTICAL, true, false, false);
        r.drawGraph(chart, annotList, processNames);

    }

    /**
     * Given the information in the parsers, add the times to the result
     * window r so that the pauses graph can be displayed.
     * @param parsers list of XMLParsers, each representing a separate file
     * @param r the result window where the graph should be displayed
     */
    private void displayPausesGraph(List<XMLParser> parsers, ResultsWindow r) {

        if (parsers.isEmpty()) {
            return;
        }
        int size = parsers.size() + 2;
        double step = 1.0 / size;
        String nameField = parsers.size() + " process(es): ";

        XYSeriesCollection data = new XYSeriesCollection();
        int numtypes = parsers.get(0).pauses.size();

        int ypos = numtypes;
        List<Object[]> annotList = new LinkedList<Object[]>();
        for (Tag t : parsers.get(0).pauses) {
            annotList.add((new Object[]{t.name, ypos}));
            ypos--;
        }

        List<String> processNames = new LinkedList<String>();

        double pos = 2 * step;

        for (XMLParser p : parsers) {

            nameField += p.name + ", ";
            processNames.add(p.name);
            int i = numtypes - 1;
            // All pauses
            for (Tag t : p.pauses) {
                XYSeries pauses = new XYSeries(t.subtype + " " + p.name);
                for (Integer[] times : t.times) {
                    addToProcess(pauses, times, i + pos);
                }
                data.addSeries(pauses);
                i--;
            }

            pos += step;
        }
        r.setNameField(nameField.substring(0, nameField.lastIndexOf(",")));
        r.setTitle("Translation Process Visualizer: Pauses graph");
        JFreeChart chart = ChartFactory.createXYLineChart(
                null, "Time [in sec]", null, data,
                PlotOrientation.VERTICAL, true, false, false);
        r.drawGraph(chart, annotList, processNames);

    }

    /**
     * Given the information in the parsers, add the times to the result
     * window r so that the pauses graph can be displayed.
     * @param parsers list of XMLParsers, each representing a separate file
     * @param r the result window where the graph should be displayed
     */
    private void displayRevisionGraph(List<XMLParser> parsers, ResultsWindow r) {

        if (parsers.isEmpty()) {
            return;
        }
        int size = parsers.size() + 2;
        double step = 1.0 / size;
        String nameField = parsers.size() + " process(es): ";

        XYSeriesCollection data = new XYSeriesCollection();
        int numtypes = 4;

        List<Object[]> annotList = new LinkedList<Object[]>();
        annotList.add(new Object[]{"Insertions", 4});
        annotList.add(new Object[]{"Deletions", 3});
        annotList.add(new Object[]{"Pastes & Move to", 2});
        annotList.add(new Object[]{"Cuts & Move from", 1});

        List<String> processNames = new LinkedList<String>();

        double pos = 2 * step;

        XYSeries ins;
        XYSeries del;
        XYSeries pas;
        XYSeries cut;
        for (XMLParser p : parsers) {

            nameField += p.name + ", ";
            processNames.add(p.name);
            int i = numtypes - 1;
            ins = new XYSeries("insertions " + p.name);
            del = new XYSeries("deletions " + p.name);
            pas = new XYSeries("pastes " + p.name);
            cut = new XYSeries("cuts " + p.name);

            for (Tag t : p.revisions) {
                if ((view.getCombinedRevisions() && t.subtype.equalsIgnoreCase("revision2"))
                        || (t.subtype.equalsIgnoreCase("revision"))) {
                    if (t.type.equalsIgnoreCase("inserts")) {
                        for (Integer[] times : t.times) {
                            addToProcess(ins, times, 3 + pos);
                        }
                    } else if (t.type.equalsIgnoreCase("deletes")) {
                        for (Integer[] times : t.times) {
                            addToProcess(del, times, 2 + pos);
                        }
                    } else if (t.type.equalsIgnoreCase("pastes")) {
                        for (Integer[] times : t.times) {
                            addToProcess(pas, times, 1 + pos);
                        }
                    } else if (t.type.equalsIgnoreCase("cuts")) {
                        for (Integer[] times : t.times) {
                            addToProcess(cut, times, 0 + pos);
                        }
                    } else if (t.type.equalsIgnoreCase("moves to")) {
                        for (Integer[] times : t.times) {
                            addToProcess(pas, times, 1 + pos);
                        }
                    } else if (t.type.equalsIgnoreCase("moves from")) {
                        for (Integer[] times : t.times) {
                            addToProcess(cut, times, 0 + pos);
                        }
                    }
                }
            }
            data.addSeries(ins);
            data.addSeries(del);
            data.addSeries(pas);
            data.addSeries(cut);
            pos += step;
        }
        if (view.getCombinedRevisions()) {
            r.setTitle("Translation Process Visualizer: Combined revision graph");
        } else {
            r.setTitle("Translation Process Visualizer: Revision graph");
        }
        r.setNameField(nameField.substring(0, nameField.lastIndexOf(",")));
        JFreeChart chart = ChartFactory.createXYLineChart(
                null, "Time [in sec]", null, data,
                PlotOrientation.VERTICAL, true, false, false);
        r.drawGraph(chart, annotList, processNames);

    }

    /**
     * Given the information in the parsers, add the times to the result
     * window r so that the pauses graph can be displayed.
     * @param parsers list of XMLParsers, each representing a separate file
     * @param r the result window where the graph should be displayed
     */
    private void displayCustomGraph(List<XMLParser> parsers, ResultsWindow r) {

        if (parsers.isEmpty()) {
            return;
        }
        int size = parsers.size() + 2;
        double step = 1.0 / size;
        String nameField = parsers.size() + " process(es): ";

        XYSeriesCollection data = new XYSeriesCollection();

        int ypos = 1;
        List<Object[]> annotList = new LinkedList<Object[]>();

        if (view.getSTActions()) {
            annotList.add((new Object[]{"ST action", ypos}));
            ypos++;
        }
        if (view.getWriting()) {
            annotList.add((new Object[]{"TT writing", ypos}));
            ypos++;
        }
        if (view.getIndInterrupts()) {
            annotList.add((new Object[]{"Break", ypos}));
            ypos++;
            annotList.add((new Object[]{"Workflow", ypos}));
            ypos++;
            annotList.add((new Object[]{"Task", ypos}));
            ypos++;
            annotList.add((new Object[]{"Internet", ypos}));
            ypos++;
            annotList.add((new Object[]{"Job mail", ypos}));
            ypos++;
            annotList.add((new Object[]{"Private mail", ypos}));
            ypos++;
        }
        if (view.getInterrupts()) {
            annotList.add((new Object[]{"Interrupts", ypos}));
            ypos++;
        }
        if (view.getIndRevisions()) {
            annotList.add((new Object[]{"Cuts", ypos}));
            ypos++;
            annotList.add((new Object[]{"Pastes", ypos}));
            ypos++;
            annotList.add((new Object[]{"Deletes", ypos}));
            ypos++;
            annotList.add((new Object[]{"Inserts", ypos}));
            ypos++;
        }
        if (view.getRevisions()) {
            annotList.add((new Object[]{"Revisions", ypos}));
            ypos++;
        }
        if (view.getTypos()) {
            annotList.add((new Object[]{"Typos", ypos}));
            ypos++;
        }
        if (view.getIndConsults()) {

            annotList.add((new Object[]{"Other Resources", ypos}));
            ypos++;
            annotList.add((new Object[]{"Concordance", ypos}));
            ypos++;
            annotList.add((new Object[]{"Workflow parallel text", ypos}));
            ypos++;
            annotList.add((new Object[]{"Workflow glossary", ypos}));
            ypos++;
            annotList.add((new Object[]{"Workflow style guide", ypos}));
            ypos++;
            annotList.add((new Object[]{"Workflow context", ypos}));
            ypos++;
            annotList.add((new Object[]{"Termbanks", ypos}));
            ypos++;
            annotList.add((new Object[]{"Portals", ypos}));
            ypos++;
            annotList.add((new Object[]{"Dictionaries", ypos}));
            ypos++;
            annotList.add((new Object[]{"Encyclopedias", ypos}));
            ypos++;
            annotList.add((new Object[]{"Search engines", ypos}));
            ypos++;
               
        }
        if (view.getConsults()) {
            annotList.add((new Object[]{"Resources", ypos}));
            ypos++;
        }
        if (view.getIndPauses()) {
            annotList.add((new Object[]{"Looks unclear", ypos}));
            ypos++;
            annotList.add((new Object[]{"Looks at ST+TT", ypos}));
            ypos++;
            annotList.add((new Object[]{"Looks at TT", ypos}));
            ypos++;
            annotList.add((new Object[]{"Looks at ST", ypos}));
            ypos++;
            annotList.add((new Object[]{"Looks at task", ypos}));
            ypos++;
            annotList.add((new Object[]{"Looks at consults", ypos}));
            ypos++;
            annotList.add((new Object[]{"No screen activity", ypos}));
            ypos++;
        }
        if (view.getPauses()) {
            annotList.add((new Object[]{"No actions", ypos}));
            ypos++;
        }

        List<String> processNames = new LinkedList<String>();

        double pos = 2 * step;

        for (XMLParser p : parsers) {

            nameField += p.name + ", ";
            processNames.add(p.name);
            int i = 0;

            if (view.getSTActions()) {
                XYSeries st = new XYSeries("ST " + p.name);
                for (Integer[] times : p.sourcetext.times) {
                    addToProcess(st, times, pos + i);
                }
                data.addSeries(st);
                i++;
            }

            if (view.getWriting()) {
                XYSeries writing = new XYSeries("writing " + p.name);
                for (Integer[] times : p.writes.times) {
                    addToProcess(writing, times, pos + i);
                }
                for (Integer[] times : p.accepts.times) {
                    addToProcess(writing, times, pos + i);
                }
                data.addSeries(writing);
                i++;
            }

            if (view.getIndInterrupts()) {
                int temp = i;
                for (Tag t : p.interrupts) {
                    XYSeries interrupts = new XYSeries(t.subtype + " " + p.name);
                    for (Integer[] times : t.times) {
                        addToProcess(interrupts, times, ((2 * temp) + p.interrupts.size() - 1 - i) + pos);
                    }
                    data.addSeries(interrupts);
                    i++;
                }
            }

            if (view.getInterrupts()) {
                XYSeries interrupt = new XYSeries("interrupts " + p.name);
                for (Tag t : p.interrupts) {
                    for (Integer[] times : t.times) {
                        addToProcess(interrupt, times, pos + i);
                    }
                }
                data.addSeries(interrupt);
                i++;
            }

            if (view.getIndRevisions()) {
                XYSeries ins = new XYSeries("insertions " + p.name);
                XYSeries del = new XYSeries("deletions " + p.name);
                XYSeries pas = new XYSeries("pastes " + p.name);
                XYSeries cut = new XYSeries("cuts " + p.name);

                for (Tag t : p.revisions) {
                    if ((view.getCombinedRevisions() && t.subtype.equalsIgnoreCase("revision2"))
                            || (t.subtype.equalsIgnoreCase("revision"))) {
                        if (t.type.equalsIgnoreCase("inserts")) {
                            for (Integer[] times : t.times) {
                                addToProcess(ins, times, 3 + i + pos);
                            }
                        } else if (t.type.equalsIgnoreCase("deletes")) {
                            for (Integer[] times : t.times) {
                                addToProcess(del, times, 2 + i + pos);
                            }
                        } else if (t.type.equalsIgnoreCase("pastes")) {
                            for (Integer[] times : t.times) {
                                addToProcess(pas, times, 1 + i + pos);
                            }
                        } else if (t.type.equalsIgnoreCase("cuts")) {
                            for (Integer[] times : t.times) {
                                addToProcess(cut, times, i + pos);
                            }
                        } else if (t.type.equalsIgnoreCase("moves to")) {
                            for (Integer[] times : t.times) {
                                addToProcess(pas, times, 1 + i + pos);
                            }
                        } else if (t.type.equalsIgnoreCase("moves from")) {
                            for (Integer[] times : t.times) {
                                addToProcess(cut, times, i + pos);
                            }
                        }
                    }
                }
                data.addSeries(ins);
                data.addSeries(del);
                data.addSeries(pas);
                data.addSeries(cut);
                i = i + 4;

            }

            if (view.getRevisions()) {
                XYSeries revisions = new XYSeries("revisions " + p.name);
                for (Tag t : p.revisions) {
                    for (Integer[] times : t.times) {
                        addToProcess(revisions, times, pos + i);
                    }
                }
                data.addSeries(revisions);
                i++;
            }

            if (view.getTypos()) {
                XYSeries typos = new XYSeries("typos " + p.name);
                for (Integer[] times : p.typos.times) {
                    addToProcess(typos, times, pos + i);
                }
                data.addSeries(typos);
                i++;
            }

            if (view.getIndConsults()) {
                int temp = i;
                for (Tag t : p.consults) {
                    XYSeries consults = new XYSeries(t.subtype + " " + p.name);
                    for (Integer[] times : t.times) {
                        addToProcess(consults, times, ((2 * temp) + p.consults.size() - 1 - i) + pos);
                    }
                    data.addSeries(consults);
                    i++;
                }
            }
            if (view.getConsults()) {
                XYSeries consults = new XYSeries("consults " + p.name);
                for (Tag t : p.consults) {
                    for (Integer[] times : t.times) {
                        addToProcess(consults, times, pos + i);
                    }
                }
                data.addSeries(consults);
                i++;
            }

            if (view.getIndPauses()) {
                int temp = i;
                for (Tag t : p.pauses) {
                    XYSeries pauses = new XYSeries(t.subtype + " " + p.name);
                    for (Integer[] times : t.times) {
                        addToProcess(pauses, times, ((2 * temp) + p.pauses.size() - 1 - i) + pos);
                    }
                    data.addSeries(pauses);
                    i++;
                }
            }

            if (view.getPauses()) {
                XYSeries pauses = new XYSeries("pauses " + p.name);
                for (Tag t : p.pauses) {
                    for (Integer[] times : t.times) {
                        addToProcess(pauses, times, pos + i);
                    }
                }
                data.addSeries(pauses);
                i++;
            }

            pos += step;
        }
        
        r.setNameField(nameField.substring(0, nameField.lastIndexOf(",")));
        r.setTitle("Translation Process Visualizer: Custom graph");
        JFreeChart chart = ChartFactory.createXYLineChart(
                null, "Time [in sec]", null, data,
                PlotOrientation.VERTICAL, true, false, false);
        r.drawGraph(chart, annotList, processNames);

    }

    /**
     * Add the times to the XYSeries process at the right position pos.
     * @param process XYSeries to which the times should be added
     * @param times start and end time of the action
     * @param pos the position (y-coordinate) for the action
     */
    private void addToProcess(XYSeries process, Integer[] times, double pos) {
        process.add((times[0].doubleValue() - 0.001), null);
        process.add(times[0].doubleValue(), pos);
        process.add(times[1].doubleValue(), pos);
        process.add((times[1].doubleValue() + 0.001), null);
    }
}