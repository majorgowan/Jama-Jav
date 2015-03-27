package jamajav;

// Swing packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.net.URL;

class ToolBar extends JToolBar {

    private TrackPanel trackPanel;    // the listener for buttons

    private JButton allStopButton;    // so it can request focus
    
    public void focusOnStop() {
        allStopButton.requestFocusInWindow();
    }
    
    private JButton makeButton(String buttonType, String imageName, 
            String actionCommand, String toolTipText, String altText) {

        String imgLocation = "/Icons/Toolbar/" + buttonType + "/" + imageName + ".gif";
        URL imageURL = ToolBar.class.getResource(imgLocation);

        JButton button = new JButton();
        button.setActionCommand(actionCommand);
        button.setToolTipText(toolTipText);
        button.addActionListener(trackPanel);

        if (imageURL != null) {          
            button.setIcon(new ImageIcon(imageURL, altText));
        } else {                        
            button.setText(altText);
            System.err.println("Resource not found: "
                    + imgLocation);
        }

        return button;
    }

    ToolBar(TrackPanel tpnl) {

        trackPanel = tpnl;
        trackPanel.setToolBar(this);

        // Buttons ...
        JButton newJamButton = makeButton(
                "General", "NewJam24", "newjam", "New Jam", "New"); 
        JButton openButton = makeButton(
                "General", "Open24", "open", "Open Jam", "Open"); 
        JButton mergeButton = makeButton(
                "General", "Merge24", "merge", "Import tracks", "Merge"); 
        JButton saveButton = makeButton(
                "General", "Save24", "save", "Save Jam", "Save"); 

        JButton newTrackButton = makeButton(
                "General", "AddTrack24", "addnewtrack", "Add Track", "Add Track");

        JButton playAllButton = makeButton(
                "Media", "PlayAll24", "playall", "Play All", "Play All");
        JButton playSelectedButton = makeButton(
                "Media", "Play24", "playselected", "Play Selected", "Play Selected");
        JButton pauseButton = makeButton(
                "Media", "Pause24", "pause", "Pause", "Pause");
        allStopButton = makeButton(
                "Media", "Stop24", "allstop", "All Stop", "All Stop");
        JButton playRecordButton = makeButton(
                "Media", "PlayRecord24", "playrecord", 
                "Play Selected and Record New Track", "Play+Record");

        JButton selectAllButton = makeButton(
                "General", "SelectAll24", "selectall", "Toggle Select All", "Select All");

        JButton removeSelectedButton = makeButton(
                "General", "Remove24", "removeselected", 
                "Remove Selected", "Remove Selected");

        JButton moveSelectedUpButton = makeButton(
                "Navigation", "UpSelected24", "moveselectedup", 
                "Move Selected Up", "Move Up");
        JButton moveSelectedDownButton = makeButton(
                "Navigation", "DownSelected24", "moveselecteddown", 
                "Move Selected Down", "Move Down");

        JButton concatenateSelectedButton = makeButton(
                "Media", "Concatenate24", "concatenateselected", 
                "Concatenate Selected", "Concatenate Selected");
        JButton combineSelectedButton = makeButton(
                "Media", "Combine24", "combineselected", 
                "Combine Selected", "Combine Selected");

        JButton exportSelectedButton = makeButton(
                "General", "Export24", "exportselected", 
                "Combine and Export Selected to WAVE file", "Export Selected");

        add(newJamButton);
        add(openButton);
        add(mergeButton);
        add(saveButton);
        addSeparator();
        add(newTrackButton);
        addSeparator();
        add(playAllButton);
        add(playSelectedButton);
        add(pauseButton);
        add(allStopButton);
        add(playRecordButton);
        addSeparator();
        add(selectAllButton);
        addSeparator();
        add(removeSelectedButton);
        addSeparator();
        add(moveSelectedUpButton);
        add(moveSelectedDownButton);
        addSeparator();
        add(concatenateSelectedButton);
        add(combineSelectedButton);
        addSeparator();
        add(exportSelectedButton);

    }
}

