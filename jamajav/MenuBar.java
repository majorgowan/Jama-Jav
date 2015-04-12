package jamajav;

// Swing packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

class MenuBar extends JMenuBar {

    TrackPanel trackPanel;    // the listener for buttons

    private JMenuItem makeItem(JMenu menu, String string, 
            String actionCommand, char mnemonic) {

        JMenuItem menuItem = new JMenuItem(string);
        menuItem.setActionCommand(actionCommand);
        menuItem.addActionListener(trackPanel);
        menuItem.setMnemonic(mnemonic);
        menu.add(menuItem);

        return menuItem;
    }

    MenuBar(TrackPanel tpnl) {

        trackPanel = tpnl;

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic('E');
        JMenu createMenu = new JMenu("Create");
        createMenu.setMnemonic('C');
        JMenu playMenu = new JMenu("Play");
        playMenu.setMnemonic('P');
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');

        // File menu
        JMenuItem fileNewItem = makeItem(fileMenu, "New Jam", "newjam", 'J');
        JMenuItem fileOpenItem = makeItem(fileMenu, "Open Jam ...", "open", 'O');
        JMenuItem fileMergeItem = makeItem(fileMenu, "Import tracks ...", "merge", 'M');
        JMenuItem fileSaveItem = makeItem(fileMenu, "Save Jam as ...", "save", 'S');
        fileMenu.addSeparator();
        JMenuItem fileWebOpenItem = makeItem(fileMenu, "Open Jam from Web ...", "web", 'W');
        fileMenu.addSeparator();
        JMenuItem fileEditPrefsItem = makeItem(fileMenu,"Edit preferences ...", "editprefs", 'P');
        fileMenu.addSeparator();
        JMenuItem fileExitItem = makeItem(fileMenu, "Exit", "exit", 'X');

        // Edit menu
        JMenuItem editSelectAllItem = makeItem(editMenu, "Select all", "selectall", 'A');
        JMenuItem editRemoveSelectedItem = makeItem(editMenu, 
                "Remove selected tracks", "removeselected", 'R');
        JMenuItem editMoveSelectedUpItem = makeItem(editMenu, 
                "Move selected tracks up", "moveselectedup", 'U');
        JMenuItem editMoveSelectedDownItem = makeItem(editMenu, 
                "Move selected tracks down", "moveselecteddown", 'D');
        JMenuItem editConcatenateItem = makeItem(editMenu, 
                "Concatenate selected tracks", "concatenateselected", 'C');
        JMenuItem editCombineItem = makeItem(editMenu, 
                "Combine selected tracks", "combineselected", 'B');
        editMenu.addSeparator();
        JMenuItem editKaraokeItem = makeItem(editMenu,
                "Edit Karaoke file", "editkaraoke", 'O');
        editMenu.addSeparator();
        JMenuItem editExportItem = makeItem(editMenu, 
                "Combine and export selected tracks to WAV file", "exportselected", 'E');

        // Create menu
        JMenuItem newTrackItem = makeItem(createMenu, "Add new track", "addnewtrack", 'N');
        JMenuItem playPlayRecordItem = makeItem(createMenu, 
                "Play selected tracks and record new", "playrecord", 'R');
        JMenuItem newDrumTrackItem = makeItem(createMenu, "Add drums track", "adddrumtrack", 'D');

        // Play menu
        JMenuItem playAllItem = makeItem(playMenu, 
                "Play interval from all tracks", "playall", 'I');
        JMenuItem playAllFromTopItem = makeItem(playMenu, 
                "Play all tracks from the top", "playallfromtop", 'A');
        playMenu.addSeparator();
        JMenuItem playPlaySelectedFromTopItem = makeItem(playMenu, 
                "Play selected from the top", "playselectedfromtop", 'T');
        JMenuItem playPlaySelectedItem = makeItem(playMenu, "Play selected", "playselected", 'P');
        JMenuItem playPauseItem = makeItem(playMenu, "Pause", "pause", 'E');
        JMenuItem playStopItem = makeItem(playMenu, "Stop", "stop", 'S');

        // View menu
        JMenuItem toggleChatItem = makeItem(viewMenu,
                "Chat window", "chat", 'C');
        JMenuItem toggleMetronomeItem = makeItem(viewMenu,
                "Metronome", "showmetronome", 'M');
        JMenuItem toggleKaraokeItem = makeItem(viewMenu,
                "Karaoke Panel", "karaoke", 'K');

        // Help menu
        JMenuItem helpInstItem = makeItem(helpMenu,"Instructions","instructions", 'I');
        helpMenu.addSeparator();
        JMenuItem helpAboutItem = makeItem(helpMenu,"About","about", 'A');

        add(fileMenu);
        add(editMenu);
        add(createMenu);
        add(playMenu);
        add(viewMenu);
        add(helpMenu);
    }
}

