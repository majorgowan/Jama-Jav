package jamajav;

// Swing packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// For input/output
import java.io.*;

class PrefsPanel extends JPanel implements ActionListener {

    private Prefs prefs;
    int[] metroset = new int[2];
    int[] cparam = new int[2];
    private String userName, userCity;

    private JTextField bpMinField;
    private JTextField bpMeasField;
    private JTextField countInField;
    private JTextField userNameField;
    private JTextField userCityField;

    public void actionPerformed(ActionEvent ae) {

        String comStr = ae.getActionCommand();
        switch (comStr) {
            case ("save") :
                metroset[0] = Integer.parseInt(bpMinField.getText());
                metroset[1] = Integer.parseInt(bpMeasField.getText());
                cparam[0] = Integer.parseInt(countInField.getText());
                cparam[1] = 100;    // precision of clock (in milliseconds)
                userName = userNameField.getText();
                userCity = userCityField.getText();

                prefs.setPrefs(metroset, cparam, userName, userCity);
                prefs.writePrefsFile();
                break;

            case ("Ok") :
                metroset[0] = Integer.parseInt(bpMinField.getText());
                metroset[1] = Integer.parseInt(bpMeasField.getText());
                cparam[0] = Integer.parseInt(countInField.getText());
                cparam[1] = 100;    // precision of clock (in milliseconds)
                userName = userNameField.getText();
                userCity = userCityField.getText();

                prefs.setPrefs(metroset, cparam, userName, userCity);

            case ("Cancel") :
                // change nothing
                SwingUtilities.windowForComponent(this).setVisible(false);
                SwingUtilities.windowForComponent(this).dispose();
                break;
        }
    }

    PrefsPanel(Prefs p) {

        prefs = p;

        metroset = prefs.getMetroSet();
        cparam = prefs.getClockParam();
        userName = prefs.getUserName();
        userCity = prefs.getUserCity();

        bpMinField = new JTextField("" + metroset[0],4);
        bpMeasField = new JTextField("" + metroset[1],2);
        countInField = new JTextField("" + cparam[0],4);
        userNameField = new JTextField(userName,15);
        userCityField = new JTextField(userCity,15);

        JPanel mainPanel = new JPanel(new GridLayout(7,1));
        JPanel line1 = new JPanel();
        JPanel line2 = new JPanel();
        JPanel line3 = new JPanel();
        JPanel line4 = new JPanel();
        JPanel line5 = new JPanel();
        JPanel line6 = new JPanel();
        JPanel line7 = new JPanel();
        line1.add(new JLabel("Metronome settings:"));
        line2.add(new JLabel("Beats per minute:",JLabel.RIGHT));
        line2.add(bpMinField);
        line3.add(new JLabel("Beats per measure:",JLabel.RIGHT));
        line3.add(bpMeasField);
        line4.add(new JLabel("Clock settings:"));
        line5.add(new JLabel("Count-in (seconds):",JLabel.RIGHT));
        line5.add(countInField);
        line6.add(new JLabel("User Name:",JLabel.RIGHT));
        line6.add(userNameField);
        line7.add(new JLabel("User City:",JLabel.RIGHT));
        line7.add(userCityField);
        mainPanel.add(line1);
        mainPanel.add(line2);
        mainPanel.add(line3);
        mainPanel.add(line4);
        mainPanel.add(line5);
        mainPanel.add(line6);
        mainPanel.add(line7);

        // for the future perhaps
        JScrollPane scrollPane = new JScrollPane(mainPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("Ok");
        okButton.addActionListener(this);
        JButton saveButton = new JButton("Save as Default");
        saveButton.setActionCommand("save");
        saveButton.addActionListener(this);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);

        buttonPanel.add(okButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

}

