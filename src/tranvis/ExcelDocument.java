package tranvis;

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

    private List<Transcript> transcriptList;
    private File ofile;
    private boolean pro;
    private final static Logger LOGGER = Logger.getLogger("TranVis");

    /**
     * Public constructor
     *
     * @param transcripts list of all of the transcripts that should be used
     * @param outputFile the File the output should be written to
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
     * @param data true if the data should be saved in the excel file
     * @return the error if an error occurred
     */
    public String makeExcelFile(boolean stats, boolean data) {

        try {
            // Create an appending file handler
            FileHandler handler = new FileHandler("tranvis.log");
            LOGGER.addHandler(handler);
        } catch (IOException e) {
        }

        try {
            Workbook originalWorkbook;
            if (stats) {
                LOGGER.log(Level.INFO, "Getting template.xls");
                originalWorkbook = Workbook.getWorkbook(new File("template.xls"));
            } else {
                LOGGER.log(Level.INFO, "Getting template_allTags.xls");
                originalWorkbook = Workbook.getWorkbook(new File("template_allTags.xls"));
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

    private void fillData(WritableSheet sheet) throws WriteException {

        Label label;
        Number num;

        int i = 3;

        for (Transcript t : transcriptList) {
            for (BaseIncident e : t.incidents) {

                label = new Label(0, i, t.name);
                sheet.addCell(label);

                // blue section
                label = new Label(1, i, t.participant);
                sheet.addCell(label);
                label = new Label(2, i, t.group);
                sheet.addCell(label);
                label = new Label(3, i, t.competence);
                sheet.addCell(label);
                label = new Label(4, i, t.version);
                sheet.addCell(label);
                label = new Label(5, i, t.sourcetextname);
                sheet.addCell(label);
                label = new Label(6, i, t.experiment);
                sheet.addCell(label);

                // TODO: Check times here!
                // yellow section
                label = new Label(7, i, t.recording.direction);
                sheet.addCell(label);
                num = new Number(8, i, 0);
                sheet.addCell(num);
                num = new Number(9, i, t.totalTime);
                sheet.addCell(num);
                label = new Label(10, i, t.recording.transProcessComplete);
                sheet.addCell(label);
                if (t.startRevision == t.totalTime) {
                    label = new Label(11, i, "n/a");
                    sheet.addCell(label);
                } else {
                    num = new Number(11, i, t.startRevision);
                    sheet.addCell(num);
                }
                label = new Label(12, i, t.recording.kslavailable);
                sheet.addCell(label);
                label = new Label(13, i, t.recording.etavailable);
                sheet.addCell(label);
                label = new Label(14, i, t.recording.etquality);
                sheet.addCell(label);
                label = new Label(15, i, t.recording.concurrentVisibility);
                sheet.addCell(label);

                // orange section
                num = new Number(16, i, t.startDrafting);
                sheet.addCell(num);

                // green section
                label = new Label(17, i, e.i_type);
                sheet.addCell(label);
                if (e.validTimes) {
                    num = new Number(18, i, e.start);
                    sheet.addCell(num);
                    num = new Number(19, i, e.end);
                    sheet.addCell(num);
                } else {
                    label = new Label(18, i, "n/a");
                    sheet.addCell(label);
                    label = new Label(19, i, "n/a");
                    sheet.addCell(label);
                }

                label = new Label(20, i, e.i_subtype);
                sheet.addCell(label);
                if (e.getClass() == Consultation.class) {
                    Consultation c = (Consultation) e;
                    label = new Label(21, i, c.source);
                    sheet.addCell(label);
                    label = new Label(22, i, c.item);
                    sheet.addCell(label);
                }

                if (e.getClass() == Revision.class) {
                    Revision r = (Revision) e;
                    label = new Label(23, i, r.after);
                    sheet.addCell(label);
                    label = new Label(24, i, r.before);
                    sheet.addCell(label);
                }

                // TODO: find out what subsubtype is
                //label = new Label(25, i, e.subsubtype);
                //sheet.addCell(label);

                i++;

            }
        }

    }

    private void fillStats(WritableSheet sheet) throws WriteException {

        Label label;
        Number num;

        int i = 4;

        for (Transcript p : transcriptList) {


            // Basic information

            label = new Label(0, i, p.name);
            sheet.addCell(label);

            // TODO: check times!
            float starttime = p.startAdjustment;
            float endtime = p.totalTime;

            num = new Number(1, i, starttime);
            sheet.addCell(num);
            num = new Number(2, i, endtime);
            sheet.addCell(num);
            num = new Number(3, i, endtime - starttime);
            sheet.addCell(num);

            label = new Label(4, i, p.selection);
            sheet.addCell(label);

            // Pauses
            addPauses(sheet, p, i);

            // Consults
            addConsults(sheet, p, i);

            // Typos
            num = new Number(34, i, p.incidents.stream().filter(inc -> inc.group == IncidentType.TYPOS).count());
            sheet.addCell(num);

            // Revisions
            addRevisions(sheet, p, i);

            // Writes & Accepts
            addProductions(sheet, p, i);

            // Sourcetext
            num = new Number(51, i, p.incidents.stream().filter(inc -> inc.group == IncidentType.SOURCETEXT).count());
            sheet.addCell(num);

            // Interrupts
            if (false) {
                addInterrupts(sheet, p, i);
            }

            // End information
            num = new Number(52, i, p.totalTime);
            sheet.addCell(num);

            label = new Label(53, i, p.recording.transProcessComplete);
            sheet.addCell(label);
            if (p.startRevision == p.totalTime) {
                label = new Label(54, i, "n.a.");
                sheet.addCell(label);
            } else {
                num = new Number(54, i, p.startRevision);
                sheet.addCell(num);
            }
            label = new Label(55, i, p.recording.direction);
            sheet.addCell(label);
            label = new Label(56, i, p.recording.concurrentVisibility);
            sheet.addCell(label);

            i++;
        }

    }

    private void addProductions(WritableSheet sheet, Transcript p, int i) throws WriteException {


        Number num;
        Label label;

        num = new Number(47, i, countByGroup(p, IncidentType.PRODUCTION));
        sheet.addCell(num);

        // TODO: Figure out what these numbers are supposed to be!
        num = new Number(48, i, countBySubGroup(p, IncidentType.PR_WRITESHORT));
        sheet.addCell(num);
        num = new Number(49, i, countBySubGroup(p, IncidentType.PR_WRITELONG)
                + countBySubGroup(p, IncidentType.PR_WRITETYPO));
        sheet.addCell(num);
        num = new Number(50, i, countBySubGroup(p, IncidentType.PR_WRITETYPO));
        sheet.addCell(num);


        float[] prod_stats = getStats(p.incidents.stream().filter(inc -> inc.subgroup == IncidentType.PR_WRITELONG ||
                                                                    inc.subgroup == IncidentType.PR_WRITETYPO).iterator());

        float[] pr_short_stats = getStats(p.incidents.stream().filter(inc -> inc.subgroup == IncidentType.PR_WRITESHORT).iterator());

        if (prod_stats[0] > 0 || pr_short_stats[0] > 0) {
            if (p.startDrafting == p.totalTime) {
                label = new Label(9, i, "n/a");
                sheet.addCell(label);
            } else {
                num = new Number(9, i, p.startDrafting);
                sheet.addCell(num);
            }
        } else {
            label = new Label(9, i, "n/a");
            sheet.addCell(label);
        }


        if (prod_stats[0] > 0) {
            num = new Number(10, i, prod_stats[1]);
            sheet.addCell(num);
            if (p.incidents.stream().filter(inc -> inc.subgroup == IncidentType.PR_WRITESHORT).count() > 0 || prod_stats[2] < 5) {
                label = new Label(11, i, "< 5");
                sheet.addCell(label);
            } else {
                num = new Number(11, i, prod_stats[2]);
                sheet.addCell(num);
            }
            num = new Number(12, i, prod_stats[3]);
            sheet.addCell(num);
            num = new Number(13, i, prod_stats[4]);
            sheet.addCell(num);
        } else {

            label = new Label(10, i, "n/a");
            sheet.addCell(label);
            label = new Label(11, i, "n/a");
            sheet.addCell(label);
            label = new Label(12, i, "n/a");
            sheet.addCell(label);
            label = new Label(13, i, "n/a");
            sheet.addCell(label);
        }

    }

    private long countRevisions(List<BaseIncident> incidents, IncidentType subgroup, IncidentType revision) {
        return incidents.stream().filter(inc -> inc.subgroup == subgroup)
                .filter(inc -> ((Revision) inc).revisionType == revision).count();
    }

    private void addRevisions(WritableSheet sheet, Transcript p, int i) throws WriteException {
        Number num = new Number(0, 0, 0);

        num = new Number(35, i, countByGroup(p, IncidentType.REVISION));
        sheet.addCell(num);
        num = new Number(36, i, p.incidents.stream().filter(inc -> inc.group == IncidentType.REVISION)
                                                    .filter(inc -> ((Revision) inc).revisionType == IncidentType.R_REVISION).count());
        sheet.addCell(num);

        num = new Number(37, i, countRevisions(p.incidents, IncidentType.R_DELETES, IncidentType.R_REVISION));
        sheet.addCell(num);
        num = new Number(38, i, countRevisions(p.incidents, IncidentType.R_DELETES, IncidentType.R_REVISION2));
        sheet.addCell(num);
        num = new Number(39, i, countRevisions(p.incidents, IncidentType.R_INSERTS, IncidentType.R_REVISION));
        sheet.addCell(num);
        num = new Number(40, i, countRevisions(p.incidents, IncidentType.R_INSERTS, IncidentType.R_REVISION2));
        sheet.addCell(num);
        num = new Number(41, i, countRevisions(p.incidents, IncidentType.R_PASTES, IncidentType.R_REVISION));
        sheet.addCell(num);
        num = new Number(42, i, countRevisions(p.incidents, IncidentType.R_PASTES, IncidentType.R_REVISION2));
        sheet.addCell(num);
        num = new Number(43, i, countRevisions(p.incidents, IncidentType.R_MOVESTO, IncidentType.R_REVISION));
        sheet.addCell(num);
        num = new Number(44, i, countRevisions(p.incidents, IncidentType.R_MOVESTO, IncidentType.R_REVISION2));
        sheet.addCell(num);
        num = new Number(45, i, countRevisions(p.incidents, IncidentType.R_UNDOES, IncidentType.R_REVISION));
        sheet.addCell(num);
        num = new Number(46, i, countRevisions(p.incidents, IncidentType.R_UNDOES, IncidentType.R_REVISION2));
        sheet.addCell(num);

    }

    private long countByGroup(Transcript t, IncidentType group) {
        return t.incidents.stream().filter(inc -> inc.group == group).count();
    }

    private long countBySubGroup(Transcript t, IncidentType group) {
        return t.incidents.stream().filter(inc -> inc.subgroup == group).count();
    }

    private void addInterrupts(WritableSheet sheet, Transcript p, int i) throws WriteException {
        Number num;

        num = new Number(55, i, countByGroup(p, IncidentType.INTERRUPTION));
        sheet.addCell(num);
        num = new Number(56, i, countBySubGroup(p, IncidentType.I_PRIVATEMAIL));
        sheet.addCell(num);
        num = new Number(57, i, countBySubGroup(p, IncidentType.I_JOBMAIL));
        sheet.addCell(num);
        num = new Number(58, i, countBySubGroup(p, IncidentType.I_INTERNET));
        sheet.addCell(num);
        num = new Number(59, i, countBySubGroup(p, IncidentType.I_TASK));
        sheet.addCell(num);
        num = new Number(60, i, countBySubGroup(p, IncidentType.I_WORKFLOW));
        sheet.addCell(num);
        num = new Number(61, i, countBySubGroup(p, IncidentType.I_BREAK));
        sheet.addCell(num);

    }

    private void addPauses(WritableSheet sheet, Transcript p, int i) throws WriteException {
        Number num;

        num = new Number(14, i, countByGroup(p, IncidentType.PAUSE));
        sheet.addCell(num);
        num = new Number(15, i, countBySubGroup(p, IncidentType.P_SIMPLE));
        sheet.addCell(num);
        num = new Number(16, i, countBySubGroup(p, IncidentType.P_CONSULTS));
        sheet.addCell(num);
        num = new Number(17, i, countBySubGroup(p, IncidentType.P_READSTASK));
        sheet.addCell(num);
        num = new Number(18, i, countBySubGroup(p, IncidentType.P_READSST));
        sheet.addCell(num);
        num = new Number(19, i, countBySubGroup(p, IncidentType.P_READSTT));
        sheet.addCell(num);
        num = new Number(20, i, countBySubGroup(p, IncidentType.P_READSSTTT));
        sheet.addCell(num);
        num = new Number(21, i, countBySubGroup(p, IncidentType.P_UNCLEAR));
        sheet.addCell(num);
    }

    private void addConsults(WritableSheet sheet, Transcript p, int i) throws WriteException {
        Number num;

        num = new Number(22, i, countByGroup(p, IncidentType.CONSULTATION));
        sheet.addCell(num);
        num = new Number(23, i, countBySubGroup(p, IncidentType.C_SEARCHENG));
        sheet.addCell(num);
        num = new Number(24, i, countBySubGroup(p, IncidentType.C_ENCYCLOPEDIA));
        sheet.addCell(num);
        num = new Number(25, i, countBySubGroup(p, IncidentType.C_DICTIONARY));
        sheet.addCell(num);
        num = new Number(26, i, countBySubGroup(p, IncidentType.C_PORTALS));
        sheet.addCell(num);
        num = new Number(33, i, countBySubGroup(p, IncidentType.C_OTHER));
        sheet.addCell(num);
        num = new Number(27, i, countBySubGroup(p, IncidentType.C_TERMBANKS));
        sheet.addCell(num);
        num = new Number(28, i, countBySubGroup(p, IncidentType.C_WFCONTEXT));
        sheet.addCell(num);
        num = new Number(29, i, countBySubGroup(p, IncidentType.C_WFSTYLEGUIDE));
        sheet.addCell(num);
        num = new Number(30, i, countBySubGroup(p, IncidentType.C_WFGLOSSARY));
        sheet.addCell(num);
        num = new Number(31, i, countBySubGroup(p, IncidentType.C_WFPARALLELTEXT));
        sheet.addCell(num);
        num = new Number(32, i, countBySubGroup(p, IncidentType.C_CONCORDANCE));
        sheet.addCell(num);

        float[] stats = getStats(p.incidents.stream().filter(inc -> inc.group == IncidentType.CONSULTATION).iterator());

        if (stats[0] > 0) {
            num = new Number(5, i, stats[1]);
            sheet.addCell(num);
            num = new Number(6, i, stats[2]);
            sheet.addCell(num);
            num = new Number(7, i, stats[3]);
            sheet.addCell(num);
            num = new Number(8, i, stats[4]);
            sheet.addCell(num);
        } else {
            Label label = new Label(5, i, "n/a");
            sheet.addCell(label);
            label = new Label(6, i, "n/a");
            sheet.addCell(label);
            label = new Label(7, i, "n/a");
            sheet.addCell(label);
            label = new Label(8, i, "n/a");
            sheet.addCell(label);
        }


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


        return new float[]{ noElementsTime, totalTime, minLength, maxLength, avgLength };

    }
}
