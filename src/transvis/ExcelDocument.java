package transvis;

import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to create Excel files
 *
 * @author Sybil Ehrensberger
 */
public class ExcelDocument {

    private final static Logger LOGGER = Logger.getLogger("TransVis");
    private List<Transcript> transcriptList;
    private File ofile;
    private boolean pro;

    /**
     * Public constructor
     *
     * @param transcripts list of all of the transcripts that should be used
     * @param outputFile  the File the output should be written to
     */
    public ExcelDocument(List<Transcript> transcripts, File outputFile) {

        transcriptList = transcripts;
        ofile = outputFile;
        pro = true;
    }

    /**
     * Create an excel file with the statistics if stats is true or
     * with the data if data is true.
     *
     * @param stats true if the statistics should be saved in the excel file
     * @param data  true if the data should be saved in the excel file
     * @return the error if an error occurred
     */
    public String makeExcelFile(boolean stats, boolean data) {

        try {
            // Create an appending file handler
            FileHandler handler = new FileHandler("transvis.log");
            LOGGER.addHandler(handler);
        } catch (IOException e) {
        }

        try {
            Workbook originalWorkbook;
            if (stats) {
                LOGGER.log(Level.INFO, "Getting template.xls");
                //originalWorkbook = Workbook.getWorkbook(new File("template.xls"));
                originalWorkbook = Workbook.getWorkbook(new File(getClass().getResource("resources/template_stats.xls").toURI()));

            } else {
                LOGGER.log(Level.INFO, "Getting template_allTags.xls");
                //originalWorkbook = Workbook.getWorkbook(new File("template_allTags.xls"));
                originalWorkbook = Workbook.getWorkbook(new File(getClass().getResource("resources/template_data.xls").toURI()));
            }
            LOGGER.log(Level.INFO, "Creating new workbook");
            WritableWorkbook workbook = Workbook.createWorkbook(ofile, originalWorkbook);
            WritableSheet sheet = workbook.getSheet(0);

            if (stats) {
                fillStats(sheet);
            } else {
                fillData(sheet);
            }

            LOGGER.log(Level.INFO, "Production");
            workbook.write();
            workbook.close();
            return "";
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error with Excel...", ex);
            String error = "Error while saving statistics: " + ex.getMessage();
            return error;

        }
    }

    private void fillGenericData(WritableSheet sheet, int row, Transcript t) throws WriteException {

        // Filename
        sheet.addCell(new Label(0, row, t.name));

        // blue section
        sheet.addCell(new Label(1, row, t.participant));
        sheet.addCell(new Label(2, row, t.group));
        sheet.addCell(new Label(3, row, t.competence));
        sheet.addCell(new Label(4, row, t.version));
        sheet.addCell(new Label(5, row, t.sourcetextname));

        // yellow section
        sheet.addCell(new Label(6, row, t.recording.direction));
        sheet.addCell(new Label(7, row, t.recording.transProcessComplete));
        sheet.addCell(new Label(8, row, t.recording.kslavailable));
        sheet.addCell(new Label(9, row, t.recording.etavailable));
        sheet.addCell(new Label(10, row, t.recording.etquality));
        sheet.addCell(new Label(11, row, t.recording.concurrentVisibility));

    }

    private void fillData(WritableSheet sheet) throws WriteException {

        int i = 3;

        for (Transcript t : transcriptList) {

            // TODO: add start drafting incident
            // TODO: add start revision incident
            // TODO: add end process incident

            for (BaseIncident e : t.incidents) {

                fillGenericData(sheet, i, t);

                // green section
                sheet.addCell(new Label(12, i, e.phase)); // TODO: Assign phase at initialization!
                sheet.addCell(new Label(13, i, e.i_type));
                sheet.addCell(new Label(16, i, e.i_subtype));

                if (e.validTimes) {
                    sheet.addCell(new Number(14, i, e.start));
                    sheet.addCell(new Number(15, i, e.end));
                } else {
                    sheet.addCell(new Label(14, i, "n/a"));
                    sheet.addCell(new Label(15, i, "n/a"));
                }

                // source and item only for consultation incidents
                if (e.getClass() == Consultation.class) {
                    Consultation c = (Consultation) e;
                    sheet.addCell(new Label(17, i, c.source));
                    sheet.addCell(new Label(18, i, c.item));
                }

                // before, after and subsubtype only for revision incidents
                if (e.getClass() == Revision.class) {
                    Revision r = (Revision) e;
                    sheet.addCell(new Label(19, i, r.after));
                    sheet.addCell(new Label(20, i, r.before));
                    sheet.addCell(new Label(21, i, r.subsubtype));
                }

                i++;

            }
        }

    }

    private void fillStats(WritableSheet sheet) throws WriteException {

        int i = 3;

        for (Transcript p : transcriptList) {

            fillGenericData(sheet, i, p);

            int c = 12;

            // General lengths
            sheet.addCell(new Number(c++, i, p.totalTime)); // Process length
            sheet.addCell(new Number(c++, i, p.startDrafting)); // OP length
            sheet.addCell(new Number(c++, i, p.startRevision - p.startDrafting)); // DP length
            sheet.addCell(new Number(c++, i, p.totalTime - p.startRevision)); // RP length

            // Selection info
            sheet.addCell(new Number(c++, i, p.startSelection));
            sheet.addCell(new Number(c++, i, p.endSelection));
            sheet.addCell(new Number(c++, i, p.durationSelection));
            sheet.addCell(new Label(c++, i, p.selection));

            sheet.addCell(new Number(c++, i, countByGroup(p, IncidentType.SOURCETEXT)));

            // Pauses
            c = addPauses(sheet, p, i, c);

            // Writes & Accepts
            c = addProductions(sheet, p, i, c);

            sheet.addCell(new Number(c++, i, countByGroup(p, IncidentType.TYPOS)));

            // Revisions
            c = addRevisions(sheet, p, i, c);

            // Consults
            c = addConsults(sheet, p, i, c);

            // Interrupts
            // addInterrupts(sheet, p, i, c);

            i++;
        }

    }

    private long countByGroup(Transcript t, IncidentType group) {
        return t.incidents.stream().filter(inc -> inc.group == group).count();
    }

    private long countBySubGroup(Transcript t, IncidentType group) {
        return t.incidents.stream().filter(inc -> inc.subgroup == group).count();
    }

    private long countRevisions(List<BaseIncident> incidents, IncidentType subgroup, IncidentType revision) {
        return incidents.stream().filter(inc -> inc.subgroup == subgroup)
                .filter(inc -> ((Revision) inc).revisionType == revision).count();
    }

    private int addProductions(WritableSheet sheet, Transcript t, int row, int c) throws WriteException {

        float[] prod_stats = getStats(t.incidents.stream().filter(inc -> inc.group == IncidentType.TARGETTEXT).iterator());

        if (prod_stats[0] > 0) {
            sheet.addCell(new Number(c++, row, prod_stats[1]));
            if (t.incidents.stream().filter(inc -> inc.subgroup == IncidentType.T_WRITESHORT).count() > 0 || prod_stats[2] < 5) {
                sheet.addCell(new Label(c++, row, "< 5"));
            } else {
                sheet.addCell(new Number(c++, row, prod_stats[2]));
            }
            sheet.addCell(new Number(c++, row, prod_stats[3]));
            sheet.addCell(new Number(c++, row, prod_stats[4]));
        } else {
            for (int j = 0; j < 4; j++)
                sheet.addCell(new Label(c++, row, "n/a"));
        }

        sheet.addCell(new Number(c++, row, countByGroup(t, IncidentType.TARGETTEXT)));

        // TODO: Double check these numbers with mom!
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.T_WRITESHORT)));
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.T_WRITELONG)
                + countBySubGroup(t, IncidentType.T_WRITETYPO)));
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.T_WRITETYPO)));
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.T_MATCH)));

        return c;
    }

    private int addRevisions(WritableSheet sheet, Transcript t, int row, int c) throws WriteException {

        sheet.addCell(new Number(c++, row, countByGroup(t, IncidentType.REVISION)));
        sheet.addCell(new Number(c++, row, t.incidents.stream().filter(inc -> inc.group == IncidentType.REVISION)
                .filter(inc -> ((Revision) inc).revisionType == IncidentType.R_REVISION).count()));
        sheet.addCell(new Number(c++, row, countRevisions(t.incidents, IncidentType.R_DELETES, IncidentType.R_REVISION)));
        sheet.addCell(new Number(c++, row, countRevisions(t.incidents, IncidentType.R_DELETES, IncidentType.R_REVISION2)));
        sheet.addCell(new Number(c++, row, countRevisions(t.incidents, IncidentType.R_INSERTS, IncidentType.R_REVISION)));
        sheet.addCell(new Number(c++, row, countRevisions(t.incidents, IncidentType.R_INSERTS, IncidentType.R_REVISION2)));
        sheet.addCell(new Number(c++, row, countRevisions(t.incidents, IncidentType.R_PASTES, IncidentType.R_REVISION)));
        sheet.addCell(new Number(c++, row, countRevisions(t.incidents, IncidentType.R_PASTES, IncidentType.R_REVISION2)));
        sheet.addCell(new Number(c++, row, countRevisions(t.incidents, IncidentType.R_MOVESTO, IncidentType.R_REVISION)));
        sheet.addCell(new Number(c++, row, countRevisions(t.incidents, IncidentType.R_MOVESTO, IncidentType.R_REVISION2)));
        sheet.addCell(new Number(c++, row, countRevisions(t.incidents, IncidentType.R_UNDOES, IncidentType.R_REVISION)));
        sheet.addCell(new Number(c++, row, countRevisions(t.incidents, IncidentType.R_UNDOES, IncidentType.R_REVISION2)));

        return c;
    }

    private int addInterrupts(WritableSheet sheet, Transcript t, int row, int c) throws WriteException {

        sheet.addCell(new Number(c++, row, countByGroup(t, IncidentType.INTERRUPTION)));
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.I_PRIVATEMAIL)));
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.I_JOBMAIL)));
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.I_INTERNET)));
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.I_TASK)));
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.I_WORKFLOW)));
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.I_BREAK)));


        return c;
    }

    private int addPauses(WritableSheet sheet, Transcript t, int row, int c) throws WriteException {
        sheet.addCell(new Number(c++, row, countByGroup(t, IncidentType.PAUSE)));
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.P_SIMPLE)));
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.P_CONSULTS)));
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.P_READSTASK)));
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.P_READSST)));
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.P_READSTT)));
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.P_READSSTTT)));
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.P_UNCLEAR)));

        return c;
    }

    private int addConsults(WritableSheet sheet, Transcript t, int row, int c) throws WriteException {

        float[] stats = getStats(t.incidents.stream().filter(inc -> inc.group == IncidentType.CONSULTATION).iterator());

        if (stats[0] > 0) {
            for (int j = 1; j <= 4; j++)
                sheet.addCell(new Number(c++, row, stats[j]));
        } else {
            for (int j = 0; j < 4; j++)
                sheet.addCell(new Label(c++, row, "n/a"));
        }

        sheet.addCell(new Number(c++, row, countByGroup(t, IncidentType.CONSULTATION)));
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.C_SEARCHENG)));
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.C_ENCYCLOPEDIA)));
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.C_DICTIONARY)));
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.C_PORTALS)));
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.C_OTHER)));
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.C_TERMBANKS)));
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.C_WFCONTEXT)));
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.C_WFSTYLEGUIDE)));
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.C_WFGLOSSARY)));
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.C_WFPARALLELTEXT)));
        sheet.addCell(new Number(c++, row, countBySubGroup(t, IncidentType.C_CONCORDANCE)));

        return c;
    }

    private float[] getStats(Iterator<BaseIncident> incident_iterator) {

        float minLength = -1;
        float maxLength = -1;
        float totalTime = 0;
        float noElementsTime = 0;

        BaseIncident b;

        while (incident_iterator.hasNext()) {
            b = incident_iterator.next();

            if (b.validTimes) {

                if (minLength == -1)
                    minLength = b.length();

                minLength = Math.min(minLength, b.length());
                maxLength = Math.max(maxLength, b.length());
                totalTime += b.length();
                noElementsTime++;

            }
        }
        float avgLength = totalTime / noElementsTime;


        return new float[]{noElementsTime, totalTime, minLength, maxLength, avgLength};

    }
}
