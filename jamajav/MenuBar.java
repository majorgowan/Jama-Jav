package jamajav;

// Swing packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

class MenuBar extends JMenuBar {

    TrackPanel trackPanel;    // the listener for buttons

    MenuBar(TrackPanel tpnl) {

        trackPanel = tpnl;

        JMenu fileMenu = new JMenu("File");
        JMenu playMenu = new JMenu("Play");
        JMenu helpMenu = new JMenu("Help");

        // File menu
        fileMenu.setMnemonic('F');
        JMenuItem fileNewItem = new JMenuItem("New Jam");
        fileNewItem.setActionCommand("newjam");
        fileNewItem.addActionListener(trackPanel);
        JMenuItem fileOpenItem = new JMenuItem("Open ...");
        fileOpenItem.setActionCommand("open");
        fileOpenItem.addActionListener(trackPanel);
        JMenuItem fileSaveItem = new JMenuItem("Save as ...");
        fileSaveItem.setActionCommand("save");
        fileSaveItem.addActionListener(trackPanel);
        JMenuItem fileWebOpenItem = new JMenuItem("Open from Web ...");
        fileWebOpenItem.setActionCommand("WWW");
        fileWebOpenItem.addActionListener(trackPanel);
        JMenuItem fileEditPrefsItem = new JMenuItem("Edit preferences ...");
        fileEditPrefsItem.setActionCommand("editprefs");
        fileEditPrefsItem.addActionListener(trackPanel);
        JMenuItem fileExitItem = new JMenuItem("Exit");
        fileExitItem.setActionCommand("exit");
        fileExitItem.addActionListener(trackPanel);
        fileMenu.add(fileNewItem);
        fileMenu.add(fileOpenItem);
        fileMenu.add(fileSaveItem);
        fileMenu.addSeparator();
        fileMenu.add(fileWebOpenItem);
        fileMenu.addSeparator();
        fileMenu.add(fileEditPrefsItem);
        fileMenu.addSeparator();
        fileMenu.add(fileExitItem);

        // Play menu
        playMenu.setMnemonic('P');
        JMenuItem playAllItem = new JMenuItem("Play all");
        playAllItem.setActionCommand("playall");
        playAllItem.addActionListener(trackPanel);
        playMenu.add(playAllItem);

        // Help menu
        helpMenu.setMnemonic('H');
        JMenuItem helpInstItem = new JMenuItem("Instructions");
        helpInstItem.addActionListener(trackPanel);
        JMenuItem helpAboutItem = new JMenuItem("About Jama Jav");
        helpAboutItem.setActionCommand("About");
        helpAboutItem.addActionListener(trackPanel);
        helpMenu.add(helpInstItem);
        helpMenu.addSeparator();
        helpMenu.add(helpAboutItem);

        add(fileMenu);
        add(playMenu);
        add(helpMenu);
    }
}

