package com.sybil_ehrensberger.transvis;

import javax.swing.*;
import java.awt.event.*;

public class FatalErrorDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JLabel error_msg;

    public FatalErrorDialog(String msg) {

        error_msg.setText(msg);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

    }

    private void onOK() {
// add your code here
        dispose();
    }


}
