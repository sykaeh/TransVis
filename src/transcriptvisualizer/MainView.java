/*
 * TranVis.java
 */
package transcriptvisualizer;

import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

class XMLFileFilter extends javax.swing.filechooser.FileFilter {

    @Override
    public boolean accept(File file) {
        // Allow just directories and files with ".xml" extension...
        return file.isDirectory() || file.getAbsolutePath().endsWith(".xml");
    }

    @Override
    public String getDescription() {
        // This description will be displayed in the dialog,
        // hard-coded = ugly, should be done via I18N
        return "XML documents (*.xml)";
    }
}


/**
 * The application's main frame.
 * @author Sybil Ehrensberger
 * @version 0.2
 */
public class MainView extends FrameView {

    /**
     * List of files that contain the transcripts.
     */
    public List<File> fileList;

    /**
     * Public constructor for the main view.
     * @param app
     */
    public MainView(SingleFrameApplication app) {
        super(app);
        fileList = new LinkedList<File>();
        initComponents();
    }

    /**
     * Method invoked when the "Clear all" button is clicked.
     */
    @Action
    public void clearAllFiles() {
        fileList.clear();
        totalFilesField.setText("0");
        multFilesField.setText("");
    }
    
    /**
     * Method invoked when the "Add file" button is clicked.
     */
    @Action
    public void showAddFileChooser() {
        int returnVal = fileChooser.showOpenDialog(this.getFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            fileList.add(f);
            String prevText = multFilesField.getText();
            if (prevText.isEmpty()) {
                multFilesField.setText(f.getName());
            } else {
                multFilesField.setText(prevText.concat(", " + f.getName()));
            }

        } else {
            System.out.println("File access cancelled by user.");
        }
        totalFilesField.setText(String.format("%d", fileList.size()));
    }

    /**
     * Method invoked when the "Add folder" button is clicked.
     */
    @Action
    public void showFolderChooser() {
        int returnVal = folderChooser.showOpenDialog(this.getFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File f = folderChooser.getSelectedFile();
            if (f.isDirectory()) {
                addSubFiles(f);
            } else {
                reportError("Chosen folder is not a folder.");
            }
            
        } else {
            System.out.println("File access cancelled by user.");
        }
        totalFilesField.setText(String.format("%d", fileList.size()));

    }
    
    public File showSaveFileChooser() {
        int returnVal = saveFileChooser.showSaveDialog(this.getFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return saveFileChooser.getSelectedFile();
        } else {
            System.out.println("File access cancelled by user.");
            return null;
        }
        
    }

    /**
     * Recursively goes through the directory f and adds all xml-files 
     * contained in that directory to the fileList.
     * @param f the directory to be searched.
     */
    private void addSubFiles(File f) {

        String[] subfiles = f.list();
        for (String s : subfiles) {
            File newf = new File(f.getAbsolutePath().concat("/" + s));
            if (newf.isDirectory()) {
                addSubFiles(newf);
            } else if (newf.getAbsolutePath().endsWith(".xml")) {
                fileList.add(newf);
                String prevText = multFilesField.getText();
                if (prevText.isEmpty()) {
                    multFilesField.setText(newf.getName());
                } else {
                    multFilesField.setText(prevText.concat(", " + newf.getName()));
                }
            }
        }
    }

    /**
     * Converts the string HH:MM:SS to a an int in seconds.
     * @param time the string containing the time.
     * @return an int representing the time in seconds.
     */
    private int getTime(String time) {
        String[] timesplit = time.split(":");
        if (timesplit.length != 3) {
            reportError("Invalid time format!");
            return 0;
        } else {
            try {
                int hours = Integer.parseInt(timesplit[0]);
                int min = Integer.parseInt(timesplit[1]);
                int sec = Integer.parseInt(timesplit[2]);
                return hours * 360 + min * 60 + sec;
            } catch (NumberFormatException e) {
                reportError("Invalid number.");
                return 0;
            }
        }
    }

    /**
     * Displays a warning dialog with the specified errorText.
     * @param errorText the text to be displayed.
     */
    public void reportError(String errorText) {
        JFrame frame = this.getFrame();
        JOptionPane.showMessageDialog(frame, errorText, "Warning",
                JOptionPane.WARNING_MESSAGE);

    }

    /**
     * Returns the specified start time.
     * @return the start time in seconds.
     */
    public int getStart() {
        String start = startTimeField.getText();
        return getTime(start);
    }

    /**
     * Returns the specified end time.
     * @return the end time in seconds.
     */
    public int getEnd() {
        String end = endTimeField.getText();
        return getTime(end);
    }
    
    public boolean getComplete() {
        return wholeRB.isSelected();
    }
    
    public boolean getOrientationPhase() {
        return orientationRB.isSelected();
    }
    
    public boolean getDraftingPhase() {
        return draftingRB.isSelected();
    }
    
    public boolean getRevisionPhase() {
        return revisionRB.isSelected();
    }

    public boolean getIndividualGraphs() {
        return showIndCB.isSelected();
    }
    
    /**
     * Returns the statistical minimum.
     * @return the statistical minimum in minutes.
     */
    public int getStat() {
        String stat = statDataField.getText();
        int result = 0;
        try {
            result = Integer.parseInt(stat);
        } catch (NumberFormatException e) {
            reportError("Invalid number.");
        }
        return result;
    }
    
    public boolean getCombinedRevisions() {
        return revisioncombinedRB.isSelected();
    }
    
    /**
     * Returns whether the interrupts should be displayed.
     * @return true if the interrupts should be shown.
     */
    public boolean getInterrupts() {
        return interruptsCB.isSelected();
    }

    /**
     * Determines whether the pauses should be displayed.
     * @return true if the pauses should be displayed
     */
    public boolean getPauses() {
        return pausesCB.isSelected();
    }

    /**
     * Determines whether the revisions should be displayed.
     * @return true if the revisions should be displayed
     */
    public boolean getRevisions() {
        return revisionCB.isSelected();
    }

    /**
     * Determines whether the consults should be displayed.
     * @return true if the consults should be displayed
     */
    public boolean getConsults() {
        return consultsCB.isSelected();
    }
    
    /**
     * Determines whether the typos should be displayed.
     * @return true if the typos should be displayed
     */
    public boolean getTypos() {
        return typosCB.isSelected();
    }

    /**
     * Determines whether the individual consults should be displayed.
     * @return true if the individual consults should be displayed
     */
    public boolean getIndConsults() {
        return indconsultsCB.isSelected();
    }

    /**
     * Determines whether the individual revisions should be displayed.
     * @return true if the individual revision should be displayed
     */
    public boolean getIndRevisions() {
        return indrevisionsCB.isSelected();
    }

    /**
     * Determines whether the individual interrupts should be displayed.
     * @return true if the individual interrupts should be displayed
     */
    public boolean getIndInterrupts() {
        return indinterruptsCB.isSelected();
    }
    
    /**
     * Determines whether the individual pauses should be displayed.
     * @return true if the individual pauses should be displayed
     */
    public boolean getIndPauses() {
        return indpausesCB.isSelected();
    }
    
    /**
     * Determines whether the writing actions should be displayed.
     * @return true if the writing actions interrupts should be displayed
     */
    public boolean getWriting() {
        return writingCB.isSelected();
    }
    
    /**
     * Determines whether the ST actions should be displayed.
     * @return true if the ST actions should be displayed
     */
    public boolean getSTActions() {
        return stCB.isSelected();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        optionsPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        pausesCB = new javax.swing.JCheckBox();
        consultsCB = new javax.swing.JCheckBox();
        typosCB = new javax.swing.JCheckBox();
        revisionCB = new javax.swing.JCheckBox();
        interruptsCB = new javax.swing.JCheckBox();
        writingCB = new javax.swing.JCheckBox();
        stCB = new javax.swing.JCheckBox();
        indpausesCB = new javax.swing.JCheckBox();
        indconsultsCB = new javax.swing.JCheckBox();
        indrevisionsCB = new javax.swing.JCheckBox();
        indinterruptsCB = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        revisionsingleRB = new javax.swing.JRadioButton();
        revisioncombinedRB = new javax.swing.JRadioButton();
        timePanel = new javax.swing.JPanel();
        endTimeField = new javax.swing.JTextField();
        startTimeField = new javax.swing.JTextField();
        statDataField = new javax.swing.JTextField();
        label1 = new java.awt.Label();
        wholeRB = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        selectedRB = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        orientationRB = new javax.swing.JRadioButton();
        draftingRB = new javax.swing.JRadioButton();
        revisionRB = new javax.swing.JRadioButton();
        filePanel = new javax.swing.JPanel();
        addFolderButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        multFilesField = new javax.swing.JTextArea();
        jLabel7 = new javax.swing.JLabel();
        addFileButton = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        totalFilesField = new javax.swing.JLabel();
        clearButton = new javax.swing.JButton();
        buttonPanel = new javax.swing.JPanel();
        mainGraphButton = new javax.swing.JButton();
        consultsGraphButton = new javax.swing.JButton();
        revisionsGraphButton = new javax.swing.JButton();
        pausesGraphButton = new javax.swing.JButton();
        customGraphButton = new javax.swing.JButton();
        statsButton = new javax.swing.JButton();
        dataButton = new javax.swing.JButton();
        showIndCB = new javax.swing.JCheckBox();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        fileChooser = new javax.swing.JFileChooser();
        timeBG = new javax.swing.ButtonGroup();
        folderChooser = new javax.swing.JFileChooser();
        revisionBG = new javax.swing.ButtonGroup();
        saveFileChooser = new javax.swing.JFileChooser();

        mainPanel.setMaximumSize(new java.awt.Dimension(1000, 1000));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(950, 700));
        mainPanel.setSize(new java.awt.Dimension(950, 700));

        optionsPanel.setName("optionsPanel"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(transcriptvisualizer.MainApp.class).getContext().getResourceMap(MainView.class);
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        pausesCB.setSelected(true);
        pausesCB.setText(resourceMap.getString("pausesCB.text")); // NOI18N
        pausesCB.setName("pausesCB"); // NOI18N

        consultsCB.setSelected(true);
        consultsCB.setText(resourceMap.getString("consultsCB.text")); // NOI18N
        consultsCB.setName("consultsCB"); // NOI18N

        typosCB.setSelected(true);
        typosCB.setText(resourceMap.getString("typosCB.text")); // NOI18N
        typosCB.setName("typosCB"); // NOI18N

        revisionCB.setSelected(true);
        revisionCB.setText(resourceMap.getString("revisionCB.text")); // NOI18N
        revisionCB.setName("revisionCB"); // NOI18N

        interruptsCB.setSelected(true);
        interruptsCB.setText(resourceMap.getString("interruptsCB.text")); // NOI18N
        interruptsCB.setName("interruptsCB"); // NOI18N

        writingCB.setSelected(true);
        writingCB.setText(resourceMap.getString("writingCB.text")); // NOI18N
        writingCB.setName("writingCB"); // NOI18N

        stCB.setSelected(true);
        stCB.setText(resourceMap.getString("stCB.text")); // NOI18N
        stCB.setName("stCB"); // NOI18N

        indpausesCB.setText(resourceMap.getString("indpausesCB.text")); // NOI18N
        indpausesCB.setName("indpausesCB"); // NOI18N

        indconsultsCB.setText(resourceMap.getString("indconsultsCB.text")); // NOI18N
        indconsultsCB.setName("indconsultsCB"); // NOI18N

        indrevisionsCB.setText(resourceMap.getString("indrevisionsCB.text")); // NOI18N
        indrevisionsCB.setName("indrevisionsCB"); // NOI18N

        indinterruptsCB.setText(resourceMap.getString("indinterruptsCB.text")); // NOI18N
        indinterruptsCB.setName("indinterruptsCB"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        revisionBG.add(revisionsingleRB);
        revisionsingleRB.setSelected(true);
        revisionsingleRB.setText(resourceMap.getString("revisionsingleRB.text")); // NOI18N
        revisionsingleRB.setName("revisionsingleRB"); // NOI18N

        revisionBG.add(revisioncombinedRB);
        revisioncombinedRB.setText(resourceMap.getString("revisioncombinedRB.text")); // NOI18N
        revisioncombinedRB.setName("revisioncombinedRB"); // NOI18N

        org.jdesktop.layout.GroupLayout optionsPanelLayout = new org.jdesktop.layout.GroupLayout(optionsPanel);
        optionsPanel.setLayout(optionsPanelLayout);
        optionsPanelLayout.setHorizontalGroup(
            optionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(optionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(optionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(optionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, optionsPanelLayout.createSequentialGroup()
                            .add(optionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(pausesCB)
                                .add(stCB)
                                .add(writingCB)
                                .add(interruptsCB)
                                .add(revisionCB)
                                .add(consultsCB)
                                .add(typosCB)
                                .add(revisionsingleRB))
                            .add(42, 42, 42)
                            .add(optionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(revisioncombinedRB)
                                .add(indconsultsCB)
                                .add(indpausesCB)
                                .add(indrevisionsCB)
                                .add(indinterruptsCB)))
                        .add(optionsPanelLayout.createSequentialGroup()
                            .add(jLabel2)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 148, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(53, Short.MAX_VALUE))
        );
        optionsPanelLayout.setVerticalGroup(
            optionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(optionsPanelLayout.createSequentialGroup()
                .add(8, 8, 8)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(optionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(revisionsingleRB)
                    .add(revisioncombinedRB))
                .add(19, 19, 19)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(optionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(optionsPanelLayout.createSequentialGroup()
                        .add(pausesCB)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(consultsCB)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(typosCB)
                        .add(3, 3, 3)
                        .add(revisionCB)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(interruptsCB)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(writingCB)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(stCB))
                    .add(optionsPanelLayout.createSequentialGroup()
                        .add(indpausesCB)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(indconsultsCB)
                        .add(26, 26, 26)
                        .add(indrevisionsCB)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(indinterruptsCB)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        timePanel.setName("timePanel"); // NOI18N

        endTimeField.setText(resourceMap.getString("endTimeField.text")); // NOI18N
        endTimeField.setName("endTimeField"); // NOI18N

        startTimeField.setText(resourceMap.getString("startTimeField.text")); // NOI18N
        startTimeField.setName("startTimeField"); // NOI18N

        statDataField.setText(resourceMap.getString("statDataField.text")); // NOI18N
        statDataField.setAutoscrolls(false);
        statDataField.setDragEnabled(false);
        statDataField.setMaximumSize(new java.awt.Dimension(30, 28));
        statDataField.setName("statDataField"); // NOI18N

        label1.setName("label1"); // NOI18N
        label1.setText(resourceMap.getString("label1.text")); // NOI18N

        timeBG.add(wholeRB);
        wholeRB.setSelected(true);
        wholeRB.setText(resourceMap.getString("wholeRB.text")); // NOI18N
        wholeRB.setName("wholeRB"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        timeBG.add(selectedRB);
        selectedRB.setText(resourceMap.getString("selectedRB.text")); // NOI18N
        selectedRB.setName("selectedRB"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        timeBG.add(orientationRB);
        orientationRB.setText(resourceMap.getString("orientationRB.text")); // NOI18N
        orientationRB.setName("orientationRB"); // NOI18N

        timeBG.add(draftingRB);
        draftingRB.setText(resourceMap.getString("draftingRB.text")); // NOI18N
        draftingRB.setName("draftingRB"); // NOI18N

        timeBG.add(revisionRB);
        revisionRB.setText(resourceMap.getString("revisionRB.text")); // NOI18N
        revisionRB.setName("revisionRB"); // NOI18N

        org.jdesktop.layout.GroupLayout timePanelLayout = new org.jdesktop.layout.GroupLayout(timePanel);
        timePanel.setLayout(timePanelLayout);
        timePanelLayout.setHorizontalGroup(
            timePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(timePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(timePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(label1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(selectedRB)
                    .add(timePanelLayout.createSequentialGroup()
                        .add(42, 42, 42)
                        .add(timePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel3)
                            .add(jLabel4))
                        .add(34, 34, 34)
                        .add(timePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(startTimeField)
                            .add(endTimeField)))
                    .add(wholeRB)
                    .add(orientationRB)
                    .add(timePanelLayout.createSequentialGroup()
                        .add(jLabel5)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(statDataField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel6))
                    .add(draftingRB)
                    .add(revisionRB))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        timePanelLayout.setVerticalGroup(
            timePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(timePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(label1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(3, 3, 3)
                .add(wholeRB)
                .add(5, 5, 5)
                .add(selectedRB)
                .add(9, 9, 9)
                .add(timePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(startTimeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(8, 8, 8)
                .add(timePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(endTimeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(orientationRB)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(draftingRB)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(revisionRB)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 12, Short.MAX_VALUE)
                .add(timePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(statDataField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel6))
                .add(50, 50, 50))
        );

        filePanel.setName("filePanel"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(transcriptvisualizer.MainApp.class).getContext().getActionMap(MainView.class, this);
        addFolderButton.setAction(actionMap.get("showFolderChooser")); // NOI18N
        addFolderButton.setText(resourceMap.getString("addFolderButton.text")); // NOI18N
        addFolderButton.setName("addFolderButton"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        multFilesField.setColumns(20);
        multFilesField.setEditable(false);
        multFilesField.setLineWrap(true);
        multFilesField.setRows(5);
        multFilesField.setName("multFilesField"); // NOI18N
        jScrollPane1.setViewportView(multFilesField);

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        addFileButton.setAction(actionMap.get("showAddFileChooser")); // NOI18N
        addFileButton.setText(resourceMap.getString("addFileButton.text")); // NOI18N
        addFileButton.setName("addFileButton"); // NOI18N

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        totalFilesField.setText(resourceMap.getString("totalFilesField.text")); // NOI18N
        totalFilesField.setName("totalFilesField"); // NOI18N

        clearButton.setAction(actionMap.get("clearAllFiles")); // NOI18N
        clearButton.setText(resourceMap.getString("clearButton.text")); // NOI18N
        clearButton.setName("clearButton"); // NOI18N

        org.jdesktop.layout.GroupLayout filePanelLayout = new org.jdesktop.layout.GroupLayout(filePanel);
        filePanel.setLayout(filePanelLayout);
        filePanelLayout.setHorizontalGroup(
            filePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(filePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(filePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(filePanelLayout.createSequentialGroup()
                        .add(addFolderButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(addFileButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(clearButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 321, Short.MAX_VALUE)
                        .add(jLabel8)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(totalFilesField))
                    .add(filePanelLayout.createSequentialGroup()
                        .add(8, 8, 8)
                        .add(jLabel7)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 707, Short.MAX_VALUE)))
                .addContainerGap())
        );
        filePanelLayout.setVerticalGroup(
            filePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, filePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(filePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(addFolderButton)
                    .add(addFileButton)
                    .add(totalFilesField)
                    .add(jLabel8)
                    .add(clearButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(filePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel7)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 104, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(113, 113, 113))
        );

        buttonPanel.setName("buttonPanel"); // NOI18N

        mainGraphButton.setAction(actionMap.get("displayMainGraphClicked")); // NOI18N
        mainGraphButton.setText(resourceMap.getString("mainGraphButton.text")); // NOI18N
        mainGraphButton.setName("mainGraphButton"); // NOI18N

        consultsGraphButton.setAction(actionMap.get("displayConsultsGraphClicked")); // NOI18N
        consultsGraphButton.setText(resourceMap.getString("consultsGraphButton.text")); // NOI18N
        consultsGraphButton.setName("consultsGraphButton"); // NOI18N

        revisionsGraphButton.setAction(actionMap.get("displayRevisionsGraphClicked")); // NOI18N
        revisionsGraphButton.setText(resourceMap.getString("revisionsGraphButton.text")); // NOI18N
        revisionsGraphButton.setName("revisionsGraphButton"); // NOI18N

        pausesGraphButton.setAction(actionMap.get("displayPausesGraphClicked")); // NOI18N
        pausesGraphButton.setText(resourceMap.getString("pausesGraphButton.text")); // NOI18N
        pausesGraphButton.setName("pausesGraphButton"); // NOI18N

        customGraphButton.setAction(actionMap.get("displayCustomGraphClicked")); // NOI18N
        customGraphButton.setText(resourceMap.getString("customGraphButton.text")); // NOI18N
        customGraphButton.setName("customGraphButton"); // NOI18N

        statsButton.setAction(actionMap.get("exportStatisticsClicked")); // NOI18N
        statsButton.setText(resourceMap.getString("statsButton.text")); // NOI18N
        statsButton.setName("statsButton"); // NOI18N

        dataButton.setAction(actionMap.get("exportDataClicked")); // NOI18N
        dataButton.setText(resourceMap.getString("dataButton.text")); // NOI18N
        dataButton.setName("dataButton"); // NOI18N

        showIndCB.setText(resourceMap.getString("showIndCB.text")); // NOI18N
        showIndCB.setName("showIndCB"); // NOI18N

        org.jdesktop.layout.GroupLayout buttonPanelLayout = new org.jdesktop.layout.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(buttonPanelLayout.createSequentialGroup()
                .add(11, 11, 11)
                .add(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(buttonPanelLayout.createSequentialGroup()
                        .add(mainGraphButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(consultsGraphButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(revisionsGraphButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(pausesGraphButton))
                    .add(buttonPanelLayout.createSequentialGroup()
                        .add(customGraphButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(showIndCB)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(statsButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(dataButton)))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(mainGraphButton)
                    .add(consultsGraphButton)
                    .add(revisionsGraphButton)
                    .add(pausesGraphButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(customGraphButton)
                    .add(dataButton)
                    .add(statsButton)
                    .add(showIndCB))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mainPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(optionsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(timePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 337, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(filePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(mainPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(buttonPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(57, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .add(filePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 182, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(buttonPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(optionsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(timePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(94, 94, 94))
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setDialogTitle(resourceMap.getString("fileChooser.dialogTitle")); // NOI18N
        fileChooser.setFileFilter(new XMLFileFilter());
        fileChooser.setName("fileChooser"); // NOI18N

        folderChooser.setDialogTitle(resourceMap.getString("folderChooser.dialogTitle")); // NOI18N
        folderChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        folderChooser.setName("folderChooser"); // NOI18N

        saveFileChooser.setCurrentDirectory(new java.io.File("/HOME"));
        saveFileChooser.setDialogTitle(resourceMap.getString("saveFileChooser.dialogTitle")); // NOI18N
        saveFileChooser.setSelectedFile(new java.io.File("/statistics.xls"));
        saveFileChooser.setName("saveFileChooser"); // NOI18N

        setComponent(mainPanel);
        setMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addFileButton;
    private javax.swing.JButton addFolderButton;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton clearButton;
    private javax.swing.JCheckBox consultsCB;
    private javax.swing.JButton consultsGraphButton;
    private javax.swing.JButton customGraphButton;
    private javax.swing.JButton dataButton;
    private javax.swing.JRadioButton draftingRB;
    private javax.swing.JTextField endTimeField;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JPanel filePanel;
    private javax.swing.JFileChooser folderChooser;
    private javax.swing.JCheckBox indconsultsCB;
    private javax.swing.JCheckBox indinterruptsCB;
    private javax.swing.JCheckBox indpausesCB;
    private javax.swing.JCheckBox indrevisionsCB;
    private javax.swing.JCheckBox interruptsCB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private java.awt.Label label1;
    private javax.swing.JButton mainGraphButton;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JTextArea multFilesField;
    private javax.swing.JPanel optionsPanel;
    private javax.swing.JRadioButton orientationRB;
    private javax.swing.JCheckBox pausesCB;
    private javax.swing.JButton pausesGraphButton;
    private javax.swing.ButtonGroup revisionBG;
    private javax.swing.JCheckBox revisionCB;
    private javax.swing.JRadioButton revisionRB;
    private javax.swing.JRadioButton revisioncombinedRB;
    private javax.swing.JButton revisionsGraphButton;
    private javax.swing.JRadioButton revisionsingleRB;
    private javax.swing.JFileChooser saveFileChooser;
    private javax.swing.JRadioButton selectedRB;
    private javax.swing.JCheckBox showIndCB;
    private javax.swing.JCheckBox stCB;
    private javax.swing.JTextField startTimeField;
    private javax.swing.JTextField statDataField;
    private javax.swing.JButton statsButton;
    private javax.swing.ButtonGroup timeBG;
    private javax.swing.JPanel timePanel;
    private javax.swing.JLabel totalFilesField;
    private javax.swing.JCheckBox typosCB;
    private javax.swing.JRadioButton wholeRB;
    private javax.swing.JCheckBox writingCB;
    // End of variables declaration//GEN-END:variables

}
