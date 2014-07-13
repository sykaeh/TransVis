/*
 * TranVis.java
 * 
 * Translation process Visualizer
 * 
 */
package transcriptvisualizer;

import java.io.File;
import java.util.Arrays;
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
            List<Transcript> parsers = parseFiles();
            if (view.getIndividualGraphs() && view.fileList.size() > 1) {
                for (Transcript p : parsers) {
                    List<Transcript> plist = new LinkedList<Transcript>();
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
    private List<Transcript> parseFiles() {

        int start = view.getStart();
        int end = view.getEnd();

        int type = 0;
        info = "Partial process";
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

        List<Transcript> parsers = new LinkedList<Transcript>();
        for (File f : view.fileList) {
            Transcript p = analyzeFile(f, type, start, end);
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
            List<Transcript> parsers = parseFiles();
            if (view.getIndividualGraphs() && view.fileList.size() > 1) {
                for (Transcript p : parsers) {
                    List<Transcript> plist = new LinkedList<Transcript>();
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
            List<Transcript> parsers = parseFiles();
            if (view.getIndividualGraphs() && view.fileList.size() > 1) {
                for (Transcript p : parsers) {
                    List<Transcript> plist = new LinkedList<Transcript>();
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
            List<Transcript> parsers = parseFiles();
            if (view.getIndividualGraphs() && view.fileList.size() > 1) {
                for (Transcript p : parsers) {
                    List<Transcript> plist = new LinkedList<Transcript>();
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
            List<Transcript> parsers = parseFiles();
            if (view.getIndividualGraphs() && view.fileList.size() > 1) {
                for (Transcript p : parsers) {
                    List<Transcript> plist = new LinkedList<Transcript>();
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

                
        File saveStatsFile = view.showSaveFileChooser();
        while (saveStatsFile != null && !saveStatsFile.getAbsolutePath().endsWith(".xls")) {
            view.reportError("Please enter a filename with ending \".xls\"");
            saveStatsFile = view.showSaveFileChooser();
        }
        if (saveStatsFile != null) {
            List<Transcript> parsers = parseFiles();
            try {
                ExcelDocument e = new ExcelDocument(parsers, saveStatsFile);
                String errormsg = e.makeExcelFile(false, true);
                if (!errormsg.isEmpty()) {
                    view.reportError(errormsg);
                }
            } catch (Exception ex) {
                view.reportError(ex.getMessage());
            }
        }
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
            List<Transcript> parsers = parseFiles();
            try {
                ExcelDocument e = new ExcelDocument(parsers, saveStatsFile);
                String errormsg = e.makeExcelFile(true, false);
                if (!errormsg.isEmpty()) {
                    view.reportError(errormsg);
                }
            } catch (Exception ex) {
                view.reportError(ex.getMessage());
            }
        }
    }

    /**
     * Generates a single graph with the corresponding statistical data
     * in a separate window.
     * @param f the file containing the transcript
     * @return the Transcript generated
     */
    private Transcript analyzeFile(File f, int type, int start, int end) {

        Transcript parser = null;
        try {
            parser = new Transcript(f);
            String error = parser.check(type, start, end);
            if (!error.isEmpty()) {
                view.reportError("Error while parsing document "
                        + f.getName() + ": \n" + error);
            } else {
                parser.parse();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            view.reportError("Error while parsing document "
                    + f.getName() + ": \n" + ex.getMessage());
        }
        return parser;

    }

    /**
     * Given the information in the parsers, add the times to the result
     * window r so that the main graph can be displayed.
     * @param parsers list of XMLParsers, each representing a separate file
     * @param r the result window where the graph should be displayed
     */
    private void displayMainGraph(List<Transcript> parsers, ResultsWindow r) {

        if (parsers.isEmpty()) {
            return;
        }

        int size = parsers.size() + 2;
        double step = 1.0 / size;
        String nameField = parsers.size() + " process(es): ";
        XYSeriesCollection data = new XYSeriesCollection();

        List<Object[]> annotList = new LinkedList<Object[]>();
        annotList.add(new Object[]{"Resources", 5}); // used to be consults
        annotList.add(new Object[]{"Typos", 4});
        annotList.add(new Object[]{"Revisions", 3});
        annotList.add(new Object[]{"TT writing", 2});
        annotList.add(new Object[]{"ST actions", 1});

        List<String> processNames = new LinkedList<String>();

        double pos = 1 - step;

        for (Transcript p : parsers) {

            nameField += p.name + ", ";
            processNames.add(p.name);

            XYSeries consults = new XYSeries("consults " + p.name);
            // All consultations
            for (Incident e : p.incidentlists.get(IncidentType.CONSULTATION).elements) {
                addToSeries(consults, e, pos + 4);
            }
            data.addSeries(consults);

            XYSeries typos = new XYSeries("typos " + p.name);
            // All typos
            for (Incident e : p.incidentlists.get(IncidentType.TYPOS).elements) {
                addToSeries(typos, e, pos + 3);
            }
            data.addSeries(typos);

            XYSeries revisions = new XYSeries("revisions " + p.name);
            // All revisions
            for (Incident e : p.incidentlists.get(IncidentType.REVISION).elements) {
                addToSeries(revisions, e, pos + 2);
            }
            data.addSeries(revisions);

            XYSeries writing = new XYSeries("writing " + p.name);
            // All TT writing
            for (Incident e : p.incidentlists.get(IncidentType.PRODUCTION).elements) {
                addToSeries(writing, e, pos + 1);
            }
            data.addSeries(writing);

            XYSeries st = new XYSeries("ST " + p.name);
            // All ST actions
            for (Incident e : p.incidentlists.get(IncidentType.SOURCETEXT).elements) {
                addToSeries(st, e, pos + 0);
            }
            data.addSeries(st);

            pos -= step;
        }

        r.setNameField(nameField.substring(0, nameField.lastIndexOf(",")));
        r.setTitle("Main graph");
        JFreeChart chart = ChartFactory.createXYLineChart(
                null, "Time [in sec]", null, data,
                PlotOrientation.VERTICAL, true, false, false);
        r.drawGraph(chart, annotList, processNames, 6);

    }

    /**
     * Given the information in the parsers, add the times to the result
     * window r so that the consults graph can be displayed.
     * @param parsers list of XMLParsers, each representing a separate file
     * @param r the result window where the graph should be displayed
     */
    private void displayConsultsGraph(List<Transcript> parsers, ResultsWindow r) {

        if (parsers.isEmpty()) {
            return;
        }

        int size = parsers.size() + 2;
        double step = 1.0 / size;
        String nameField = parsers.size() + " process(es): ";

        XYSeriesCollection data = new XYSeriesCollection();

        // Checking if there are any workflow consultations
        boolean workplace = false;
        for (Transcript p : parsers) {
            if (p.workPlace) {
                workplace = true;
            }
        }

        boolean initialized = false;
        int numTypes = 0;
        List<Object[]> annotList = new LinkedList<Object[]>();
        List<String> processNames = new LinkedList<String>();

        double pos = 1 - step;

        for (Transcript p : parsers) {

            nameField += p.name + ", ";
            processNames.add(p.name);

            List<IncidentList> categories = Arrays.asList(
                    p.incidentlists.get(IncidentType.C_SEARCHENG),
                    p.incidentlists.get(IncidentType.C_ENCYCLOPEDIA),
                    p.incidentlists.get(IncidentType.C_DICTIONARY),
                    p.incidentlists.get(IncidentType.C_PORTALS),
                    p.incidentlists.get(IncidentType.C_OTHER));
            if (workplace) {
                List<IncidentList> categories2 = Arrays.asList(
                        p.incidentlists.get(IncidentType.C_TERMBANKS),
                        p.incidentlists.get(IncidentType.C_WFCONTEXT),
                        p.incidentlists.get(IncidentType.C_WFSTYLEGUIDE),
                        p.incidentlists.get(IncidentType.C_WFGLOSSARY),
                        p.incidentlists.get(IncidentType.C_WFPARALLELTEXT),
                        p.incidentlists.get(IncidentType.C_CONCORDANCE));
                categories.addAll(categories2);

            }

            if (!initialized) {
                numTypes = categories.size();
            }


            int i = numTypes - 1;
            // All consultations
            for (IncidentList t : categories) {
                if (!initialized) {
                    annotList.add(new Object[]{t.group.descr, i + 1});
                }
                XYSeries consults = new XYSeries(t.group + " " + p.name);
                for (Incident e : t.elements) {
                    addToSeries(consults, e, i + pos);
                }
                data.addSeries(consults);
                i--;
            }

            initialized = true;
            pos -= step;
        }

        r.setNameField(nameField.substring(0, nameField.lastIndexOf(",")));
        r.setTitle("Consults graph");
        JFreeChart chart = ChartFactory.createXYLineChart(
                null, "Time [in sec]", null, data,
                PlotOrientation.VERTICAL, true, false, false);
        r.drawGraph(chart, annotList, processNames, numTypes + 1);


    }

    /**
     * Given the information in the parsers, add the times to the result
     * window r so that the pauses graph can be displayed.
     * @param parsers list of XMLParsers, each representing a separate file
     * @param r the result window where the graph should be displayed
     */
    private void displayPausesGraph(List<Transcript> parsers, ResultsWindow r) {

        if (parsers.isEmpty()) {
            return;
        }

        int size = parsers.size() + 2;
        double step = 1.0 / size;
        String nameField = parsers.size() + " process(es): ";

        XYSeriesCollection data = new XYSeriesCollection();

        boolean initialized = false;
        int numTypes = 0;
        List<Object[]> annotList = new LinkedList<Object[]>();
        List<String> processNames = new LinkedList<String>();


        double pos = 1 - step;

        for (Transcript p : parsers) {

            nameField += p.name + ", ";
            processNames.add(p.name);

            List<IncidentList> categories = Arrays.asList(
                    p.incidentlists.get(IncidentType.P_SIMPLE),
                    p.incidentlists.get(IncidentType.P_CONSULTS),
                    p.incidentlists.get(IncidentType.P_READSTASK),
                    p.incidentlists.get(IncidentType.P_READSST),
                    p.incidentlists.get(IncidentType.P_READSTT),
                    p.incidentlists.get(IncidentType.P_READSSTTT),
                    p.incidentlists.get(IncidentType.P_UNCLEAR));

            if (!initialized) {
                numTypes = categories.size();
            }


            int i = numTypes - 1;
            // All pauses
            for (IncidentList t : categories) {
                if (!initialized) {
                    annotList.add(new Object[]{t.group.descr, i + 1});
                }
                XYSeries pauses = new XYSeries(t.group + " " + p.name);
                for (Incident e : t.elements) {
                    addToSeries(pauses, e, i + pos);
                }
                data.addSeries(pauses);
                i--;
            }

            initialized = true;
            pos -= step;
        }

        r.setNameField(nameField.substring(0, nameField.lastIndexOf(",")));
        r.setTitle("Pauses graph");
        JFreeChart chart = ChartFactory.createXYLineChart(
                null, "Time [in sec]", null, data,
                PlotOrientation.VERTICAL, true, false, false);
        r.drawGraph(chart, annotList, processNames, numTypes + 1);

    }

    /**
     * Given the information in the parsers, add the times to the result
     * window r so that the pauses graph can be displayed.
     * @param parsers list of XMLParsers, each representing a separate file
     * @param r the result window where the graph should be displayed
     */
    private void displayRevisionGraph(List<Transcript> parsers, ResultsWindow r) {

        if (parsers.isEmpty()) {
            return;
        }

        int size = parsers.size() + 2;
        double step = 1.0 / size;
        String nameField = parsers.size() + " process(es): ";

        XYSeriesCollection data = new XYSeriesCollection();
        int numtypes = 4;

        List<Object[]> annotList = new LinkedList<Object[]>();
        annotList.add(new Object[]{"Insertions", 3});
        annotList.add(new Object[]{"Deletions", 2});
        annotList.add(new Object[]{"Pastes & Move to", 1});

        List<String> processNames = new LinkedList<String>();

        double pos = 1 - step;

        XYSeries ins;
        XYSeries del;
        XYSeries pas;

        boolean bothRevisions = view.getCombinedRevisions();

        for (Transcript p : parsers) {

            nameField += p.name + ", ";
            processNames.add(p.name);
            int i = numtypes - 1;
            ins = new XYSeries("insertions " + p.name);
            del = new XYSeries("deletions " + p.name);
            pas = new XYSeries("pastes " + p.name);


            IncidentList list_inserts = p.incidentlists.get(IncidentType.R_INSERTS);
            for (Incident e : list_inserts.elements) {
                if ((bothRevisions && e.revisionType == IncidentType.R_REVISION2)
                        || e.revisionType == IncidentType.R_REVISION) {
                    addToSeries(ins, e, 2 + pos);
                }
            }

            IncidentList list_deletes = p.incidentlists.get(IncidentType.R_DELETES);
            for (Incident e : list_deletes.elements) {
                if ((bothRevisions && e.revisionType == IncidentType.R_REVISION2)
                        || e.revisionType == IncidentType.R_REVISION) {
                    addToSeries(del, e, 1 + pos);
                }
            }

            IncidentList list_pastes = p.incidentlists.get(IncidentType.R_PASTES);
            for (Incident e : list_pastes.elements) {
                if ((bothRevisions && e.revisionType == IncidentType.R_REVISION2)
                        || e.revisionType == IncidentType.R_REVISION) {
                    addToSeries(pas, e, 0 + pos);
                }
            }
            IncidentList list_moves = p.incidentlists.get(IncidentType.R_MOVESTO);
            for (Incident e : list_moves.elements) {
                if ((bothRevisions && e.revisionType == IncidentType.R_REVISION2)
                        || e.revisionType == IncidentType.R_REVISION) {
                    addToSeries(pas, e, 0 + pos);
                }
            }

            data.addSeries(ins);
            data.addSeries(del);
            data.addSeries(pas);
            pos -= step;
        }

        if (bothRevisions) {
            r.setTitle("Combined revision graph");
        } else {
            r.setTitle("Revision graph");
        }

        r.setNameField(nameField.substring(0, nameField.lastIndexOf(",")));
        JFreeChart chart = ChartFactory.createXYLineChart(
                null, "Time [in sec]", null, data,
                PlotOrientation.VERTICAL, true, false, false);
        r.drawGraph(chart, annotList, processNames, numtypes);


    }

    /**
     * Given the information in the parsers, add the times to the result
     * window r so that the pauses graph can be displayed.
     * @param parsers list of XMLParsers, each representing a separate file
     * @param r the result window where the graph should be displayed
     */
    private void displayCustomGraph(List<Transcript> parsers, ResultsWindow r) {

        if (parsers.isEmpty()) {
            return;
        }

        int size = parsers.size() + 2;
        double step = 1.0 / size;
        String nameField = parsers.size() + " process(es): ";

        XYSeriesCollection data = new XYSeriesCollection();

        // Checking if there are any workflow consultations
        boolean workplace = false;
        for (Transcript p : parsers) {
            if (p.workPlace) {
                workplace = true;
            }
        }
        boolean bothRevisions = view.getCombinedRevisions();

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
        if (view.getMatches()) {
            annotList.add((new Object[] {"TM input", ypos}));
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

            if (workplace) {
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
            }
            annotList.add((new Object[]{"Other Resources", ypos}));
            ypos++;
            annotList.add((new Object[]{"Portals", ypos}));
            ypos++;
            annotList.add((new Object[]{"Online Dictionaries", ypos}));
            ypos++;
            annotList.add((new Object[]{"Online Encyclopedias", ypos}));
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

        double pos = 1 - step;

        for (Transcript p : parsers) {

            nameField += p.name + ", ";
            processNames.add(p.name);
            int i = 0;

            if (view.getSTActions()) {
                XYSeries st = new XYSeries("ST " + p.name);
                for (Incident e : p.incidentlists.get(IncidentType.SOURCETEXT).elements) {
                    addToSeries(st, e, pos + i);
                }
                data.addSeries(st);
                i++;
            }

            if (view.getWriting()) {
                XYSeries writing = new XYSeries("writing " + p.name);
                for (Incident e : p.incidentlists.get(IncidentType.PRODUCTION).elements) {
                    addToSeries(writing, e, pos + i);
                }
                data.addSeries(writing);
                i++;
            }
            
            if (view.getMatches()) {
                XYSeries matches = new XYSeries("matches " + p.name);
                for (Incident e : p.incidentlists.get(IncidentType.MATCH).elements) {
                    addToSeries(matches, e, pos + i);
                }
                data.addSeries(matches);
                i++;
            }

            if (view.getIndInterrupts()) {
                int temp = i;                
                List<IncidentList> categories = Arrays.asList(
                        p.incidentlists.get(IncidentType.I_BREAK),
                        p.incidentlists.get(IncidentType.I_WORKFLOW),
                        p.incidentlists.get(IncidentType.I_TASK),
                        p.incidentlists.get(IncidentType.I_INTERNET),
                        p.incidentlists.get(IncidentType.I_JOBMAIL),
                        p.incidentlists.get(IncidentType.I_PRIVATEMAIL));

                for (IncidentList t : categories) {

                    XYSeries interrupts = new XYSeries(t.group + " " + p.name);
                    for (Incident e : t.elements) {
                        addToSeries(interrupts, e, ((2 * temp) + categories.size() - 1 - i) + pos);
                    }
                    data.addSeries(interrupts);
                    i++;
                }
                
            }

            if (view.getInterrupts()) {
                XYSeries interrupt = new XYSeries("interrupts " + p.name);
                for (Incident e : p.incidentlists.get(IncidentType.INTERRUPTION).elements) {
                    addToSeries(interrupt, e, pos + i);
                }
                data.addSeries(interrupt);
                i++;
            }

            if (view.getIndRevisions()) {
                XYSeries ins = new XYSeries("insertions " + p.name);
                XYSeries del = new XYSeries("deletions " + p.name);
                XYSeries pas = new XYSeries("pastes " + p.name);

                IncidentList list_inserts = p.incidentlists.get(IncidentType.R_INSERTS);
                for (Incident e : list_inserts.elements) {
                    if ((bothRevisions && e.revisionType == IncidentType.R_REVISION2)
                            || e.revisionType == IncidentType.R_REVISION) {
                        addToSeries(ins, e, 3 + i + pos);
                    }
                }

                IncidentList list_deletes = p.incidentlists.get(IncidentType.R_DELETES);
                for (Incident e : list_deletes.elements) {
                    if ((bothRevisions && e.revisionType == IncidentType.R_REVISION2)
                            || e.revisionType == IncidentType.R_REVISION) {
                        addToSeries(del, e, 2 + i + pos);
                    }
                }

                IncidentList list_pastes = p.incidentlists.get(IncidentType.R_PASTES);
                for (Incident e : list_pastes.elements) {
                    if ((bothRevisions && e.revisionType == IncidentType.R_REVISION2)
                            || e.revisionType == IncidentType.R_REVISION) {
                        addToSeries(pas, e, 1 + i + pos);
                    }
                }
                IncidentList list_moves = p.incidentlists.get(IncidentType.R_MOVESTO);
                for (Incident e : list_moves.elements) {
                    if ((bothRevisions && e.revisionType == IncidentType.R_REVISION2)
                            || e.revisionType == IncidentType.R_REVISION) {
                        addToSeries(pas, e, 1 + i + pos);
                    }
                }


                data.addSeries(ins);
                data.addSeries(del);
                data.addSeries(pas);
                i = i + 4;

            }

            if (view.getRevisions()) {
                XYSeries revisions = new XYSeries("revisions " + p.name);
                for (Incident e : p.incidentlists.get(IncidentType.REVISION).elements) {
                    addToSeries(revisions, e, pos + i);
                }
                data.addSeries(revisions);
                i++;
            }

            if (view.getTypos()) {
                XYSeries typos = new XYSeries("typos " + p.name);
                for (Incident e : p.incidentlists.get(IncidentType.TYPOS).elements) {
                    addToSeries(typos, e, pos + i);
                }
                data.addSeries(typos);
                i++;
            }

            if (view.getIndConsults()) {
                int temp = i;

                List<IncidentList> categories = Arrays.asList(
                        p.incidentlists.get(IncidentType.C_SEARCHENG),
                        p.incidentlists.get(IncidentType.C_ENCYCLOPEDIA),
                        p.incidentlists.get(IncidentType.C_DICTIONARY),
                        p.incidentlists.get(IncidentType.C_PORTALS),
                        p.incidentlists.get(IncidentType.C_OTHER));
                if (workplace) {
                    List<IncidentList> categories2 = Arrays.asList(
                            p.incidentlists.get(IncidentType.C_TERMBANKS),
                            p.incidentlists.get(IncidentType.C_WFCONTEXT),
                            p.incidentlists.get(IncidentType.C_WFSTYLEGUIDE),
                            p.incidentlists.get(IncidentType.C_WFGLOSSARY),
                            p.incidentlists.get(IncidentType.C_WFPARALLELTEXT),
                            p.incidentlists.get(IncidentType.C_CONCORDANCE));
                    categories.addAll(categories2);

                }

                for (IncidentList t : categories) {

                    XYSeries consults = new XYSeries(t.group + " " + p.name);
                    for (Incident e : t.elements) {
                        addToSeries(consults, e, ((2 * temp) + categories.size() - 1 - i) + pos);
                    }
                    data.addSeries(consults);
                    i++;
                }

            }
            if (view.getConsults()) {
                XYSeries consults = new XYSeries("consults " + p.name);
                for (Incident e : p.incidentlists.get(IncidentType.CONSULTATION).elements) {
                    addToSeries(consults, e, pos + i);
                }
                data.addSeries(consults);
                i++;
            }

            if (view.getIndPauses()) {
                int temp = i;
                List<IncidentList> categories = Arrays.asList(
                        p.incidentlists.get(IncidentType.P_SIMPLE),
                        p.incidentlists.get(IncidentType.P_CONSULTS),
                        p.incidentlists.get(IncidentType.P_READSTASK),
                        p.incidentlists.get(IncidentType.P_READSST),
                        p.incidentlists.get(IncidentType.P_READSTT),
                        p.incidentlists.get(IncidentType.P_READSSTTT),
                        p.incidentlists.get(IncidentType.P_UNCLEAR));

                for (IncidentList t : categories) {

                    XYSeries pauses = new XYSeries(t.group + " " + p.name);
                    for (Incident e : t.elements) {
                        addToSeries(pauses, e, ((2 * temp) + categories.size() - 1 - i) + pos);
                    }
                    data.addSeries(pauses);
                    i++;
                }

            }

            if (view.getPauses()) {
                XYSeries pauses = new XYSeries("pauses " + p.name);
                for (Incident e : p.incidentlists.get(IncidentType.PAUSE).elements) {
                    addToSeries(pauses, e, pos + i);
                }
                data.addSeries(pauses);
                i++;
            }

            pos -= step;
        }

        r.setNameField(nameField.substring(0, nameField.lastIndexOf(",")));
        r.setTitle("Custom graph");
        JFreeChart chart = ChartFactory.createXYLineChart(
                null, "Time [in sec]", null, data,
                PlotOrientation.VERTICAL, true, false, false);
        r.drawGraph(chart, annotList, processNames, ypos);


    }

    private void addToSeries(XYSeries series, Incident i, double pos) {
        if (i.validTimes) {
            series.add((i.start - 0.001), null);
            series.add(i.start, pos);
            series.add(i.end, pos);
            series.add(i.end + 0.001, null);
        }
    }
}