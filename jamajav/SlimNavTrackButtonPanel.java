package jamajav;

// Swing packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.net.URL;

class SlimNavTrackButtonPanel extends TrackButtonPanel {

    SlimNavTrackButtonPanel(Track trck) {
        super(trck);

        // Buttons ...
        TrackButton expandButton = makeButton(
                "Navigation", "Expand24", "expand",
                "Expand", "Exp");

        JPanel navPanel = new JPanel();
        navPanel.add(expandButton);

        setLayout(new FlowLayout(FlowLayout.RIGHT));
        add(navPanel);
    }
}

