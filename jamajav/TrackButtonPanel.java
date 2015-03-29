package jamajav;

// Swing packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.net.URL;

class TrackButtonPanel extends JPanel {

    protected Track track;    // the listener for buttons

    protected TrackButton makeButton(String buttonType, String imageName, 
            String actionCommand, String toolTipText, String altText) {

        String imgLocation = "/Icons/Toolbar/" + buttonType + "/" + imageName + ".gif";
        URL imageURL = ToolBar.class.getResource(imgLocation);

        TrackButton button = new TrackButton();
        button.setActionCommand(actionCommand);
        button.setToolTipText(toolTipText);
        button.addActionListener(track);

        if (imageURL != null) {          
            button.setIcon(new ImageIcon(imageURL, altText));
        } else {                        
            button.setText(altText);
            System.err.println("Resource not found: "
                    + imgLocation);
        }

        return button;
    }

    TrackButtonPanel(Track trck) {
        track = trck;
    }
}

