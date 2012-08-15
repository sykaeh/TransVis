package transcriptvisualizer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.read.biff.BiffException;
import jxl.*;
import jxl.write.*;
import jxl.write.Number;

/**
 *
 * @author Sybil Ehrensberger
 */
public class ExcelDocument {

    private List<XMLParser> parserList;
    private File ofile;
    private final static Logger LOGGER = Logger.getLogger("TranVis");

    ExcelDocument(List<XMLParser> parsers, File outputfile) {

        parserList = parsers;
        ofile = outputfile;
    }

    public void makeExcelFile() {

        try {
            // Create an appending file handler
            FileHandler handler = new FileHandler("tranvis.log");
            LOGGER.addHandler(handler);
        } catch (IOException e) {
        }

        try {
            LOGGER.log(Level.INFO, "Getting template.xls");
            Workbook originalworkbook = Workbook.getWorkbook(new File("template.xls"));
            LOGGER.log(Level.INFO, "Creating new workbook");
            WritableWorkbook workbook = Workbook.createWorkbook(ofile, originalworkbook);
            WritableSheet sheet = workbook.getSheet(0);

            fillSheet(sheet);


            LOGGER.log(Level.INFO, "Writing");
            workbook.write();
            workbook.close();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error with Excel...", ex);

        }
    }

    private void fillSheet(WritableSheet sheet) throws WriteException {

        Label label;
        Number num;
        Formula f;

        int i = 4;
        int j = i + 1;
        for (XMLParser p : parserList) {
            j = i + 1;
            label = new Label(0, i, p.name);
            sheet.addCell(label);
            label = new Label(0, j, p.name);
            sheet.addCell(label);
            num = new Number(1, i, p.startAdjustment - p.startProcess);
            sheet.addCell(num);
            num = new Number(1, j, p.startAdjustment - p.startProcess);
            sheet.addCell(num);
            num = new Number(2, i, p.endProcess);
            sheet.addCell(num);
            num = new Number(2, j, p.statistics);
            sheet.addCell(num);
            num = new Number(3, i, p.lengthProcess);
            sheet.addCell(num);
            num = new Number(3, j, p.statistics);
            sheet.addCell(num);

            label = new Label(4, j, "partial");
            sheet.addCell(label);
            label = new Label(4, i, "full");
            sheet.addCell(label);

            addPauses(sheet, p, i, j);
            addConsults(sheet, p, i, j);

            p.typos.getStats();
            num = new Number(34, i, p.typos.getTotalNum());
            sheet.addCell(num);
            num = new Number(34, j, p.typos.getFirstNum());
            sheet.addCell(num);

            addRevisions(sheet, p, i, j);

            p.writes.getStats();
            p.accepts.getStats();
            num = new Number(51, i, p.writes.getTotalNum() + p.accepts.getTotalNum());
            sheet.addCell(num);
            num = new Number(51, j, p.accepts.getFirstNum() + p.writes.getFirstNum());
            sheet.addCell(num);
            
            num = new Number(52, i, p.writes.getTotalNum());
            sheet.addCell(num);
            num = new Number(52, j, p.writes.getFirstNum());
            sheet.addCell(num);
            
            num = new Number(53, i, p.accepts.getTotalNum());
            sheet.addCell(num);
            num = new Number(53, j, p.accepts.getFirstNum());
            sheet.addCell(num);

            p.sourcetext.getStats();
            num = new Number(54, i, p.sourcetext.getTotalNum());
            sheet.addCell(num);
            num = new Number(54, j, p.sourcetext.getFirstNum());
            sheet.addCell(num);

            addInterrupts(sheet, p, i, j);

            num = new Number(9, i, p.writes.getFirstTime());
            sheet.addCell(num);
            num = new Number(10, i, p.writes.getTotalTime());
            sheet.addCell(num);
            num = new Number(11, i, p.writes.getMinLength());
            sheet.addCell(num);
            num = new Number(12, i, p.writes.getMaxLength());
            sheet.addCell(num);
            num = new Number(13, i, p.writes.getAvgLength());
            sheet.addCell(num);

            i++;
            i++;
        }

    }

    /**
     * 
     * @param sheet
     * @param p
     * @param i
     * @param j 
     */
    private void addRevisions(WritableSheet sheet, XMLParser p, int i, int j) throws WriteException {
        Number num1 = new Number(0, 0, 0);
        Number num2 = new Number(0, 0, 0);

        int numrev = 0;
        int numrevfirst = 0;
        int numrev2 = 0;
        int numrev2first = 0;

        for (Tag t : p.revisions) {
            t.getStats();
            if (t.subtype.equalsIgnoreCase("revision")) {
                numrev += t.getTotalNum();
                numrevfirst += t.getFirstNum();
            } else if (t.subtype.equalsIgnoreCase("revision2")) {
                numrev2 += t.getTotalNum();
                numrev2first += t.getFirstNum();
            }

            if (t.type.equalsIgnoreCase("deletes") && t.subtype.equalsIgnoreCase("revision")) {
                num1 = new Number(37, i, t.getTotalNum());
                num2 = new Number(37, j, t.getFirstNum());
            } else if (t.type.equalsIgnoreCase("deletes") && t.subtype.equalsIgnoreCase("revision2")) {
                num1 = new Number(38, i, t.getTotalNum());
                num2 = new Number(38, j, t.getFirstNum());
            } else if (t.type.equalsIgnoreCase("inserts") && t.subtype.equalsIgnoreCase("revision")) {
                num1 = new Number(39, i, t.getTotalNum());
                num2 = new Number(39, j, t.getFirstNum());
            } else if (t.type.equalsIgnoreCase("inserts") && t.subtype.equalsIgnoreCase("revision2")) {
                num1 = new Number(40, i, t.getTotalNum());
                num2 = new Number(40, j, t.getFirstNum());
            } else if (t.type.equalsIgnoreCase("cuts") && t.subtype.equalsIgnoreCase("revision")) {
                num1 = new Number(41, i, t.getTotalNum());
                num2 = new Number(41, j, t.getFirstNum());
            } else if (t.type.equalsIgnoreCase("cuts") && t.subtype.equalsIgnoreCase("revision2")) {
                num1 = new Number(42, i, t.getTotalNum());
                num2 = new Number(42, j, t.getFirstNum());
            } else if (t.type.equalsIgnoreCase("pastes") && t.subtype.equalsIgnoreCase("revision")) {
                num1 = new Number(43, i, t.getTotalNum());
                num2 = new Number(43, j, t.getFirstNum());
            } else if (t.type.equalsIgnoreCase("pastes") && t.subtype.equalsIgnoreCase("revision2")) {
                num1 = new Number(44, i, t.getTotalNum());
                num2 = new Number(44, j, t.getFirstNum());
            } else if (t.type.equalsIgnoreCase("moves from") && t.subtype.equalsIgnoreCase("revision")) {
                num1 = new Number(45, i, t.getTotalNum());
                num2 = new Number(45, j, t.getFirstNum());
            } else if (t.type.equalsIgnoreCase("moves from") && t.subtype.equalsIgnoreCase("revision2")) {
                num1 = new Number(46, i, t.getTotalNum());
                num2 = new Number(46, j, t.getFirstNum());
            } else if (t.type.equalsIgnoreCase("moves to") && t.subtype.equalsIgnoreCase("revision")) {
                num1 = new Number(47, i, t.getTotalNum());
                num2 = new Number(47, j, t.getFirstNum());
            } else if (t.type.equalsIgnoreCase("moves to") && t.subtype.equalsIgnoreCase("revision2")) {
                num1 = new Number(48, i, t.getTotalNum());
                num2 = new Number(48, j, t.getFirstNum());
            } else if (t.type.equalsIgnoreCase("undoes") && t.subtype.equalsIgnoreCase("revision")) {
                num1 = new Number(49, i, t.getTotalNum());
                num2 = new Number(49, j, t.getFirstNum());
            } else if (t.type.equalsIgnoreCase("undoes") && t.subtype.equalsIgnoreCase("revision2")) {
                num1 = new Number(50, i, t.getTotalNum());
                num2 = new Number(50, j, t.getFirstNum());
            }
            sheet.addCell(num1);
            sheet.addCell(num2);
        }

        num1 = new Number(35, i, numrev2 + numrev);
        num2 = new Number(35, j, numrev2first + numrevfirst);
        sheet.addCell(num1);
        sheet.addCell(num2);
        num1 = new Number(36, i, numrev);
        num2 = new Number(36, j, numrevfirst);
        sheet.addCell(num1);
        sheet.addCell(num2);;
    }

    /**
     * 
     * @param sheet
     * @param p
     * @param i
     * @param j
     * @throws WriteException 
     */
    private void addInterrupts(WritableSheet sheet, XMLParser p, int i, int j) throws WriteException {
        Number num1 = new Number(0, 0, 0);
        Number num2 = new Number(0, 0, 0);

        int total = 0;
        int firstTotal = 0;

        for (Tag t : p.interrupts) {
            t.getStats();

            if (t.subtype.equalsIgnoreCase("privatemail")) {
                num1 = new Number(56, i, t.getTotalNum());
                num2 = new Number(56, j, t.getFirstNum());
            } else if (t.subtype.equalsIgnoreCase("jobmail")) {
                num1 = new Number(57, i, t.getTotalNum());
                num2 = new Number(57, j, t.getFirstNum());
            } else if (t.subtype.equalsIgnoreCase("internet")) {
                num1 = new Number(58, i, t.getTotalNum());
                num2 = new Number(58, j, t.getFirstNum());
            } else if (t.subtype.equalsIgnoreCase("task")) {
                num1 = new Number(59, i, t.getTotalNum());
                num2 = new Number(59, j, t.getFirstNum());
            } else if (t.subtype.equalsIgnoreCase("workflow")) {
                num1 = new Number(60, i, t.getTotalNum());
                num2 = new Number(60, j, t.getFirstNum());
            } else if (t.subtype.equalsIgnoreCase("break")) {
                num1 = new Number(61, i, t.getTotalNum());
                num2 = new Number(61, j, t.getFirstNum());
            }
            total = total + t.getTotalNum();
            firstTotal = firstTotal + t.getFirstNum();

            sheet.addCell(num1);
            sheet.addCell(num2);
        }

        num1 = new Number(55, i, total);
        sheet.addCell(num1);
        num2 = new Number(55, j, firstTotal);
        sheet.addCell(num2);
    }

    /**
     * 
     * @param sheet
     * @param p
     * @param i
     * @param j
     * @throws WriteException 
     */
    private void addPauses(WritableSheet sheet, XMLParser p, int i, int j) throws WriteException {
        Number num1 = new Number(0, 0, 0);
        Number num2 = new Number(0, 0, 0);

        int total = 0;
        int firstTotal = 0;

        for (Tag t : p.pauses) {
            t.getStats();
            if (t.type.equalsIgnoreCase("pause")) {
                num1 = new Number(15, i, t.getTotalNum());
                num2 = new Number(15, j, t.getFirstNum());
            } else if (t.subtype.equalsIgnoreCase("consults")) {
                num1 = new Number(16, i, t.getTotalNum());
                num2 = new Number(16, j, t.getFirstNum());
            } else if (t.subtype.equalsIgnoreCase("readsTask")) {
                num1 = new Number(17, i, t.getTotalNum());
                num2 = new Number(17, j, t.getFirstNum());
            } else if (t.subtype.equalsIgnoreCase("readsST")) {
                num1 = new Number(18, i, t.getTotalNum());
                num2 = new Number(18, j, t.getFirstNum());
            } else if (t.subtype.equalsIgnoreCase("readsTT")) {
                num1 = new Number(19, i, t.getTotalNum());
                num2 = new Number(19, j, t.getFirstNum());
            } else if (t.subtype.equalsIgnoreCase("readsST+TT")) {
                num1 = new Number(20, i, t.getTotalNum());
                num2 = new Number(20, j, t.getFirstNum());
            } else if (t.subtype.equalsIgnoreCase("unclear")) {
                num1 = new Number(21, i, t.getTotalNum());
                num2 = new Number(21, j, t.getFirstNum());
            }
            total = total + t.getTotalNum();
            firstTotal = firstTotal + t.getFirstNum();
            sheet.addCell(num1);
            sheet.addCell(num2);
        }

        num1 = new Number(14, i, total);
        sheet.addCell(num1);
        num2 = new Number(14, j, firstTotal);
        sheet.addCell(num2);
    }

    /**
     * 
     * @param sheet
     * @param p
     * @param i
     * @param j
     * @throws WriteException 
     */
    private void addConsults(WritableSheet sheet, XMLParser p, int i, int j) throws WriteException {
        Number num1 = new Number(0, 0, 0);
        Number num2 = new Number(0, 0, 0);

        int totaltime = 0;
        int totalnum = 0;
        int shortest = p.lengthProcess;
        int longest = 0;
        int totalfirstnum = 0;

        for (Tag t : p.consults) {
            t.getStats();

            totalnum += t.getTotalNum();
            totalfirstnum += t.getFirstNum();

            totaltime += t.getTotalTime();
            if (t.getMinLength() < shortest) {
                shortest = t.getMinLength();
            }
            if (t.getMaxLength() > longest) {
                longest = t.getMaxLength();
            }

            if (t.subtype.equalsIgnoreCase("Search engines")) {
                num1 = new Number(23, i, t.getTotalNum());
                num2 = new Number(23, j, t.getFirstNum());
            } else if (t.subtype.equalsIgnoreCase("Online encyclopedias")) {
                num1 = new Number(24, i, t.getTotalNum());
                num2 = new Number(24, j, t.getFirstNum());
            } else if (t.subtype.equalsIgnoreCase("Online Dictionaries")) {
                num1 = new Number(25, i, t.getTotalNum());
                num2 = new Number(25, j, t.getFirstNum());
            } else if (t.subtype.equalsIgnoreCase("Portals")) {
                num1 = new Number(26, i, t.getTotalNum());
                num2 = new Number(26, j, t.getFirstNum());
            } else if (t.subtype.equalsIgnoreCase("Termbanks")) {
                num1 = new Number(27, i, t.getTotalNum());
                num2 = new Number(27, j, t.getFirstNum());
            } else if (t.subtype.equalsIgnoreCase("Workflow context")) {
                num1 = new Number(28, i, t.getTotalNum());
                num2 = new Number(28, j, t.getFirstNum());
            } else if (t.subtype.equalsIgnoreCase("Workflow style guide")) {
                num1 = new Number(29, i, t.getTotalNum());
                num2 = new Number(29, j, t.getFirstNum());
            } else if (t.subtype.equalsIgnoreCase("Workflow glossary")) {
                num1 = new Number(30, i, t.getTotalNum());
                num2 = new Number(30, j, t.getFirstNum());
            } else if (t.subtype.equalsIgnoreCase("Workflow parallel text")) {
                num1 = new Number(31, i, t.getTotalNum());
                num2 = new Number(31, j, t.getFirstNum());
            } else if (t.subtype.equalsIgnoreCase("Concordance")) {
                num1 = new Number(32, i, t.getTotalNum());
                num2 = new Number(32, j, t.getFirstNum());
            } else if (t.subtype.equalsIgnoreCase("Other Resources")) {
                num1 = new Number(33, i, t.getTotalNum());
                num2 = new Number(33, j, t.getFirstNum());
            }
            sheet.addCell(num1);
            sheet.addCell(num2);
        }

        double average = totaltime / totalnum;

        num1 = new Number(5, i, totaltime);
        sheet.addCell(num1);
        num1 = new Number(6, i, shortest);
        sheet.addCell(num1);
        num1 = new Number(7, i, longest);
        sheet.addCell(num1);
        num1 = new Number(8, i, average);
        sheet.addCell(num1);

        num1 = new Number(22, i, totalnum);
        sheet.addCell(num1);
        num2 = new Number(22, j, totalfirstnum);
        sheet.addCell(num2);
    }
}
