package jamajav;

// Swing packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// For input/output
import java.io.*;

class AvatarPanel extends JPanel implements  MouseListener {

    private Prefs prefs;
    private Avatar[] avatars;
    private JPanel[] avatarPanels;

    public void mouseClicked(MouseEvent me) {
        if (SwingUtilities.isLeftMouseButton(me)) {
            int selectedNum;
            for (int i = 0; i < avatars.length; i++) {
                if (me.getSource() == avatarPanels[i]) {
                    prefs.setAvatar(avatars[i].getName());
                    SwingUtilities.windowForComponent(this).setVisible(false);
                    SwingUtilities.windowForComponent(this).dispose();
                }
            }
        }
    }
    public void mouseEntered(MouseEvent me) {
        // Invoked when the mouse enters a component.
    }
    public void    mouseExited(MouseEvent me) {
        // Invoked when the mouse exits a component.
    }
    public void    mousePressed(MouseEvent me) {
        // Invoked when a mouse button has been pressed on a component.
    }
    public void    mouseReleased(MouseEvent me) {
        // Invoked when a mouse button has been released on a component.
    }

    AvatarPanel(Avatar[] avs, Prefs p) {

        prefs = p;
        avatars = avs;

        avatarPanels = new JPanel[avatars.length];

        JPanel gridPanel = new JPanel(new GridLayout(4,4));

        for (int i = 0; i<avatars.length; i++) {
            avatarPanels[i] = new JPanel(new BorderLayout());
            avatarPanels[i].add(new JLabel(new ImageIcon(avatars[i].getImage())),
                    BorderLayout.CENTER);
            gridPanel.add(avatarPanels[i]);
            avatarPanels[i].addMouseListener(this);
            avatarPanels[i].setToolTipText(avatars[i].getName());
        }

        JPanel mainPanel = new JPanel(new FlowLayout());
        mainPanel.add(gridPanel);

        // for the future perhaps
        JScrollPane scrollPane = new JScrollPane(mainPanel);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

}

