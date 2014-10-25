package tranvis;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.*;
import jxl.write.*;
import jxl.write.Number;

/**
 *
 * @author Sybil Ehrensberger
 */
public class ExcelDocument {

    private List<Transcript> parserList;
    private File ofile;
    private boolean pro;
    private final static Logger LOGGER = Logger.getLogger("TranVis");

    ExcelDocument(List<Transcript> parsers, File outputfile) {

        parserList = parsers;
        ofile = outputfile;
        pro = true;
    }

    public String makeExcelFile(boolean stats, boolean data) {

        try {
            // Create an appending file handler
            FileHandler handler = new FileHandler("tranvis.log");
            LOGGER.addHandler(handler);
        } catch (IOException e) {
        }

        try {
            Workbook originalworkbook;
            if (stats) {
                LOGGER.log(Level.INFO, "Getting template.xls");
                originalworkbook = Workbook.getWorkbook(new File("template.xls"));
            } else {
                LOGGER.log(Level.INFO, "Getting template_allTags.xls");
                originalworkbook = Workbook.getWorkbook(new File("template_allTags.xls"));
            }
            LOGGER.log(Level.INFO, "Creating new workbook");
            WritableWorkbook workbook = Workbook.createWorkbook(ofile, originalworkbook);
            WritableSheet sheet = workbook.getSheet(0);

            if (stats) {
                fillStats(sheet);
            } else {
                fillData(sheet);
            }

            LOGGER.log(Level.INFO, "Writing");
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

        for (Transcript t : parserList) {
            for (Incident e : t.allIncidents.elements) {

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

                // yellow section
                label = new Label(7, i, t.direction);
                sheet.addCell(label);
                num = new Number(8, i, t.startProcess);
                sheet.addCell(num);
                num = new Number(9, i, t.endProcess);
                sheet.addCell(num);
                label = new Label(10, i, t.complete);
                sheet.addCell(label);
                if (t.startRevision == t.endProcess) {
                    label = new Label(11, i, "n/a");
                    sheet.addCell(label);
                } else {
                    num = new Number(11, i, t.startRevision);
                    sheet.addCell(num);
                }
                label = new Label(12, i, t.kslavailable);
                sheet.addCell(label);
                label = new Label(13, i, t.etavailable);
                sheet.addCell(label);
                label = new Label(14, i, t.etquality);
                sheet.addCell(label);
                label = new Label(15, i, t.concurrentVisibility);
                sheet.addCell(label);

                // orange section
                num = new Number(16, i, t.startDrafting);
                sheet.addCell(num);

                // green section
                label = new Label(17, i, e.type);
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

                label = new Label(20, i, e.subtype);
                sheet.addCell(label);
                label = new Label(21, i, e.src);
                sheet.addCell(label);
                label = new Label(22, i, e.item);
                sheet.addCell(label);
                label = new Label(23, i, e.after);
                sheet.addCell(label);
                label = new Label(24, i, e.before);
                sheet.addCell(label);
                label = new Label(25, i, e.subsubtype);
                sheet.addCell(label);

                i++;

            }
        }

    }

    private void fillStats(WritableSheet sheet) throws WriteException {

        Label label;
        Number num;

        int i = 4;

        for (Transcript p : parserList) {


            // Basic information

            label = new Label(0, i, p.name);
            sheet.addCell(label);

            float starttime = p.startAdjustment - p.startProcess;
            float endtime = p.endAdjustment - p.startProcess;

            num = new Number(1, i, starttime);
            sheet.addCell(num);
            num = new Number(2, i, endtime);
            sheet.addCell(num);
            num = new Number(3, i, endtime - starttime);
            sheet.addCell(num);

            label = new Label(4, i, p.timespan);
            sheet.addCell(label);

            // Pauses
            addPauses(sheet, p, i);

            // Consults
            addConsults(sheet, p, i);

            // Typos
            num = new Number(34, i, p.incidentlists.get(IncidentType.TYPOS).noElements());
            sheet.addCell(num);

            // Revisions
            addRevisions(sheet, p, i);

            // Writes & Accepts
            addProductions(sheet, p, i);

            // Sourcetext
            num = new Number(51, i, p.incidentlists.get(IncidentType.SOURCETEXT).noElements());
            sheet.addCell(num);

            // Interrupts
            if (false) {
                addInterrupts(sheet, p, i);
            }

            // End information
            num = new Number(52, i, p.lengthProcess);
            sheet.addCell(num);

            label = new Label(53, i, p.complete);
            sheet.addCell(label);
            if (p.startRevision == p.endProcess) {
                label = new Label(54, i, "n.a.");
                sheet.addCell(label);
            } else {
                num = new Number(54, i, p.startRevision - p.startProcess);
                sheet.addCell(num);
            }
            label = new Label(55, i, p.direction);
            sheet.addCell(label);
            label = new Label(56, i, p.concurrentVisibility);
            sheet.addCell(label);

            i++;
        }

    }

    private void addProductions(WritableSheet sheet, Transcript p, int i) throws WriteException {


        Number num = new Number(0, 0, 0);
        Label label;

        num = new Number(47, i, p.incidentlists.get(IncidentType.PRODUCTION).noElements());
        sheet.addCell(num);

        num = new Number(48, i, p.incidentlists.get(IncidentType.PR_WRITESHORT).noElements());
        sheet.addCell(num);
        num = new Number(49, i, p.incidentlists.get(IncidentType.PR_WRITELONG).noElements()
                + p.incidentlists.get(IncidentType.PR_WRITETYPO).noElements());
        sheet.addCell(num);
        num = new Number(50, i, p.incidentlists.get(IncidentType.PR_WRITETYPO).noElements());
        sheet.addCell(num);


        IncidentList productions = IncidentList.addLists(
                p.incidentlists.get(IncidentType.PR_WRITELONG),
                p.incidentlists.get(IncidentType.PR_WRITETYPO));

        productions.getStats();
        p.incidentlists.get(IncidentType.PR_WRITESHORT).getStats();
        
        if (productions.noElements() > 0 || p.incidentlists.get(IncidentType.PR_WRITESHORT).noElementsTime > 0) {
            if (p.startDrafting == p.endProcess) {
                label = new Label(9, i, "n/a");
                sheet.addCell(label);
            } else {
                num = new Number(9, i, p.startDrafting - p.startProcess);
                sheet.addCell(num);
            }
        } else {
            label = new Label(9, i, "n/a");
            sheet.addCell(label);
        }


        if (productions.noElements() > 0) {
            num = new Number(10, i, productions.getTotalTime());
            sheet.addCell(num);
            if (p.incidentlists.get(IncidentType.PR_WRITESHORT).noElements() > 0
                    || productions.getMinLength() < 5) {
                label = new Label(11, i, "< 5");
                sheet.addCell(label);
            } else {
                num = new Number(11, i, productions.getMinLength());
                sheet.addCell(num);
            }
            num = new Number(12, i, productions.getMaxLength());
            sheet.addCell(num);
            num = new Number(13, i, productions.getAvgLength());
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

    /**
     *
     * @param sheet
     * @param p
     * @param i
     * @param j
     */
    private void addRevisions(WritableSheet sheet, Transcript p, int i) throws WriteException {
        Number num = new Number(0, 0, 0);

        num = new Number(35, i, p.incidentlists.get(IncidentType.REVISION).noElements());
        sheet.addCell(num);
        num = new Number(36, i, p.incidentlists.get(IncidentType.REVISION).sublist(IncidentType.R_REVISION).noElements());
        sheet.addCell(num);

        num = new Number(37, i, p.incidentlists.get(IncidentType.R_DELETES).sublist(IncidentType.R_REVISION).noElements());
        sheet.addCell(num);
        num = new Number(38, i, p.incidentlists.get(IncidentType.R_DELETES).sublist(IncidentType.R_REVISION2).noElements());
        sheet.addCell(num);
        num = new Number(39, i, p.incidentlists.get(IncidentType.R_INSERTS).sublist(IncidentType.R_REVISION).noElements());
        sheet.addCell(num);
        num = new Number(40, i, p.incidentlists.get(IncidentType.R_INSERTS).sublist(IncidentType.R_REVISION2).noElements());
        sheet.addCell(num);
        num = new Number(41, i, p.incidentlists.get(IncidentType.R_PASTES).sublist(IncidentType.R_REVISION).noElements());
        sheet.addCell(num);
        num = new Number(42, i, p.incidentlists.get(IncidentType.R_PASTES).sublist(IncidentType.R_REVISION2).noElements());
        sheet.addCell(num);
        num = new Number(43, i, p.incidentlists.get(IncidentType.R_MOVESTO).sublist(IncidentType.R_REVISION).noElements());
        sheet.addCell(num);
        num = new Number(44, i, p.incidentlists.get(IncidentType.R_MOVESTO).sublist(IncidentType.R_REVISION2).noElements());
        sheet.addCell(num);
        num = new Number(45, i, p.incidentlists.get(IncidentType.R_UNDOES).sublist(IncidentType.R_REVISION).noElements());
        sheet.addCell(num);
        num = new Number(46, i, p.incidentlists.get(IncidentType.R_UNDOES).sublist(IncidentType.R_REVISION2).noElements());
        sheet.addCell(num);

    }

    /**
     *
     * @param sheet
     * @param p
     * @param i
     * @param j
     * @throws WriteException
     */
    private void addInterrupts(WritableSheet sheet, Transcript p, int i) throws WriteException {
        Number num = new Number(0, 0, 0);

        num = new Number(55, i, p.incidentlists.get(IncidentType.INTERRUPTION).noElements());
        sheet.addCell(num);
        num = new Number(56, i, p.incidentlists.get(IncidentType.I_PRIVATEMAIL).noElements());
        sheet.addCell(num);
        num = new Number(57, i, p.incidentlists.get(IncidentType.I_JOBMAIL).noElements());
        sheet.addCell(num);
        num = new Number(58, i, p.incidentlists.get(IncidentType.I_INTERNET).noElements());
        sheet.addCell(num);
        num = new Number(59, i, p.incidentlists.get(IncidentType.I_TASK).noElements());
        sheet.addCell(num);
        num = new Number(60, i, p.incidentlists.get(IncidentType.I_WORKFLOW).noElements());
        sheet.addCell(num);
        num = new Number(61, i, p.incidentlists.get(IncidentType.I_BREAK).noElements());
        sheet.addCell(num);

    }

    /**
     *
     * @param sheet
     * @param p
     * @param i
     * @param j
     * @throws WriteException
     */
    private void addPauses(WritableSheet sheet, Transcript p, int i) throws WriteException {
        Number num = new Number(0, 0, 0);

        num = new Number(14, i, p.incidentlists.get(IncidentType.PAUSE).noElements());
        sheet.addCell(num);
        num = new Number(15, i, p.incidentlists.get(IncidentType.P_SIMPLE).noElements());
        sheet.addCell(num);
        num = new Number(16, i, p.incidentlists.get(IncidentType.P_CONSULTS).noElements());
        sheet.addCell(num);
        num = new Number(17, i, p.incidentlists.get(IncidentType.P_READSTASK).noElements());
        sheet.addCell(num);
        num = new Number(18, i, p.incidentlists.get(IncidentType.P_READSST).noElements());
        sheet.addCell(num);
        num = new Number(19, i, p.incidentlists.get(IncidentType.P_READSTT).noElements());
        sheet.addCell(num);
        num = new Number(20, i, p.incidentlists.get(IncidentType.P_READSSTTT).noElements());
        sheet.addCell(num);
        num = new Number(21, i, p.incidentlists.get(IncidentType.P_UNCLEAR).noElements());
        sheet.addCell(num);
    }

    /**
     *
     * @param sheet
     * @param p
     * @param i
     * @param j
     * @throws WriteException
     */
    private void addConsults(WritableSheet sheet, Transcript p, int i) throws WriteException {
        Number num = new Number(0, 0, 0);

        num = new Number(22, i, p.incidentlists.get(IncidentType.CONSULTATION).noElements());
        sheet.addCell(num);
        num = new Number(23, i, p.incidentlists.get(IncidentType.C_SEARCHENG).noElements());
        sheet.addCell(num);
        num = new Number(24, i, p.incidentlists.get(IncidentType.C_ENCYCLOPEDIA).noElements());
        sheet.addCell(num);
        num = new Number(25, i, p.incidentlists.get(IncidentType.C_DICTIONARY).noElements());
        sheet.addCell(num);
        num = new Number(26, i, p.incidentlists.get(IncidentType.C_PORTALS).noElements());
        sheet.addCell(num);
        num = new Number(33, i, p.incidentlists.get(IncidentType.C_OTHER).noElements());
        sheet.addCell(num);
        num = new Number(27, i, p.incidentlists.get(IncidentType.C_TERMBANKS).noElements());
        sheet.addCell(num);
        num = new Number(28, i, p.incidentlists.get(IncidentType.C_WFCONTEXT).noElements());
        sheet.addCell(num);
        num = new Number(29, i, p.incidentlists.get(IncidentType.C_WFSTYLEGUIDE).noElements());
        sheet.addCell(num);
        num = new Number(30, i, p.incidentlists.get(IncidentType.C_WFGLOSSARY).noElements());
        sheet.addCell(num);
        num = new Number(31, i, p.incidentlists.get(IncidentType.C_WFPARALLELTEXT).noElements());
        sheet.addCell(num);
        num = new Number(32, i, p.incidentlists.get(IncidentType.C_CONCORDANCE).noElements());
        sheet.addCell(num);

        p.incidentlists.get(IncidentType.CONSULTATION).getStats();

        if (p.incidentlists.get(IncidentType.CONSULTATION).noElementsTime > 0) {
            num = new Number(5, i, p.incidentlists.get(IncidentType.CONSULTATION).getTotalTime());
            sheet.addCell(num);
            num = new Number(6, i, p.incidentlists.get(IncidentType.CONSULTATION).getMinLength());
            sheet.addCell(num);
            num = new Number(7, i, p.incidentlists.get(IncidentType.CONSULTATION).getMaxLength());
            sheet.addCell(num);
            num = new Number(8, i, p.incidentlists.get(IncidentType.CONSULTATION).getAvgLength());
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
}
