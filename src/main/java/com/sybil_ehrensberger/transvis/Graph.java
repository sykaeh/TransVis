/*
 * TranVis.java
 * 
 * Translation process Visualizer
 * 
 */
package com.sybil_ehrensberger.transvis;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 *
 * @author Sybil Ehrensberger
 * @version 2.0
 */
public class Graph {

    private GeneralView view;

    private List<Transcript> transcripts;

    public Graph(List<Transcript> transcript_list, GeneralView gv) {

        view = gv;
        transcripts = transcript_list;
    }

    public void generateGraphsClicked(List<GraphType> graphTypes, boolean individuals) {

        for (GraphType g : graphTypes) {

            if (individuals && transcripts.size() > 1) {
                for (Transcript t : transcripts) {
                    ResultsWindow r = new ResultsWindow();
                    displayGraph(g, new LinkedList<>(Arrays.asList(t)), r);
                    r.setInfo(g.name() + ": " + t.selection);
                    r.setVisible(true);
                }
            }

            ResultsWindow results = new ResultsWindow();
            displayGraph(g, transcripts, results);
            results.setInfo(g.name() + ": " + transcripts.get(0).selection);
            results.setVisible(true);
        }
    }


    private XYSeries getDataSeriesByGroup(Transcript t, IncidentType type, double position) {

        XYSeries series = new XYSeries(t.name + type.descr);
        t.validIncidents.stream().filter(i -> i.group == type).forEach(i -> addToSeries(series, i, position));
        return series;
    }

    private XYSeries getDataSeriesBySubGroup(Transcript t, IncidentType type, double position) {

        XYSeries series = new XYSeries(t.name + type.descr);
        t.validIncidents.stream().filter(i -> i.subgroup == type).forEach(i -> addToSeries(series, i, position));
        return series;
    }

    private void displayGraph(GraphType g, List<Transcript> transcripts, ResultsWindow r) {

        if (transcripts.isEmpty()) {
            return;
        }

        switch (g) {
            case MAIN:
                displayMainGraph(transcripts, r);
                break;
            case CONSULTS:
                boolean workplace = false;
                for (Transcript p : transcripts) {
                    if (p.workPlace) {
                        workplace = true;
                    }
                }
                List<IncidentType> categories = Consultation.SUBGROUP_GENERAL;
                if (workplace) {
                    categories.addAll(Consultation.SUBGROUP_WORKPLACE);
                }

                displayByCategories(transcripts, r, categories, "Consults graph");
                break;
            case PAUSES:
                displayByCategories(transcripts, r, Pause.SUBGROUPS, "Pauses graph");
                break;
            case INTERRUPTIONS:
                break;
            case REVISIONS:
                displayRevisionGraph(transcripts, r);
                break;
            case CUSTOM:
                displayCustomGraph(transcripts, r);
                break;
            default:
                displayMainGraph(transcripts, r);
                break;
        }
    }

    /**
     * Given the information in the transcripts, add the times to the result
     * window r so that the main graph can be displayed.
     *
     * @param transcripts list of XMLParsers, each representing a separate file
     * @param r           the result window where the graph should be displayed
     */
    private void displayMainGraph(List<Transcript> transcripts, ResultsWindow r) {

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

            t.adjustTimesToSelection();

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
     * window r so that the chosen graph can be displayed.
     *
     * @param transcripts list of XMLParsers, each representing a separate file
     * @param r       the result window where the graph should be displayed
     * @param categories a list of all of the different categories
     * @param graph_name the name of the graph
     */
    private void displayByCategories(List<Transcript> transcripts, ResultsWindow r,
                                     List<IncidentType> categories, String graph_name) {

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

            t.adjustTimesToSelection();

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
     * Given the information in the parsers, add the times to the result
     * window r so that the pauses graph can be displayed.
     *
     * @param parsers list of XMLParsers, each representing a separate file
     * @param r       the result window where the graph should be displayed
     */
    private void displayRevisionGraph(List<Transcript> parsers, ResultsWindow r) {

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

            p.adjustTimesToSelection();

            Iterator<BaseIncident> inserts = p.validIncidents.stream().filter(inc -> inc.subgroup == IncidentType.R_INSERTS).iterator();
            while (inserts.hasNext()) {
                Revision e = (Revision) inserts.next();
                if ((bothRevisions && e.revisionType == IncidentType.R_REVISION2)
                        || e.revisionType == IncidentType.R_REVISION) {
                    addToSeries(ins, e, 2 + pos);
                }
            }

            Iterator<BaseIncident> deletes = p.validIncidents.stream().filter(inc -> inc.subgroup == IncidentType.R_DELETES).iterator();
            while (deletes.hasNext()) {
                Revision e = (Revision) deletes.next();
                if ((bothRevisions && e.revisionType == IncidentType.R_REVISION2)
                        || e.revisionType == IncidentType.R_REVISION) {
                    addToSeries(del, e, 1 + pos);
                }
            }

            Iterator<BaseIncident> pastes = p.validIncidents.stream().filter(inc -> inc.subgroup == IncidentType.R_PASTES || inc.subgroup == IncidentType.R_MOVESTO).iterator();
            while (pastes.hasNext()) {
                Revision e = (Revision) pastes.next();
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

            p.adjustTimesToSelection();
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


                Iterator<BaseIncident> inserts = p.validIncidents.stream().filter(inc -> inc.subgroup == IncidentType.R_INSERTS).iterator();
                while (inserts.hasNext()) {
                    Revision e = (Revision) inserts.next();
                    if ((bothRevisions && e.revisionType == IncidentType.R_REVISION2)
                            || e.revisionType == IncidentType.R_REVISION) {
                        addToSeries(ins, e, 2 + pos);
                    }
                }

                Iterator<BaseIncident> deletes = p.validIncidents.stream().filter(inc -> inc.subgroup == IncidentType.R_DELETES).iterator();
                while (deletes.hasNext()) {
                    Revision e = (Revision) deletes.next();
                    if ((bothRevisions && e.revisionType == IncidentType.R_REVISION2)
                            || e.revisionType == IncidentType.R_REVISION) {
                        addToSeries(del, e, 1 + pos);
                    }
                }

                Iterator<BaseIncident> pastes = p.validIncidents.stream().filter(inc -> inc.subgroup == IncidentType.R_PASTES || inc.subgroup == IncidentType.R_MOVESTO).iterator();
                while (pastes.hasNext()) {
                    Revision e = (Revision) pastes.next();
                    if ((bothRevisions && e.revisionType == IncidentType.R_REVISION2)
                            || e.revisionType == IncidentType.R_REVISION) {
                        addToSeries(pas, e, 0 + pos);
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