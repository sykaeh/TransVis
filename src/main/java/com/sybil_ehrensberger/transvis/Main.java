package com.sybil_ehrensberger.transvis;

import javax.swing.*;

/**
 *
 *
 * @author Sybil Ehrensberger
 */
public class Main {

    public static GeneralView main_gv;

    public static void main(String[] args) {
        JFrame frame = new JFrame("GeneralView");
        main_gv = new GeneralView();
        frame.setContentPane(main_gv.main_panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


    public static void fatalError(String msg) {
        FatalErrorDialog dialog = new FatalErrorDialog(msg);
        dialog.pack();
        dialog.setVisible(true);
    }

    public static void note(String msg) {

        main_gv.messages.setText(msg + "\n" + main_gv.messages.getText());
    }
}
