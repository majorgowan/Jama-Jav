package jamajav;

// Swing packages:
import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.*;

// For date stuff:
import java.util.Calendar;
import java.text.*;

class ChatPanel extends JPanel implements ActionListener {

    // future: Timer to periodically poll for new entries from web
    // on submit, first poll web for new entries and then add this one
    // and update web

    private TrackPanel trackPanel;
    private Chat chat;
    private Prefs prefs;

    private JPanel mainPanel;
    private JTextField entryField;

    public void actionPerformed(ActionEvent ae) {
        String comStr = ae.getActionCommand();

        switch (comStr) {
            case ("submit") :
                submitEntry();
                break;
        }
    }

    private void submitEntry() {
        Calendar currentTime = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        ChatEntry entry = 
            new ChatEntry(
                    entryField.getText(), 
                    prefs.getAvatar(),
                    prefs.getUserName(),
                    prefs.getUserCity(),
                    dateFormat.format(currentTime.getTime())
                    );
        chat.add(entry);
        mainPanel.add(new ChatBubble(trackPanel, entry));
        mainPanel.add(Box.createRigidArea(new Dimension(0,5)));
        mainPanel.revalidate();
        entryField.setText("");

        scrollRectToVisible(
                new Rectangle(
                    0,(int)getPreferredSize().getHeight(),10,10));
    }

    public void requestFocus() {
        entryField.requestFocusInWindow();
    }

    public void refreshChat() {
        mainPanel.removeAll();
        for (int i = 0; i < chat.getSize(); i++) {
            mainPanel.add(new ChatBubble(trackPanel, chat.get(i)));
            mainPanel.add(Box.createRigidArea(new Dimension(0,5)));
        }
    }

    ChatPanel(TrackPanel tpnl, Prefs p, Chat ch) {
        chat = ch;
        trackPanel = tpnl;
        prefs = p;

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        refreshChat();

        JPanel outerMainPanel = new JPanel();
        outerMainPanel.add(mainPanel);

        JPanel entryPanel = new JPanel();
        
        entryField = new JTextField(40);
        entryField.setActionCommand("submit");
        entryField.addActionListener(this);

        JButton submitButton = new JButton("Submit");
        submitButton.setActionCommand("submit");
        submitButton.addActionListener(this);
        entryPanel.add(entryField);
        entryPanel.add(submitButton);

        setLayout(new BorderLayout());
        add(outerMainPanel,BorderLayout.CENTER);
        add(entryPanel,BorderLayout.PAGE_END);

        mainPanel.setBackground(JamaJav.clickedColour);
        outerMainPanel.setBackground(JamaJav.clickedColour);
        entryPanel.setBackground(JamaJav.clickedColour);

        // scroll to the bottom to expose entryPanel
        scrollRectToVisible(
                new Rectangle(
                    0,(int)getPreferredSize().getHeight(),10,10));
    }
}
