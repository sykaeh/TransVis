/*
 * TranVis.java
 * 
 * Translation process Visualizer
 * 
 */
package transvis;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main class of the application.
 *
 * @author Sybil Ehrensberger
 * @version 2.0
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
     *
     * @param root
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     *
     * @return the instance of TranscriptVisualizer
     */
    public static MainApp getApplication() {
        return Application.getInstance(MainApp.class);
    }

    /**
     * Main method launching the application.
     *
     * @param args command line arguments
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

        // TODO: Look at selection choices

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

    private List<Transcript> parseFiles() {

        int start = Transcript.convertToSeconds(view.getStart());
        int end = Transcript.convertToSeconds(view.getEnd());

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

        List<Transcript> transcripts = new LinkedList<Transcript>();
        for (File f : view.fileList) {
            Transcript t = parseFile(f, type, start, end);
            if (t != null) {
                transcripts.add(t);
            }
        }
        return transcripts;
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
     *
     * @param f the file containing the transcript
     * @return the Transcript generated
     */
    private Transcript parseFile(File f, int type, int start, int end) {

        Transcript t = null;
        try {
            t = new Transcript(f);
            t.setSelection(type, start, end);
        } catch (Exception ex) {
            ex.printStackTrace();
            view.reportError("Error while parsing document " + f.getName() + ": \n" + ex.getMessage());
        }
        return t;

    }

    private XYSeries getDataSeriesByGroup(Transcript t, IncidentType type, double position) {

        XYSeries series = new XYSeries(t.name + type.descr);
        t.incidents.stream().filter(i -> i.group == type).forEach(i -> addToSeries(series, i, position));
        return series;
    }

    private XYSeries getDataSeriesBySubGroup(Transcript t, IncidentType type, double position) {

        XYSeries series = new XYSeries(t.name + type.descr);
        t.incidents.stream().filter(i -> i.subgroup == type).forEach(i -> addToSeries(series, i, position));
        return series;
    }

    /**
     * Given the information in the transcripts, add the times to the result
     * window r so that the main graph can be displayed.
     *
     * @param transcripts list of XMLParsers, each representing a separate file
     * @param r           the result window where the graph should be displayed
     */
    private void displayMainGraph(List<Transcript> transcripts, ResultsWindow r) {

        if (transcripts.isEmpty()) {
            return;
        }

        int size = transcripts.size() + 2;
        double step = 1.0 / size;
        String nameField = transcripts.size() + " process(es): ";
        XYSeriesCollection data = new XYSeriesCollection();

        List<Object[]> annotList = new LinkedList<Object[]>();
        annotList.add(new Object[]{"Resources", 5}); // used to be consults
        annotList.add(new Object[]{"Typos", 4});
        annotList.add(new Object[]{"Revisions", 3});
        annotList.add(new Object[]{"TT writing", 2});
        annotList.add(new Object[]{"ST actions", 1});

        List<String> processNames = new LinkedList<String>();

        double pos = 1 - step;

        for (Transcript t : transcripts) {

            nameField += t.name + ", ";
            processNames.add(t.name);

            data.addSeries(getDataSeriesByGroup(t, IncidentType.CONSULTATION, pos + 4));
            data.addSeries(getDataSeriesByGroup(t, IncidentType.TYPOS, pos + 3));
            data.addSeries(getDataSeriesByGroup(t, IncidentType.REVISION, pos + 2));
            data.addSeries(getDataSeriesByGroup(t, IncidentType.TARGETTEXT, pos + 1));
            data.addSeries(getDataSeriesByGroup(t, IncidentType.SOURCETEXT, pos + 0));

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
     *
     * @param parsers list of XMLParsers, each representing a separate file
     * @param r       the result window where the graph should be displayed
     */
    private void displayConsultsGraph(List<Transcript> parsers, ResultsWindow r) {

        boolean workplace = false;
        for (Transcript p : parsers) {
            if (p.workPlace) {
                workplace = true;
            }
        }
        List<IncidentType> categories = Consultation.SUBGROUP_GENERAL;
        if (workplace) {
            categories.addAll(Consultation.SUBGROUP_WORKPLACE);
        }

        displayByCategories(parsers, r, categories, "Consults graph");

    }


    private void displayByCategories(List<Transcript> transcripts, ResultsWindow r,
                                     List<IncidentType> categories, String graph_name) {

        if (transcripts.isEmpty()) {
            return;
        }

        int size = transcripts.size() + 2;
        double step = 1.0 / size;
        String nameField = transcripts.size() + " process(es): ";

        XYSeriesCollection data = new XYSeriesCollection();

        boolean initialized = false;
        int numTypes = categories.size();
        List<Object[]> annotList = new LinkedList<Object[]>();
        List<String> processNames = new LinkedList<String>();

        double pos = 1 - step;

        for (Transcript t : transcripts) {

            nameField += t.name + ", ";
            processNames.add(t.name);

            int i = numTypes - 1;
            for (IncidentType sub : categories) {
                if (!initialized) {
                    annotList.add(new Object[]{sub.descr, i + 1});
                }
                data.addSeries(getDataSeriesBySubGroup(t, sub, i + pos));
                i--;
            }

            initialized = true;
            pos -= step;
        }

        r.setNameField(nameField.substring(0, nameField.lastIndexOf(",")));
        r.setTitle(graph_name);
        JFreeChart chart = ChartFactory.createXYLineChart(
                null, "Time [in sec]", null, data,
                PlotOrientation.VERTICAL, true, false, false);
        r.drawGraph(chart, annotList, processNames, numTypes + 1);
    }

    /**
     * Given the information in the transcripts, add the times to the result
     * window r so that the pauses graph can be displayed.
     *
     * @param transcripts list of XMLParsers, each representing a separate file
     * @param r           the result window where the graph should be displayed
     */
    private void displayPausesGraph(List<Transcript> transcripts, ResultsWindow r) {

        displayByCategories(transcripts, r, Pause.SUBGROUPS, "Pauses graph");

    }

    /**
     * Given the information in the parsers, add the times to the result
     * window r so that the pauses graph can be displayed.
     *
     * @param parsers list of XMLParsers, each representing a separate file
     * @param r       the result window where the graph should be displayed
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
            ins = new XYSeries("insertions " + p.name);
            del = new XYSeries("deletions " + p.name);
            pas = new XYSeries("pastes " + p.name);


            // TODO: cast does not work!
            Revision[] inserts = (Revision[]) p.incidents.stream().filter(inc -> inc.subgroup == IncidentType.R_INSERTS).toArray();
            for (Revision e : inserts) {
                if ((bothRevisions && e.revisionType == IncidentType.R_REVISION2)
                        || e.revisionType == IncidentType.R_REVISION) {
                    addToSeries(ins, e, 2 + pos);
                }
            }

            Revision[] deletes = (Revision[]) p.incidents.stream().filter(inc -> inc.subgroup == IncidentType.R_DELETES).toArray();
            for (Revision e : deletes) {
                if ((bothRevisions && e.revisionType == IncidentType.R_REVISION2)
                        || e.revisionType == IncidentType.R_REVISION) {
                    addToSeries(del, e, 1 + pos);
                }
            }

            Revision[] pastes = (Revision[]) p.incidents.stream().filter(inc -> inc.subgroup == IncidentType.R_PASTES || inc.subgroup == IncidentType.R_MOVESTO).toArray();
            for (Revision e : pastes) {
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
     *
     * @param parsers list of XMLParsers, each representing a separate file
     * @param r       the result window where the graph should be displayed
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

        List<IncidentType> consult_categories = Consultation.SUBGROUP_GENERAL;
        if (workplace)
            consult_categories.addAll(Consultation.SUBGROUP_WORKPLACE);

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
            annotList.add((new Object[]{"TM input", ypos}));
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
                data.addSeries(getDataSeriesByGroup(p, IncidentType.SOURCETEXT, pos + i));
                i++;
            }

            if (view.getWriting()) {
                data.addSeries(getDataSeriesByGroup(p, IncidentType.TARGETTEXT, pos + i));
                i++;
            }

            if (view.getMatches()) {
                data.addSeries(getDataSeriesByGroup(p, IncidentType.T_MATCH, pos + i));
                i++;
            }

            if (view.getIndInterrupts()) {
                int temp = i;
                for (IncidentType sub : Interruption.SUBGROUPS) {
                    data.addSeries(getDataSeriesBySubGroup(p, sub, ((2 * temp) + Interruption.SUBGROUPS.size() - 1 - i) + pos));
                    i++;
                }

            }

            if (view.getInterrupts()) {
                data.addSeries(getDataSeriesByGroup(p, IncidentType.INTERRUPTION, pos + i));
                i++;
            }

            if (view.getIndRevisions()) {

                XYSeries ins = new XYSeries("insertions " + p.name);
                XYSeries del = new XYSeries("deletions " + p.name);
                XYSeries pas = new XYSeries("pastes " + p.name);


                Revision[] inserts = (Revision[]) p.incidents.stream().filter(inc -> inc.subgroup == IncidentType.R_INSERTS).toArray();
                for (Revision e : inserts) {
                    if ((bothRevisions && e.revisionType == IncidentType.R_REVISION2)
                            || e.revisionType == IncidentType.R_REVISION) {
                        addToSeries(ins, e, 3 + i + pos);
                    }
                }

                Revision[] deletes = (Revision[]) p.incidents.stream().filter(inc -> inc.subgroup == IncidentType.R_DELETES).toArray();
                for (Revision e : deletes) {
                    if ((bothRevisions && e.revisionType == IncidentType.R_REVISION2)
                            || e.revisionType == IncidentType.R_REVISION) {
                        addToSeries(del, e, 2 + i + pos);
                    }
                }

                Revision[] pastes = (Revision[]) p.incidents.stream().filter(inc -> inc.subgroup == IncidentType.R_PASTES || inc.subgroup == IncidentType.R_MOVESTO).toArray();
                for (Revision e : pastes) {
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
                data.addSeries(getDataSeriesByGroup(p, IncidentType.REVISION, pos + i));
                i++;
            }

            if (view.getTypos()) {
                data.addSeries(getDataSeriesByGroup(p, IncidentType.TYPOS, pos + i));
                i++;
            }

            if (view.getIndConsults()) {
                int temp = i;

                for (IncidentType sub : consult_categories) {
                    data.addSeries(getDataSeriesBySubGroup(p, sub, ((2 * temp) + consult_categories.size() - 1 - i) + pos));
                    i++;
                }

            }
            if (view.getConsults()) {
                data.addSeries(getDataSeriesByGroup(p, IncidentType.CONSULTATION, pos + i));
                i++;
            }

            if (view.getIndPauses()) {
                int temp = i;

                for (IncidentType sub : Pause.SUBGROUPS) {
                    data.addSeries(getDataSeriesBySubGroup(p, sub, ((2 * temp) + Pause.SUBGROUPS.size() - 1 - i) + pos));
                    i++;
                }


            }

            if (view.getPauses()) {
                data.addSeries(getDataSeriesByGroup(p, IncidentType.PAUSE, pos + i));
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

    private void addToSeries(XYSeries series, BaseIncident i, double pos) {
        if (i.validTimes) {
            series.add((i.start - 0.001), null);
            series.add(i.start, pos);
            series.add(i.end, pos);
            series.add(i.end + 0.001, null);
        }
    }
}