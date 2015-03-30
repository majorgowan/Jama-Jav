package jamajav;

// Swing packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.net.URL;

class NavTrackButtonPanel extends TrackButtonPanel {

    NavTrackButtonPanel(Track trck) {
        super(trck);

        // Buttons ...
        TrackButton cancelButton = makeButton(
                "General", "Remove24", "remove", 
                "Remove Track", "Remove");
        TrackButton moveUpButton = makeButton(
                "Navigation", "Up24", "moveup", 
                "Move Up", "Up");
        TrackButton moveDownButton = makeButton(
                "Navigation", "Down24", "movedown", 
                "Move Down", "Down");

        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel,BoxLayout.PAGE_AXIS));
        navPanel.add(cancelButton);
        navPanel.add(Box.createRigidArea(new Dimension(0,40)));
        navPanel.add(moveUpButton);
        navPanel.add(moveDownButton);

        setLayout(new FlowLayout());
        add(navPanel);
    }
}

