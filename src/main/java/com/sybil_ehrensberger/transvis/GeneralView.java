package com.sybil_ehrensberger.transvis;

import javax.swing.*;
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

}
