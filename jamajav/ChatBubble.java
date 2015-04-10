package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;

// For avatar images
import java.awt.image.*;
import java.net.URL;

class ChatBubble extends JPanel {

    ChatBubble(TrackPanel trackPanel, ChatEntry entry) {

        String text = entry.getText();
        String avatar = entry.getAvatar();
        String contributor = entry.getContributor();
        String location = entry.getLocation();
        String date = entry.getDate();

        Font infoFont = new Font("SansSerif",Font.PLAIN,12);
        Font textFont = new Font("SansSerif",Font.PLAIN,14);

        // make rounded black border
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 3, true));

        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(JamaJav.unclickedColour);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.LINE_AXIS));
        JLabel coordLabel = new JLabel(location + "  " + date);
        coordLabel.setFont(infoFont);
        coordLabel.setForeground(JamaJav.darkGoldColour);
        coordLabel.setBackground(JamaJav.unclickedColour);
        JLabel contribLabel = new JLabel(contributor);
        contribLabel.setFont(infoFont);
        contribLabel.setForeground(JamaJav.darkGoldColour);
        contribLabel.setBackground(JamaJav.unclickedColour);
        infoPanel.add(contribLabel);
        infoPanel.add(Box.createHorizontalGlue());
        infoPanel.add(coordLabel);

        JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        textPanel.setBackground(JamaJav.unclickedColour);
        textPanel.setLayout(new BoxLayout(textPanel,BoxLayout.LINE_AXIS));

        JPanel avatarPanel = new JPanel();
        avatarPanel.setBackground(JamaJav.unclickedColour);
        JLabel avatarLabel = new JLabel();
        avatarLabel.setIcon(
                new ImageIcon(trackPanel.getAvatarImage(avatar)
                    .getScaledInstance(40,40,Image.SCALE_SMOOTH)));
        avatarPanel.add(avatarLabel);

        JPanel textAreaPanel = new JPanel();
        textAreaPanel.setBackground(JamaJav.unclickedColour);
        JTextArea textArea = new JTextArea();
        textArea.setColumns(40);
        textArea.setText(text);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        //textArea.setOpaque(false);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setFont(textFont);
        textArea.setForeground(Color.BLACK);
        textArea.setBackground(JamaJav.unclickedColour);
        textAreaPanel.add(textArea);

        textPanel.add(avatarPanel);
        textPanel.add(textAreaPanel);

        // two-row layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(4,0,4,0));
        mainPanel.setBackground(JamaJav.unclickedColour);
        mainPanel.add(infoPanel);
        mainPanel.add(textPanel);

        setBackground(JamaJav.unclickedColour);
        add(mainPanel);
    }
}
