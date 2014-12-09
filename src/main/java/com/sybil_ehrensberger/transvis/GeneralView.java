package com.sybil_ehrensberger.transvis;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Sybil Ehrensberger
 */
public class GeneralView {
    public JPanel main_panel;
    public JTextPane messages;
    private JButton chooseFolderButton;
    private JButton chooseFilesButton;
    private JButton clearAllButton;
    private JTabbedPane tabbedPane1;
    private JButton createExcelFilesButton;
    private JRadioButton completeProcessRadioButton;
    private JRadioButton orientationPhaseRadioButton;
    private JRadioButton draftingPhaseRadioButton;
    private JRadioButton revisionPhaseRadioButton;
    private JRadioButton selectedSegmentRadioButton;
    private JCheckBox mainGraphCheckBox;
    private JCheckBox consultsGraphCheckBox;
    private JCheckBox revisionsGraphCheckBox;
    private JCheckBox pausesGraphCheckBox;
    private JCheckBox showIndividualSubgraphsCheckBox;
    private JRadioButton onlyRevisionRadioButton1;
    private JButton displayGraphsButton;
    private JCheckBox pausesCheckBox;
    private JCheckBox consultsCheckBox;
    private JCheckBox typosCheckBox;
    private JCheckBox TTActionsCheckBox;
    private JCheckBox revisionsCheckBox;
    private JCheckBox interruptionsCheckBox;
    private JCheckBox STActionsCheckBox;
    private JRadioButton onlyRevisionRadioButton;
    private JButton displayGraphButton;
    private JCheckBox pausesSubgroupsCheckBox;
    private JCheckBox consultsSubgroupsCheckBox;
    private JCheckBox revisionsSubgroupsCheckBox;
    private JCheckBox interruptionsSubgroupsCheckBox;
    private JTextField textField3;
    private JTextField dataXlsTextField;
    private JTextField statsXlsTextField;
    private JLabel num_files;
    private JList file_list;
    private JTextField startTimeField;
    private JTextField endTimeField;
    private JRadioButton combinedRevisionStandard;
    private JRadioButton combinedRevisionCustom;
    private JCheckBox matchesCheckBox;
    private JTextField excelDirectoryField;
    private JButton chooseButton;
    private JCheckBox createDataCheckBox;
    private JCheckBox createStatsCheckBox;
    private JRadioButton actualTimeInProcessRadioButton;
    private JRadioButton timeInSelectedPhaseRadioButton;

    private List<Transcript> transcripts;
    private File excelDirectory;

    public GeneralView() {

        chooseFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File[] files = new FileHandler().showFolderChooser(main_panel);
                populateFileList(files);
            }
        });

        chooseFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File[] files = new FileHandler().showAddFileChooser(main_panel);
                populateFileList(files);
            }
        });

        clearAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transcripts.clear();
                messages.setText("");
                populateFileList(new File[0]);
            }
        });

        displayGraphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                setSelectionForAll();
                Graph graph = new Graph(transcripts, adjust());
                graph.generateGraphsClicked(Arrays.asList(GraphType.CUSTOM), showIndividualSubgraphsCheckBox.isSelected());


            }
        });

        displayGraphsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Graph graph;

                try {
                    setSelectionForAll();
                    graph = new Graph(transcripts, adjust());
                    List<GraphType> types = new ArrayList<>();

                    if (mainGraphCheckBox.isSelected())
                        types.add(GraphType.MAIN);
                    if (consultsGraphCheckBox.isSelected())
                        types.add(GraphType.CONSULTS);
                    if (revisionsGraphCheckBox.isSelected())
                        types.add(GraphType.REVISIONS);
                    if (pausesCheckBox.isSelected())
                        types.add(GraphType.PAUSES);

                    graph.generateGraphsClicked(types, showIndividualSubgraphsCheckBox.isSelected());

                } catch (Exception ex) {
                    Main.fatalError("Unexpected error: " + ex.getMessage());
                    ex.printStackTrace();
                }

            }
        });

        chooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                excelDirectory = new FileHandler().showSaveFileChooser(main_panel, excelDirectory);
                excelDirectoryField.setText(excelDirectory.getAbsolutePath());
            }
        });

        createExcelFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // TODO: check for proper directory and file names

                setSelectionForAll();
                String directory_path = excelDirectory.getAbsolutePath();

                File dataFile = new File(directory_path + "/" + dataXlsTextField.getText());
                File statsFile = new File(directory_path + "/" + statsXlsTextField.getText());


                boolean generate_data = createDataCheckBox.isSelected();
                boolean generate_stats = createStatsCheckBox.isSelected();

                System.out.println(generate_data + " Data file: " + dataFile.getAbsolutePath());
                System.out.println(generate_stats + " Stats file: " + statsFile.getAbsolutePath());

                try {
                    ExcelDocument excelDoc = new ExcelDocument(transcripts);
                    if (generate_stats)
                        excelDoc.makeStatsFile(statsFile);
                    if (generate_data)
                        excelDoc.makeDataFile(dataFile);

                } catch (Exception ex) {
                    Main.fatalError("Unexpected error: " + ex.getMessage());
                }

            }
        });
    }

    private void populateFileList(File[] files) {

        transcripts = new LinkedList<>();
        Transcript t;

        for (File f : files) {

            try {
                t = new Transcript(f);
                transcripts.add(t);
            } catch (TranscriptParseError transcriptParseError) {
                transcriptParseError.printStackTrace();
                Main.fatalError(transcriptParseError.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
                Main.fatalError("Unknown error while parsing document " + f.getName() + ": \n" + ex.getMessage());
            }
        }

        num_files.setText(String.format("%d", transcripts.size()));
        file_list.setListData(transcripts.stream().map(f -> f.getName()).toArray());

    }

    private void setSelectionForAll() {

        if (transcripts.isEmpty()) {
            Main.fatalError("No transcripts selected. Please choose at least one transcript.");
        }

        int start = Transcript.convertToSeconds(getStart());
        int end = Transcript.convertToSeconds(getEnd());

        int type = 0;
        if (getComplete()) {
            type = 1;
        } else if (getOrientationPhase()) {
            type = 2;
        } else if (getDraftingPhase()) {
            type = 3;
        } else if (getRevisionPhase()) {
            type = 4;
        }

        for (Transcript t : transcripts) {
            t.setSelection(type, start, end);
        }
    }

    public String getStart() {

        return startTimeField.getText();
    }

    private boolean adjust() {
        return timeInSelectedPhaseRadioButton.isSelected();
    }

    public String getEnd() {
        return endTimeField.getText();
    }

    public boolean getComplete() {
        return completeProcessRadioButton.isSelected();
    }

    public boolean getOrientationPhase() {
        return orientationPhaseRadioButton.isSelected();
    }

    public boolean getDraftingPhase() {
        return draftingPhaseRadioButton.isSelected();
    }

    public boolean getRevisionPhase() {
        return revisionPhaseRadioButton.isSelected();
    }

    public boolean getIndividualGraphs() {
        return showIndividualSubgraphsCheckBox.isSelected();
    }

    public boolean getCombinedRevisions() {
        return combinedRevisionStandard.isSelected();
    }

    public boolean getCombinedRevisionsCustom() {
        return combinedRevisionCustom.isSelected();
    }

    /**
     * Returns whether the interrupts should be displayed.
     *
     * @return true if the interrupts should be shown.
     */
    public boolean getInterrupts() {
        return interruptionsCheckBox.isSelected();
    }

    /**
     * Determines whether the pauses should be displayed.
     *
     * @return true if the pauses should be displayed
     */
    public boolean getPauses() {
        return pausesCheckBox.isSelected();
    }

    /**
     * Determines whether the revisions should be displayed.
     *
     * @return true if the revisions should be displayed
     */
    public boolean getRevisions() {
        return revisionsCheckBox.isSelected();
    }

    /**
     * Determines whether the consults should be displayed.
     *
     * @return true if the consults should be displayed
     */
    public boolean getConsults() {
        return consultsCheckBox.isSelected();
    }

    /**
     * Determines whether the typos should be displayed.
     *
     * @return true if the typos should be displayed
     */
    public boolean getTypos() {
        return typosCheckBox.isSelected();
    }

    public boolean getMatches() {
        return matchesCheckBox.isSelected();
    }

    /**
     * Determines whether the individual consults should be displayed.
     *
     * @return true if the individual consults should be displayed
     */
    public boolean getIndConsults() {
        return consultsSubgroupsCheckBox.isSelected();
    }

    /**
     * Determines whether the individual revisions should be displayed.
     *
     * @return true if the individual revision should be displayed
     */
    public boolean getIndRevisions() {
        return revisionsSubgroupsCheckBox.isSelected();
    }

    /**
     * Determines whether the individual interrupts should be displayed.
     *
     * @return true if the individual interrupts should be displayed
     */
    public boolean getIndInterrupts() {
        return interruptionsSubgroupsCheckBox.isSelected();
    }

    /**
     * Determines whether the individual pauses should be displayed.
     *
     * @return true if the individual pauses should be displayed
     */
    public boolean getIndPauses() {
        return pausesSubgroupsCheckBox.isSelected();
    }

    /**
     * Determines whether the writing actions should be displayed.
     *
     * @return true if the writing actions interrupts should be displayed
     */
    public boolean getWriting() {
        return TTActionsCheckBox.isSelected();
    }

    /**
     * Determines whether the ST actions should be displayed.
     *
     * @return true if the ST actions should be displayed
     */
    public boolean getSTActions() {
        return STActionsCheckBox.isSelected();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        main_panel = new JPanel();
        main_panel.setLayout(new GridLayoutManager(3, 5, new Insets(10, 10, 10, 10), -1, -1));
        chooseFolderButton = new JButton();
        chooseFolderButton.setText("Choose folder");
        main_panel.add(chooseFolderButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chooseFilesButton = new JButton();
        chooseFilesButton.setText("Choose file(s)");
        main_panel.add(chooseFilesButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 29), null, 0, false));
        tabbedPane1 = new JTabbedPane();
        main_panel.add(tabbedPane1, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 400), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(10, 2, new Insets(0, 5, 0, 0), -1, -1));
        tabbedPane1.addTab("Standard Graphs", panel1);
        mainGraphCheckBox = new JCheckBox();
        mainGraphCheckBox.setSelected(true);
        mainGraphCheckBox.setText("Main graph");
        panel1.add(mainGraphCheckBox, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        consultsGraphCheckBox = new JCheckBox();
        consultsGraphCheckBox.setText("Consults graph");
        panel1.add(consultsGraphCheckBox, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        revisionsGraphCheckBox = new JCheckBox();
        revisionsGraphCheckBox.setText("Revisions graph");
        panel1.add(revisionsGraphCheckBox, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pausesGraphCheckBox = new JCheckBox();
        pausesGraphCheckBox.setText("Pauses graph");
        panel1.add(pausesGraphCheckBox, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showIndividualSubgraphsCheckBox = new JCheckBox();
        showIndividualSubgraphsCheckBox.setText("Show individual graph for each process");
        panel1.add(showIndividualSubgraphsCheckBox, new GridConstraints(4, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Revisions display options:");
        panel1.add(label1, new GridConstraints(6, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        onlyRevisionRadioButton1 = new JRadioButton();
        onlyRevisionRadioButton1.setSelected(true);
        onlyRevisionRadioButton1.setText("only revision");
        panel1.add(onlyRevisionRadioButton1, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        combinedRevisionStandard = new JRadioButton();
        combinedRevisionStandard.setText("revision and revision2");
        panel1.add(combinedRevisionStandard, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        displayGraphsButton = new JButton();
        displayGraphsButton.setText("Display graphs");
        panel1.add(displayGraphsButton, new GridConstraints(9, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(12, 2, new Insets(0, 5, 0, 0), -1, -1));
        tabbedPane1.addTab("Custom graph", panel2);
        pausesCheckBox = new JCheckBox();
        pausesCheckBox.setText("Pauses");
        panel2.add(pausesCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        consultsCheckBox = new JCheckBox();
        consultsCheckBox.setText("Consults");
        panel2.add(consultsCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        typosCheckBox = new JCheckBox();
        typosCheckBox.setText("Typos");
        panel2.add(typosCheckBox, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TTActionsCheckBox = new JCheckBox();
        TTActionsCheckBox.setText("TT actions");
        panel2.add(TTActionsCheckBox, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        revisionsCheckBox = new JCheckBox();
        revisionsCheckBox.setText("Revisions");
        panel2.add(revisionsCheckBox, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        interruptionsCheckBox = new JCheckBox();
        interruptionsCheckBox.setText("Interruptions");
        panel2.add(interruptionsCheckBox, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        STActionsCheckBox = new JCheckBox();
        STActionsCheckBox.setText("ST actions");
        panel2.add(STActionsCheckBox, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Revision display options");
        panel2.add(label2, new GridConstraints(8, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        onlyRevisionRadioButton = new JRadioButton();
        onlyRevisionRadioButton.setSelected(true);
        onlyRevisionRadioButton.setText("only revision");
        panel2.add(onlyRevisionRadioButton, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        combinedRevisionCustom = new JRadioButton();
        combinedRevisionCustom.setText("revision and revision2");
        panel2.add(combinedRevisionCustom, new GridConstraints(9, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        displayGraphButton = new JButton();
        displayGraphButton.setText("Display graph");
        panel2.add(displayGraphButton, new GridConstraints(11, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pausesSubgroupsCheckBox = new JCheckBox();
        pausesSubgroupsCheckBox.setText("Pauses subgroups");
        panel2.add(pausesSubgroupsCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        consultsSubgroupsCheckBox = new JCheckBox();
        consultsSubgroupsCheckBox.setText("Consults subgroups");
        panel2.add(consultsSubgroupsCheckBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        revisionsSubgroupsCheckBox = new JCheckBox();
        revisionsSubgroupsCheckBox.setText("Revisions subgroups");
        panel2.add(revisionsSubgroupsCheckBox, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        interruptionsSubgroupsCheckBox = new JCheckBox();
        interruptionsSubgroupsCheckBox.setText("Interruptions subgroups");
        panel2.add(interruptionsSubgroupsCheckBox, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel2.add(spacer3, new GridConstraints(10, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        matchesCheckBox = new JCheckBox();
        matchesCheckBox.setText("Matches");
        panel2.add(matchesCheckBox, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel2.add(spacer4, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(5, 3, new Insets(0, 5, 0, 0), -1, -1));
        tabbedPane1.addTab("Data", panel3);
        createExcelFilesButton = new JButton();
        createExcelFilesButton.setText("Create Excel files");
        panel3.add(createExcelFilesButton, new GridConstraints(4, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        excelDirectoryField = new JTextField();
        excelDirectoryField.setEditable(false);
        excelDirectoryField.setEnabled(true);
        panel3.add(excelDirectoryField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Directory");
        panel3.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Data filename");
        panel3.add(label4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dataXlsTextField = new JTextField();
        dataXlsTextField.setText("data.xls");
        panel3.add(dataXlsTextField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        createDataCheckBox = new JCheckBox();
        createDataCheckBox.setSelected(true);
        createDataCheckBox.setText("Create?");
        panel3.add(createDataCheckBox, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Statistics filename");
        panel3.add(label5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        statsXlsTextField = new JTextField();
        statsXlsTextField.setText("stats.xls");
        panel3.add(statsXlsTextField, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        createStatsCheckBox = new JCheckBox();
        createStatsCheckBox.setSelected(true);
        createStatsCheckBox.setText("Create?");
        panel3.add(createStatsCheckBox, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel3.add(spacer5, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(11, 63), null, 0, false));
        chooseButton = new JButton();
        chooseButton.setText("Choose...");
        panel3.add(chooseButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(13, 3, new Insets(30, 0, 15, 0), -1, -1));
        main_panel.add(panel4, new GridConstraints(2, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 400), null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Time span of process:");
        panel4.add(label6, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        completeProcessRadioButton = new JRadioButton();
        completeProcessRadioButton.setSelected(true);
        completeProcessRadioButton.setText("Complete Process");
        panel4.add(completeProcessRadioButton, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        orientationPhaseRadioButton = new JRadioButton();
        orientationPhaseRadioButton.setText("Orientation Phase");
        panel4.add(orientationPhaseRadioButton, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        draftingPhaseRadioButton = new JRadioButton();
        draftingPhaseRadioButton.setText("Drafting Phase");
        panel4.add(draftingPhaseRadioButton, new GridConstraints(3, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        revisionPhaseRadioButton = new JRadioButton();
        revisionPhaseRadioButton.setText("Revision Phase");
        panel4.add(revisionPhaseRadioButton, new GridConstraints(4, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        selectedSegmentRadioButton = new JRadioButton();
        selectedSegmentRadioButton.setText("Selected segment:");
        panel4.add(selectedSegmentRadioButton, new GridConstraints(5, 0, 2, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Start:");
        panel4.add(label7, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        startTimeField = new JTextField();
        startTimeField.setText("00:00:00");
        panel4.add(startTimeField, new GridConstraints(7, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(114, 28), null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("End:");
        panel4.add(label8, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        endTimeField = new JTextField();
        endTimeField.setText("00:00:00");
        panel4.add(endTimeField, new GridConstraints(8, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(114, 28), null, 0, false));
        final Spacer spacer6 = new Spacer();
        panel4.add(spacer6, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(10, -1), new Dimension(15, -1), new Dimension(30, -1), 0, false));
        final Spacer spacer7 = new Spacer();
        panel4.add(spacer7, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(10, -1), new Dimension(15, -1), new Dimension(30, -1), 0, false));
        final Spacer spacer8 = new Spacer();
        panel4.add(spacer8, new GridConstraints(9, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        actualTimeInProcessRadioButton = new JRadioButton();
        actualTimeInProcessRadioButton.setSelected(true);
        actualTimeInProcessRadioButton.setText("actual time in process");
        panel4.add(actualTimeInProcessRadioButton, new GridConstraints(11, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Time axis:");
        panel4.add(label9, new GridConstraints(10, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        timeInSelectedPhaseRadioButton = new JRadioButton();
        timeInSelectedPhaseRadioButton.setText("time in selected phase");
        panel4.add(timeInSelectedPhaseRadioButton, new GridConstraints(12, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Messages:");
        main_panel.add(label10, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clearAllButton = new JButton();
        clearAllButton.setText("Clear all");
        main_panel.add(clearAllButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer9 = new Spacer();
        main_panel.add(spacer9, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        main_panel.add(scrollPane1, new GridConstraints(1, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        file_list = new JList();
        final DefaultListModel defaultListModel1 = new DefaultListModel();
        file_list.setModel(defaultListModel1);
        scrollPane1.setViewportView(file_list);
        final JScrollPane scrollPane2 = new JScrollPane();
        main_panel.add(scrollPane2, new GridConstraints(1, 4, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(250, -1), new Dimension(300, -1), new Dimension(500, -1), 0, false));
        messages = new JTextPane();
        messages.setEditable(false);
        messages.setEnabled(true);
        messages.setText("");
        scrollPane2.setViewportView(messages);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(3, 3, new Insets(0, 5, 0, 0), -1, -1));
        main_panel.add(panel5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("Chosen files:");
        panel5.add(label11, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        num_files = new JLabel();
        num_files.setText("0");
        panel5.add(num_files, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer10 = new Spacer();
        panel5.add(spacer10, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label12 = new JLabel();
        label12.setText("Total:");
        panel5.add(label12, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer11 = new Spacer();
        panel5.add(spacer11, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(completeProcessRadioButton);
        buttonGroup.add(orientationPhaseRadioButton);
        buttonGroup.add(draftingPhaseRadioButton);
        buttonGroup.add(revisionPhaseRadioButton);
        buttonGroup.add(selectedSegmentRadioButton);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(onlyRevisionRadioButton);
        buttonGroup.add(combinedRevisionCustom);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(onlyRevisionRadioButton1);
        buttonGroup.add(combinedRevisionStandard);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(actualTimeInProcessRadioButton);
        buttonGroup.add(timeInSelectedPhaseRadioButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return main_panel;
    }
}
