package transcriptvisualizer;

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
    
    private void fillData(WritableSheet sheet) {
        
    }
    
    private void fillStats(WritableSheet sheet) throws WriteException {

        Label label;
        Number num;
        Formula f;

        int i = 4;

        for (Transcript p : parserList) {


            // Basic information

            label = new Label(0, i, p.name);
            sheet.addCell(label);

            int starttime = p.startAdjustment - p.startProcess;
            int endtime = p.endAdjustment - p.startProcess;

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
            p.typos.getStats();
            num = new Number(34, i, p.typos.getTotalNum());
            sheet.addCell(num);

            // Revisions
            addRevisions(sheet, p, i);

            // Writes & Accepts
            addProductions(sheet, p, i);

            // Sourcetext
            p.sourcetext.getStats();
            num = new Number(51, i, p.sourcetext.getTotalNum());
            sheet.addCell(num);

            // Interrupts
            if (false) {
                addInterrupts(sheet, p, i);
            }

            // End information
            num = new Number(52, i, p.lengthProcess);
            sheet.addCell(num);
            
            String complete;
            if (p.complete) {
                complete = "yes";
            } else {
                complete = "no";
            }
            label = new Label(53, i, complete);
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
            String concurrent;
            if (p.concurrentVisibility) {
                concurrent = "yes";
            } else {
                concurrent = "no";
            }

            label = new Label(56, i, concurrent);
            sheet.addCell(label);

            i++;
        }

    }

    private void addProductions(WritableSheet sheet, Transcript p, int i) throws WriteException {

        Number num = new Number(0, 0, 0);
        Label label;

        for (IncidentList t : p.productions) {
            t.getStats();
        }

        // all writing 51
        num = new Number(47, i, p.productions.get(0).getTotalNum()
                + p.productions.get(1).getTotalNum() + p.productions.get(2).getTotalNum());
        sheet.addCell(num);

        num = new Number(48, i, p.productions.get(0).getTotalNum());
        sheet.addCell(num);
        num = new Number(49, i, p.productions.get(1).getTotalNum() + p.productions.get(2).getTotalNum());
        sheet.addCell(num);
        num = new Number(50, i, p.productions.get(2).getTotalNum());
        sheet.addCell(num);


        int totalnum1 = p.productions.get(1).getTotalNum();
        int totalnum2 = p.productions.get(2).getTotalNum();
        int totaltime1, totaltime2;
        int minlength1, minlength2;
        int maxlength1, maxlength2;
        if (totalnum1 > 0) {
            totaltime1 = p.productions.get(1).getTotalTime();
            minlength1 = p.productions.get(1).getMinLength();
            maxlength1 = p.productions.get(1).getMaxLength();
        } else {
            totaltime1 = 0;
            minlength1 = 99999;
            maxlength1 = 0;
        }

        if (totalnum2 > 0) {
            totaltime2 = p.productions.get(2).getTotalTime();
            minlength2 = p.productions.get(2).getMinLength();
            maxlength2 = p.productions.get(2).getMaxLength();
        } else {
            totaltime2 = 0;
            minlength2 = 99999;
            maxlength2 = 0;
        }

        int totaltime = totaltime1 + totaltime2;
        int maxlength = Math.max(maxlength1, maxlength2);
        int minlength = Math.min(minlength1, minlength2);

        if (totalnum1 > 0 || totalnum2 > 0 || p.productions.get(0).getTotalNum() > 0) {
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


        if (p.productions.get(0).getTotalNum() > 0 || minlength < 5) {
            label = new Label(11, i, "< 5");
            sheet.addCell(label);
        } else {
            num = new Number(11, i, minlength);
        }

        if (totalnum1 > 0 || totalnum2 > 0) {
            num = new Number(10, i, totaltime);
            sheet.addCell(num);
            num = new Number(12, i, maxlength);
            sheet.addCell(num);
            double avglength = (double) totaltime / (totalnum1 + totalnum2);
            num = new Number(13, i, avglength);
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
        Number num1 = new Number(0, 0, 0);

        int numrev = 0;
        int numrev2 = 0;

        for (IncidentList t : p.revisions) {
            t.getStats();
            if (t.subtype.equalsIgnoreCase("revision")) {
                numrev += t.getTotalNum();
            } else if (t.subtype.equalsIgnoreCase("revision2")) {
                numrev2 += t.getTotalNum();
            }

            if (t.type.equalsIgnoreCase("deletes") && t.subtype.equalsIgnoreCase("revision")) {
                num1 = new Number(37, i, t.getTotalNum());
            } else if (t.type.equalsIgnoreCase("deletes") && t.subtype.equalsIgnoreCase("revision2")) {
                num1 = new Number(38, i, t.getTotalNum());
            } else if (t.type.equalsIgnoreCase("inserts") && t.subtype.equalsIgnoreCase("revision")) {
                num1 = new Number(39, i, t.getTotalNum());
            } else if (t.type.equalsIgnoreCase("inserts") && t.subtype.equalsIgnoreCase("revision2")) {
                num1 = new Number(40, i, t.getTotalNum());
            } else if (t.type.equalsIgnoreCase("pastes") && t.subtype.equalsIgnoreCase("revision")) {
                num1 = new Number(41, i, t.getTotalNum());
            } else if (t.type.equalsIgnoreCase("pastes") && t.subtype.equalsIgnoreCase("revision2")) {
                num1 = new Number(42, i, t.getTotalNum());
            } else if (t.type.equalsIgnoreCase("moves to") && t.subtype.equalsIgnoreCase("revision")) {
                num1 = new Number(43, i, t.getTotalNum());
            } else if (t.type.equalsIgnoreCase("moves to") && t.subtype.equalsIgnoreCase("revision2")) {
                num1 = new Number(44, i, t.getTotalNum());
            } else if (t.type.equalsIgnoreCase("undoes") && t.subtype.equalsIgnoreCase("revision")) {
                num1 = new Number(45, i, t.getTotalNum());
            } else if (t.type.equalsIgnoreCase("undoes") && t.subtype.equalsIgnoreCase("revision2")) {
                num1 = new Number(46, i, t.getTotalNum());
            }
            sheet.addCell(num1);
        }

        num1 = new Number(35, i, numrev2 + numrev);
        sheet.addCell(num1);
        num1 = new Number(36, i, numrev);
        sheet.addCell(num1);
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
        Number num1 = new Number(0, 0, 0);

        int total = 0;

        for (IncidentList t : p.interrupts) {
            t.getStats();

            if (t.subtype.equalsIgnoreCase("privatemail")) {
                num1 = new Number(56, i, t.getTotalNum());
            } else if (t.subtype.equalsIgnoreCase("jobmail")) {
                num1 = new Number(57, i, t.getTotalNum());
            } else if (t.subtype.equalsIgnoreCase("internet")) {
                num1 = new Number(58, i, t.getTotalNum());
            } else if (t.subtype.equalsIgnoreCase("task")) {
                num1 = new Number(59, i, t.getTotalNum());
            } else if (t.subtype.equalsIgnoreCase("workflow")) {
                num1 = new Number(60, i, t.getTotalNum());
            } else if (t.subtype.equalsIgnoreCase("break")) {
                num1 = new Number(61, i, t.getTotalNum());
            }
            total = total + t.getTotalNum();

            sheet.addCell(num1);
        }

        num1 = new Number(55, i, total);
        sheet.addCell(num1);
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
        Number num1 = new Number(0, 0, 0);

        int total = 0;

        for (IncidentList t : p.pauses) {
            t.getStats();
            if (t.type.equalsIgnoreCase("pause")) {
                num1 = new Number(15, i, t.getTotalNum());
            } else if (t.subtype.equalsIgnoreCase("consults")) {
                num1 = new Number(16, i, t.getTotalNum());
            } else if (t.subtype.equalsIgnoreCase("readsTask")) {
                num1 = new Number(17, i, t.getTotalNum());
            } else if (t.subtype.equalsIgnoreCase("readsST")) {
                num1 = new Number(18, i, t.getTotalNum());
            } else if (t.subtype.equalsIgnoreCase("readsTT")) {
                num1 = new Number(19, i, t.getTotalNum());
            } else if (t.subtype.equalsIgnoreCase("readsST+TT")) {
                num1 = new Number(20, i, t.getTotalNum());
            } else if (t.subtype.equalsIgnoreCase("unclear")) {
                num1 = new Number(21, i, t.getTotalNum());
            }
            total = total + t.getTotalNum();
            sheet.addCell(num1);
        }

        num1 = new Number(14, i, total);
        sheet.addCell(num1);
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
        Number num1 = new Number(0, 0, 0);

        int totaltime = 0;
        int totalnum = 0;
        int shortest = p.lengthProcess;
        int longest = 0;


        for (IncidentList t : p.consults) {
            t.getStats();

            totalnum += t.getTotalNum();

            if (t.getTotalTime() != -1) {
                totaltime += t.getTotalTime();
            }
            if (t.getMinLength() != -1 && t.getMinLength() < shortest) {
                shortest = t.getMinLength();
            }
            if (t.getMaxLength() > longest) {
                longest = t.getMaxLength();
            }

            if (t.subtype.equalsIgnoreCase("Search engines")) {
                num1 = new Number(23, i, t.getTotalNum());
            } else if (t.subtype.equalsIgnoreCase("Online encyclopedias")) {
                num1 = new Number(24, i, t.getTotalNum());
            } else if (t.subtype.equalsIgnoreCase("Online Dictionaries")) {
                num1 = new Number(25, i, t.getTotalNum());
            } else if (t.subtype.equalsIgnoreCase("Portals")) {
                num1 = new Number(26, i, t.getTotalNum());
            } else if (t.subtype.equalsIgnoreCase("Other Resources")) {
                num1 = new Number(33, i, t.getTotalNum());
            }
            sheet.addCell(num1);
        }

        for (IncidentList t : p.consults2) {
            t.getStats();

            totalnum += t.getTotalNum();

            if (t.getTotalTime() != -1) {
                totaltime += t.getTotalTime();
            }
            if (t.getMinLength() != -1 && t.getMinLength() < shortest) {
                shortest = t.getMinLength();
            }
            if (t.getMaxLength() > longest) {
                longest = t.getMaxLength();
            }

            if (t.subtype.equalsIgnoreCase("Termbanks")) {
                num1 = new Number(27, i, t.getTotalNum());
            } else if (t.subtype.equalsIgnoreCase("Workflow context")) {
                num1 = new Number(28, i, t.getTotalNum());
            } else if (t.subtype.equalsIgnoreCase("Workflow style guide")) {
                num1 = new Number(29, i, t.getTotalNum());
            } else if (t.subtype.equalsIgnoreCase("Workflow glossary")) {
                num1 = new Number(30, i, t.getTotalNum());
            } else if (t.subtype.equalsIgnoreCase("Workflow parallel text")) {
                num1 = new Number(31, i, t.getTotalNum());
            } else if (t.subtype.equalsIgnoreCase("Concordance")) {
                num1 = new Number(32, i, t.getTotalNum());
            }
            sheet.addCell(num1);

        }


        if (totalnum > 0) {
            double average = (double) totaltime / (double) totalnum;
            num1 = new Number(5, i, totaltime);
            sheet.addCell(num1);
            num1 = new Number(6, i, shortest);
            sheet.addCell(num1);
            num1 = new Number(7, i, longest);
            sheet.addCell(num1);
            num1 = new Number(8, i, average);
            sheet.addCell(num1);
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

        num1 = new Number(22, i, totalnum);
        sheet.addCell(num1);

    }
}
