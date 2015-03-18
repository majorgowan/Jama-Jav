package jamajav;

// Swing packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// For input/output
import java.io.*;

// For resizable arrays:
import java.util.ArrayList;

class AvatarPanel extends JPanel implements  MouseListener {

    private Prefs prefs;
    private ArrayList<Avatar> avatars;
    private ArrayList<JPanel> avatarPanels;

    public void mouseClicked(MouseEvent me) {
        if (SwingUtilities.isLeftMouseButton(me)) {
            int selectedNum;
            for (int i = 0; i < avatars.size(); i++) {
                if (me.getSource() == avatarPanels.get(i)) {
                    prefs.setAvatar(avatars.get(i).getName());
                    System.out.println(avatars.get(i).getName());
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

    AvatarPanel(ArrayList<Avatar> avs, Prefs p) {

        prefs = p;
        avatars = avs;

        avatarPanels = new ArrayList<JPanel>(0);

        JPanel gridPanel = new JPanel(new GridLayout(4,4));

        for (int i = 0; i<avatars.size(); i++) {
            avatarPanels.add(new JPanel(new BorderLayout()));
            avatarPanels.get(i).add(new JLabel(new ImageIcon(avatars.get(i).getImage())),
                    BorderLayout.CENTER);
            gridPanel.add(avatarPanels.get(i));
            avatarPanels.get(i).addMouseListener(this);
            avatarPanels.get(i).setToolTipText(avatars.get(i).getName());
        }

        JPanel mainPanel = new JPanel(new FlowLayout());
        mainPanel.add(gridPanel);

        // for the future perhaps
        JScrollPane scrollPane = new JScrollPane(mainPanel);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

}

