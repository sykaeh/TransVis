package com.sybil_ehrensberger.transvis;

import javax.swing.*;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

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
 *
 * @author Sybil Ehrensberger
 * @version 2.0
 */
public class FileHandler {

    /** List of files that contain the transcripts. */
    public List<File> fileList;
    /** Directory to store the excel files to. */
    public File excelDirectory;

    public FileHandler() {
        fileList = new LinkedList<>();
    }

    /**
     * Method invoked when the "Add file" button is clicked.
     *
     * @param parent    the parent panel for the dialog window
     */
    public void showAddFileChooser(JPanel parent) {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setDialogTitle("Select transcript");
        fileChooser.setFileFilter(new XMLFileFilter());

        int returnVal = fileChooser.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            fileList.add(f);
        } else {
            System.out.println("File access cancelled by user.");
        }
    }

    /**
     * Method invoked when the "Add folder" button is clicked.
     *
     * @param parent    the parent panel for the dialog window
     * @throws TranscriptError if the chosen file is not a folder
     */
    public void showFolderChooser(JPanel parent) throws TranscriptError {

        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setDialogTitle("Choose directory with transcripts");
        folderChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

        int returnVal = folderChooser.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File f = folderChooser.getSelectedFile();
            if (f.isDirectory()) {
                addSubFiles(f);
            } else {
                throw new TranscriptError("Chosen item is not a folder!");
            }

        } else {
            System.out.println("File access cancelled by user.");
        }
    }

    public void showSaveFileChooser(JPanel parent) {

        JFileChooser saveFileChooser = new JFileChooser();

        saveFileChooser.setDialogTitle("Select directory to save files");
        saveFileChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        if (excelDirectory != null)
            saveFileChooser.setCurrentDirectory(excelDirectory);

        int returnVal = saveFileChooser.showSaveDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            excelDirectory = saveFileChooser.getSelectedFile();
        } else {
            System.out.println("File access cancelled by user.");
        }

    }

    /**
     * Recursively goes through the directory f and adds all xml-files
     * contained in that directory to the fileList.
     *
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
            }
        }
    }

}
