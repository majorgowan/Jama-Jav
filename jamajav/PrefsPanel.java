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

    private Prefs prefs, oldPrefs;
    int[] metroset = new int[2];
    private String userName, userCity;
    private String avatar;

    private JTextField bpMinField;
    private JTextField bpMeasField;
    private JTextField countInField;
    private JTextField userNameField;
    private JTextField userCityField;
    private JCheckBox metronomeCheckBox;
    private JCheckBox karaokeCheckBox;
    private JCheckBox autoSelectNewTrackCheckBox;
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

            case ("Ok") :
                //should be redundant with focus listener effecting changes
                //and checking number format exceptions!
                //metroset[0] = Integer.parseInt(bpMinField.getText());
                //metroset[1] = Integer.parseInt(bpMeasField.getText());
                userName = userNameField.getText();
                userCity = userCityField.getText();

                oldPrefs.setPrefs(metroset, userName, userCity, avatar,
                        metronomeCheckBox.isSelected(),
                        karaokeCheckBox.isSelected(),
                        autoSelectNewTrackCheckBox.isSelected());
                oldPrefs.writePrefsFile();
                // drop through to close window (no break!)

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

        oldPrefs = p;
        prefs = new Prefs(oldPrefs);
        prefsDialog = pD;
        avatars = avs;

        metroset = prefs.getMetroSet();
        userName = prefs.getUserName();
        userCity = prefs.getUserCity();

        avatar = prefs.getAvatar();
        int avatarIndex = 0;

        bpMinField = new JTextField("" + metroset[0],4);
        bpMeasField = new JTextField("" + metroset[1],2);

        bpMinField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                try {
                    int b = Integer.parseInt(bpMinField.getText());
                    if (b < 0) {
                        throw new TimeOutOfRangeException("low");
                    }
                    metroset[0] = b;
                } catch (NumberFormatException nfe) {
                    bpMinField.setText("" + metroset[0]);
                } catch (TimeOutOfRangeException toore) {
                    bpMinField.setText("" + metroset[0]);
                }
            }
        });
        bpMeasField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                try {
                    int b = Integer.parseInt(bpMeasField.getText());
                    if (b < 0) {
                        throw new TimeOutOfRangeException("low");
                    }
                    metroset[1] = b;
                } catch (NumberFormatException nfe) {
                    bpMeasField.setText("" + metroset[1]);
                } catch (TimeOutOfRangeException toore) {
                    bpMeasField.setText("" + metroset[1]);
                }
            }
        });

        userNameField = new JTextField(userName,15);
        userCityField = new JTextField(userCity,15);
        changeAvatarButton = new JButton("Change");
        changeAvatarButton.setActionCommand("changeAvatar");
        changeAvatarButton.addActionListener(this);
        metronomeCheckBox = new JCheckBox("Show metronome on startup",
                oldPrefs.getShowMetronome());
        karaokeCheckBox = new JCheckBox("Show karaoke panel on startup",
                oldPrefs.getShowKaraoke());
        autoSelectNewTrackCheckBox = new JCheckBox("Autoselect new tracks",
                oldPrefs.getAutoSelectNewTrack());

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
        JPanel line9 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel line10 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel line11 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line1.add(new JLabel("User Name:",JLabel.RIGHT));
        line1.add(userNameField);
        line2.add(new JLabel("User City:",JLabel.RIGHT));
        line2.add(userCityField);
        line3.add(new JLabel("Avatar:"));
        line4.add(avatarPanel);
        line4.add(changeAvatarButton);
        line5.add(new JLabel("Default metronome settings:"));
        line6.add(new JLabel("Beats per minute:",JLabel.RIGHT));
        line6.add(bpMinField);
        line7.add(new JLabel("Beats per measure:",JLabel.RIGHT));
        line7.add(bpMeasField);
        line8.add(new JLabel("Miscellaneous:"));
        line9.add(metronomeCheckBox);
        line10.add(karaokeCheckBox);
        line11.add(autoSelectNewTrackCheckBox);

        mainPanel.add(line1);
        mainPanel.add(line2);
        mainPanel.add(line3);
        mainPanel.add(line4);
        mainPanel.add(line5);
        mainPanel.add(line6);
        mainPanel.add(line7);
        mainPanel.add(line8);
        mainPanel.add(line9);
        mainPanel.add(line10);
        mainPanel.add(line11);

        // for the future perhaps
        JScrollPane scrollPane = new JScrollPane(mainPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("Ok");
        okButton.addActionListener(this);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

}

