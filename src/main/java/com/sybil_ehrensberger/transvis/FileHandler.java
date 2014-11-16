package com.sybil_ehrensberger.transvis;

import javax.swing.*;
import java.io.File;
import java.io.FileFilter;

class XMLFileChooserFilter extends javax.swing.filechooser.FileFilter {

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

class XMLFileFilter implements FileFilter {

    @Override
    public boolean accept(File pathname) {
        if (pathname.getAbsolutePath().endsWith(".xml"))
            return true;
        else
            return false;
    }
}

/**
 * The application's main frame.
 *
 * @author Sybil Ehrensberger
 * @version 2.0
 */
public class FileHandler {

    /**
     * Method invoked when the "Add file" button is clicked.
     *
     * @param parent    the parent panel for the dialog window
     * @return          an array of the selected files
     */
    public File[] showAddFileChooser(JPanel parent) {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setDialogTitle("Select transcript(s)");
        fileChooser.setFileFilter(new XMLFileChooserFilter());
        fileChooser.setMultiSelectionEnabled(true);

        File[] files;
        int returnVal = fileChooser.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            files = fileChooser.getSelectedFiles();
        } else {
            System.out.println("File access cancelled by user.");
            files = new File[0];
        }

        return files;
    }

    /**
     * Method invoked when the "Add folder" button is clicked.
     *
     * @param parent    the parent panel for the dialog window
     * @return          an array of the selected files
     */
    public File[] showFolderChooser(JPanel parent) {

        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setDialogTitle("Choose directory with transcripts");
        folderChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

        File[] files;
        int returnVal = folderChooser.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File f = folderChooser.getSelectedFile();
            files = f.listFiles(new XMLFileFilter());

        } else {
            files = new File[0];
            System.out.println("File access cancelled by user.");
        }

        return files;
    }

    /**
     * Method invoked to choose a directory to save files to.
     *
     * @param parent            the parent panel for the dialog window
     * @param excelDirectory    the current directory for the file chooser
     * @return                  the selected directory
     */
    public File showSaveFileChooser(JPanel parent, File excelDirectory) {

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

        return excelDirectory;
    }

}
