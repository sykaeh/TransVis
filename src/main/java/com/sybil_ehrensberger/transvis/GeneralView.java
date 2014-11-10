package com.sybil_ehrensberger.transvis;

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
    private JButton chooseFolderButton;
    private JButton chooseFileSButton;
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
    public JPanel main_panel;
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
    public JTextPane messages;

    public GeneralView() {

        FileHandler fileHandler = new FileHandler();

        chooseFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    fileHandler.showFolderChooser(main_panel);
                    num_files.setText(String.format("%d", fileHandler.fileList.size()));
                    populateFileList(fileHandler.fileList);
                } catch (TranscriptError transcriptError) {
                    Main.fatalError(transcriptError.getMessage());
                    transcriptError.printStackTrace();
                }

            }
        });

        chooseFileSButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileHandler.showAddFileChooser(main_panel);
                num_files.setText(String.format("%d", fileHandler.fileList.size()));
                populateFileList(fileHandler.fileList);
            }
        });

        clearAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileHandler.fileList.clear();
                populateFileList(fileHandler.fileList);
                num_files.setText("0");

            }
        });

        displayGraphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    Graph graph = new Graph(parseFiles(fileHandler.fileList));
                    graph.generateGraphsClicked(Arrays.asList(GraphType.CUSTOM), showIndividualSubgraphsCheckBox.isSelected());
                } catch (TranscriptError transcriptError) {
                    Main.fatalError(transcriptError.getMessage());
                    transcriptError.printStackTrace();
                } catch (Exception ex) {
                    Main.fatalError("Unexpected error: " + ex.getMessage());
                    ex.printStackTrace();
                }

            }
        });

        displayGraphsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Graph graph;

                try {
                    graph = new Graph(parseFiles(fileHandler.fileList));
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

                } catch (TranscriptError transcriptError) {
                    Main.fatalError(transcriptError.getMessage());
                    transcriptError.printStackTrace();

                } catch (Exception ex) {
                    Main.fatalError("Unexpected error: " + ex.getMessage());
                    ex.printStackTrace();
                }

            }
        });

        chooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileHandler.showSaveFileChooser(main_panel);
                excelDirectoryField.setText(fileHandler.excelDirectory.getAbsolutePath());
            }
        });

        createExcelFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // TODO: check for proper directory and file names

                String directory_path = fileHandler.excelDirectory.getAbsolutePath();

                File dataFile = new File(directory_path + "/" + dataXlsTextField.getText());
                File statsFile = new File(directory_path + "/" + statsXlsTextField.getText());


                boolean generate_data = createDataCheckBox.isSelected();
                boolean generate_stats = createStatsCheckBox.isSelected();

                System.out.println(generate_data + " Data file: " + dataFile.getAbsolutePath());
                System.out.println(generate_stats + " Stats file: " + statsFile.getAbsolutePath());

                List<Transcript> transcripts = null;
                try {
                    transcripts = parseFiles(fileHandler.fileList);
                    ExcelDocument excelDoc = new ExcelDocument(transcripts);
                    if (generate_stats)
                        excelDoc.makeStatsFile(statsFile);
                    if (generate_data)
                        excelDoc.makeDataFile(dataFile);

                } catch (TranscriptError transcriptError) {
                    Main.fatalError(transcriptError.getMessage());

                } catch (Exception ex) {
                    Main.fatalError("Unexpected error: " + ex.getMessage());
                }

            }
        });
    }

    private void populateFileList(List<File> files) {
        file_list.setListData(files.stream().map(f -> f.getName()).toArray());
    }

    private List<Transcript> parseFiles(List<File> files) throws TranscriptError {

        // TODO: Move to initial selection of files?

        // TODO: Alert if list empty!
        if (files.isEmpty()) {
            throw new TranscriptError("No transcripts selected. Please choose at least one transcript.");
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

        List<Transcript> transcripts = new LinkedList<>();
        Transcript t;

        for (File f : files) {
            try {
                t = new Transcript(f);
                t.setSelection(type, start, end);
                transcripts.add(t);
            } catch (Exception ex) {
                throw new TranscriptError("Unknown error while parsing document " + f.getName() + ": \n" + ex.getMessage());
            }
        }
        return transcripts;
    }

    public String getStart() {

        return startTimeField.getText();
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
        main_panel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 5, new Insets(10, 10, 10, 10), -1, -1));
        chooseFolderButton = new JButton();
        chooseFolderButton.setText("Choose folder");
        main_panel.add(chooseFolderButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chooseFileSButton = new JButton();
        chooseFileSButton.setText("Choose file(s)");
        main_panel.add(chooseFileSButton, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 29), null, 0, false));
        tabbedPane1 = new JTabbedPane();
        main_panel.add(tabbedPane1, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(9, 2, new Insets(0, 5, 0, 0), -1, -1));
        tabbedPane1.addTab("Standard Graphs", panel1);
        mainGraphCheckBox = new JCheckBox();
        mainGraphCheckBox.setSelected(true);
        mainGraphCheckBox.setText("Main graph");
        panel1.add(mainGraphCheckBox, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        consultsGraphCheckBox = new JCheckBox();
        consultsGraphCheckBox.setText("Consults graph");
        panel1.add(consultsGraphCheckBox, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        revisionsGraphCheckBox = new JCheckBox();
        revisionsGraphCheckBox.setText("Revisions graph");
        panel1.add(revisionsGraphCheckBox, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pausesGraphCheckBox = new JCheckBox();
        pausesGraphCheckBox.setText("Pauses graph");
        panel1.add(pausesGraphCheckBox, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showIndividualSubgraphsCheckBox = new JCheckBox();
        showIndividualSubgraphsCheckBox.setText("Show individual graph for each process");
        panel1.add(showIndividualSubgraphsCheckBox, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Revisions display options:");
        panel1.add(label1, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        onlyRevisionRadioButton1 = new JRadioButton();
        onlyRevisionRadioButton1.setSelected(true);
        onlyRevisionRadioButton1.setText("only revision");
        panel1.add(onlyRevisionRadioButton1, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        combinedRevisionStandard = new JRadioButton();
        combinedRevisionStandard.setText("revision and revision2");
        panel1.add(combinedRevisionStandard, new com.intellij.uiDesigner.core.GridConstraints(6, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        displayGraphsButton = new JButton();
        displayGraphsButton.setText("Display graphs");
        panel1.add(displayGraphsButton, new com.intellij.uiDesigner.core.GridConstraints(8, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel1.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(11, 2, new Insets(0, 5, 0, 0), -1, -1));
        tabbedPane1.addTab("Custom graph", panel2);
        pausesCheckBox = new JCheckBox();
        pausesCheckBox.setText("Pauses");
        panel2.add(pausesCheckBox, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        consultsCheckBox = new JCheckBox();
        consultsCheckBox.setText("Consults");
        panel2.add(consultsCheckBox, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        typosCheckBox = new JCheckBox();
        typosCheckBox.setText("Typos");
        panel2.add(typosCheckBox, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TTActionsCheckBox = new JCheckBox();
        TTActionsCheckBox.setText("TT actions");
        panel2.add(TTActionsCheckBox, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        revisionsCheckBox = new JCheckBox();
        revisionsCheckBox.setText("Revisions");
        panel2.add(revisionsCheckBox, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        interruptionsCheckBox = new JCheckBox();
        interruptionsCheckBox.setText("Interruptions");
        panel2.add(interruptionsCheckBox, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        STActionsCheckBox = new JCheckBox();
        STActionsCheckBox.setText("ST actions");
        panel2.add(STActionsCheckBox, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Revision display options");
        panel2.add(label2, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        onlyRevisionRadioButton = new JRadioButton();
        onlyRevisionRadioButton.setSelected(true);
        onlyRevisionRadioButton.setText("only revision");
        panel2.add(onlyRevisionRadioButton, new com.intellij.uiDesigner.core.GridConstraints(8, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        combinedRevisionCustom = new JRadioButton();
        combinedRevisionCustom.setText("revision and revision2");
        panel2.add(combinedRevisionCustom, new com.intellij.uiDesigner.core.GridConstraints(8, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        displayGraphButton = new JButton();
        displayGraphButton.setText("Display graph");
        panel2.add(displayGraphButton, new com.intellij.uiDesigner.core.GridConstraints(10, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pausesSubgroupsCheckBox = new JCheckBox();
        pausesSubgroupsCheckBox.setText("Pauses subgroups");
        panel2.add(pausesSubgroupsCheckBox, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        consultsSubgroupsCheckBox = new JCheckBox();
        consultsSubgroupsCheckBox.setText("Consults subgroups");
        panel2.add(consultsSubgroupsCheckBox, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        revisionsSubgroupsCheckBox = new JCheckBox();
        revisionsSubgroupsCheckBox.setText("Revisions subgroups");
        panel2.add(revisionsSubgroupsCheckBox, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        interruptionsSubgroupsCheckBox = new JCheckBox();
        interruptionsSubgroupsCheckBox.setText("Interruptions subgroups");
        panel2.add(interruptionsSubgroupsCheckBox, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        panel2.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(9, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        matchesCheckBox = new JCheckBox();
        matchesCheckBox.setText("Matches");
        panel2.add(matchesCheckBox, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(5, 3, new Insets(0, 5, 0, 0), -1, -1));
        tabbedPane1.addTab("Data", panel3);
        createExcelFilesButton = new JButton();
        createExcelFilesButton.setText("Create Excel files");
        panel3.add(createExcelFilesButton, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        excelDirectoryField = new JTextField();
        excelDirectoryField.setEditable(false);
        excelDirectoryField.setEnabled(true);
        panel3.add(excelDirectoryField, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Directory");
        panel3.add(label3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Data filename");
        panel3.add(label4, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dataXlsTextField = new JTextField();
        dataXlsTextField.setText("data.xls");
        panel3.add(dataXlsTextField, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        createDataCheckBox = new JCheckBox();
        createDataCheckBox.setSelected(true);
        createDataCheckBox.setText("Create?");
        panel3.add(createDataCheckBox, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Statistics filename");
        panel3.add(label5, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        statsXlsTextField = new JTextField();
        statsXlsTextField.setText("stats.xls");
        panel3.add(statsXlsTextField, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        createStatsCheckBox = new JCheckBox();
        createStatsCheckBox.setSelected(true);
        createStatsCheckBox.setText("Create?");
        panel3.add(createStatsCheckBox, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer3 = new com.intellij.uiDesigner.core.Spacer();
        panel3.add(spacer3, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(11, 63), null, 0, false));
        chooseButton = new JButton();
        chooseButton.setText("Choose...");
        panel3.add(chooseButton, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(10, 3, new Insets(30, 0, 0, 0), -1, -1));
        main_panel.add(panel4, new com.intellij.uiDesigner.core.GridConstraints(3, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Time span of process:");
        panel4.add(label6, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        completeProcessRadioButton = new JRadioButton();
        completeProcessRadioButton.setSelected(true);
        completeProcessRadioButton.setText("Complete Process");
        panel4.add(completeProcessRadioButton, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        orientationPhaseRadioButton = new JRadioButton();
        orientationPhaseRadioButton.setText("Orientation Phase");
        panel4.add(orientationPhaseRadioButton, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        draftingPhaseRadioButton = new JRadioButton();
        draftingPhaseRadioButton.setText("Drafting Phase");
        panel4.add(draftingPhaseRadioButton, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        revisionPhaseRadioButton = new JRadioButton();
        revisionPhaseRadioButton.setText("Revision Phase");
        panel4.add(revisionPhaseRadioButton, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        selectedSegmentRadioButton = new JRadioButton();
        selectedSegmentRadioButton.setText("Selected segment:");
        panel4.add(selectedSegmentRadioButton, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 2, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Start:");
        panel4.add(label7, new com.intellij.uiDesigner.core.GridConstraints(7, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        startTimeField = new JTextField();
        startTimeField.setText("00:00:00");
        panel4.add(startTimeField, new com.intellij.uiDesigner.core.GridConstraints(7, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(114, 28), null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("End:");
        panel4.add(label8, new com.intellij.uiDesigner.core.GridConstraints(8, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        endTimeField = new JTextField();
        endTimeField.setText("00:00:00");
        panel4.add(endTimeField, new com.intellij.uiDesigner.core.GridConstraints(8, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(114, 28), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer4 = new com.intellij.uiDesigner.core.Spacer();
        panel4.add(spacer4, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(10, -1), new Dimension(10, -1), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer5 = new com.intellij.uiDesigner.core.Spacer();
        panel4.add(spacer5, new com.intellij.uiDesigner.core.GridConstraints(8, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer6 = new com.intellij.uiDesigner.core.Spacer();
        panel4.add(spacer6, new com.intellij.uiDesigner.core.GridConstraints(9, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Messages:");
        main_panel.add(label9, new com.intellij.uiDesigner.core.GridConstraints(0, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clearAllButton = new JButton();
        clearAllButton.setText("Clear all");
        main_panel.add(clearAllButton, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer7 = new com.intellij.uiDesigner.core.Spacer();
        main_panel.add(spacer7, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer8 = new com.intellij.uiDesigner.core.Spacer();
        main_panel.add(spacer8, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        main_panel.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        file_list = new JList();
        final DefaultListModel defaultListModel1 = new DefaultListModel();
        file_list.setModel(defaultListModel1);
        scrollPane1.setViewportView(file_list);
        final JScrollPane scrollPane2 = new JScrollPane();
        main_panel.add(scrollPane2, new com.intellij.uiDesigner.core.GridConstraints(1, 4, 3, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(250, -1), new Dimension(300, -1), new Dimension(500, -1), 0, false));
        messages = new JTextPane();
        messages.setEditable(false);
        messages.setEnabled(false);
        messages.setText("");
        scrollPane2.setViewportView(messages);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 3, new Insets(0, 5, 0, 0), -1, -1));
        main_panel.add(panel5, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Chosen files:");
        panel5.add(label10, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        num_files = new JLabel();
        num_files.setText("0");
        panel5.add(num_files, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer9 = new com.intellij.uiDesigner.core.Spacer();
        panel5.add(spacer9, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("Total:");
        panel5.add(label11, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer10 = new com.intellij.uiDesigner.core.Spacer();
        panel5.add(spacer10, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
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
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return main_panel;
    }
}
