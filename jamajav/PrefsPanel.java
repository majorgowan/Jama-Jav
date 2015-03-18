package jamajav;

// Swing packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// For input/output
import java.io.*;

// For resizable arrays:
import java.util.ArrayList;

class PrefsPanel extends JPanel implements ActionListener {

    private Prefs prefs;
    int[] metroset = new int[2];
    int[] cparam = new int[2];
    private String userName, userCity;
    private String avatar;

    private JTextField bpMinField;
    private JTextField bpMeasField;
    private JTextField countInField;
    private JTextField userNameField;
    private JTextField userCityField;
    private JLabel avatarLabel;
    private JButton changeAvatarButton;

    private ArrayList<Avatar> avatars;
    private JDialog prefsDialog;

    public void actionPerformed(ActionEvent ae) {

        String comStr = ae.getActionCommand();
        switch (comStr) {
            case ("changeAvatar") :
                final JDialog avatarDialog = new JDialog(prefsDialog, "Choose Avatar", true);
                avatarDialog.setLocationRelativeTo(prefsDialog);
                avatarDialog.getContentPane().setLayout(new BorderLayout());
                avatarDialog.getContentPane().add(
                        new AvatarPanel(avatars, prefs), BorderLayout.CENTER);
                avatarDialog.revalidate();
                avatarDialog.pack();
                avatarDialog.setVisible(true);
                avatar = prefs.getAvatar();
                avatarLabel.setIcon(
                        new ImageIcon(avatars.get(findAvatarIndex(avatar)).getImage()));
                revalidate();
                break;

            case ("save") :
                metroset[0] = Integer.parseInt(bpMinField.getText());
                metroset[1] = Integer.parseInt(bpMeasField.getText());
                cparam[0] = Integer.parseInt(countInField.getText());
                cparam[1] = 100;    // precision of clock (in milliseconds)
                userName = userNameField.getText();
                userCity = userCityField.getText();

                prefs.setPrefs(metroset, cparam, userName, userCity, avatar);
                prefs.writePrefsFile();
                break;

            case ("Ok") :
                metroset[0] = Integer.parseInt(bpMinField.getText());
                metroset[1] = Integer.parseInt(bpMeasField.getText());
                cparam[0] = Integer.parseInt(countInField.getText());
                cparam[1] = 100;    // precision of clock (in milliseconds)
                userName = userNameField.getText();
                userCity = userCityField.getText();

                prefs.setPrefs(metroset, cparam, userName, userCity, avatar);

            case ("Cancel") :
                // change nothing
                SwingUtilities.windowForComponent(this).setVisible(false);
                SwingUtilities.windowForComponent(this).dispose();
                break;
        }
    }

    private int findAvatarIndex(String name) {
        for (int i = 0; i < avatars.size(); i++)
            if (avatars.get(i).getName().equals(name)) {
                return i;
            }

        return 0;
    }

    PrefsPanel(Prefs p, ArrayList<Avatar> avs, JDialog pD) {

        prefs = p;
        prefsDialog = pD;
        avatars = avs;

        metroset = prefs.getMetroSet();
        cparam = prefs.getClockParam();
        userName = prefs.getUserName();
        userCity = prefs.getUserCity();
        
        avatar = prefs.getAvatar();
        int avatarIndex = 0;

        bpMinField = new JTextField("" + metroset[0],4);
        bpMeasField = new JTextField("" + metroset[1],2);
        countInField = new JTextField("" + cparam[0],4);
        userNameField = new JTextField(userName,15);
        userCityField = new JTextField(userCity,15);
        changeAvatarButton = new JButton("Change");
        changeAvatarButton.setActionCommand("changeAvatar");
        changeAvatarButton.addActionListener(this);

        JPanel avatarPanel = new JPanel(new BorderLayout());
        avatarLabel = new JLabel(
                new ImageIcon(avatars.get(findAvatarIndex(avatar)).getImage()));
        avatarPanel.add(avatarLabel);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        JPanel line1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel line2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel line3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel line4 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel line5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel line6 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel line7 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel line8 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel line9 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        line1.add(new JLabel("User Name:",JLabel.RIGHT));
        line1.add(userNameField);
        line2.add(new JLabel("User City:",JLabel.RIGHT));
        line2.add(userCityField);
        line3.add(new JLabel("Avatar:"));
        line4.add(avatarPanel);
        line4.add(changeAvatarButton);
        line5.add(new JLabel("Metronome settings:"));
        line6.add(new JLabel("Beats per minute:",JLabel.RIGHT));
        line6.add(bpMinField);
        line7.add(new JLabel("Beats per measure:",JLabel.RIGHT));
        line7.add(bpMeasField);
        line8.add(new JLabel("Clock settings:"));
        line9.add(new JLabel("Count-in (seconds):",JLabel.RIGHT));
        line9.add(countInField);
        mainPanel.add(line1);
        mainPanel.add(line2);
        mainPanel.add(line3);
        mainPanel.add(line4);
        mainPanel.add(line5);
        mainPanel.add(line6);
        mainPanel.add(line7);
        mainPanel.add(line8);
        mainPanel.add(line9);

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

