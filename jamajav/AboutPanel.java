package jamajav;

// Swing packages
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

// For reading files
import java.io.*;
import javax.imageio.*;
import java.net.URL;

class AboutPanel extends JPanel {

    AboutPanel() {

        JPanel logoPanel = new JPanel(new FlowLayout());
        logoPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        // add logo to app
        try {
            URL logoURL = AboutPanel.class.getResource("/Images/logo.png");
            BufferedImage logoImage = ImageIO.read(logoURL);
            JLabel logoLabel = new JLabel(new ImageIcon(logoImage));
            logoPanel.add(logoLabel);
        } catch (IOException e) {
            System.out.println("Logo image not found!");
        }

        setLayout(new BorderLayout());
        add(logoPanel, BorderLayout.CENTER);

        JLabel aboutText = new JLabel("<html><p align=center>"
                + "<br>Copyright (c) 2015<br>"
                + "by Mark D. Fruman<br><br>"
                );

        JPanel outerLabelPanel = new JPanel(new FlowLayout());
        outerLabelPanel.add(aboutText);

        add(outerLabelPanel,BorderLayout.PAGE_END);
    }

}

