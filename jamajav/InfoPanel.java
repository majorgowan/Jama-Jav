package jamajav;

// Swing packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// For input/output
import java.io.*;

// For resizable arrays
import java.util.ArrayList;

// for icon images
import java.awt.image.*;
import java.net.URL;

class InfoPanel extends JPanel implements ActionListener {

    final private int DEFAULT_WIDTH = 500;
    final private int DEFAULT_HEIGHT = 250;

    private Info info;

    private JTextField titleField;

    private JPanel notesPanel;
    private JButton addNoteButton;
    private ArrayList<JTextField> noteField;
    private ArrayList<JPanel> noteLine;
    private ArrayList<TrackButton> removeButton;
    private ArrayList<TrackButton> upButton;
    private ArrayList<TrackButton> downButton;

    private JButton okButton;
    private JButton cancelButton;

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    // copied from TrackButtonPanel (hence strange nomenclature!)
    private TrackButton makeButton(String buttonType, String imageName, 
            String toolTipText) {

        String imgLocation = "/Icons/Toolbar/" + buttonType + "/" + imageName + ".gif";
        URL imageURL = ToolBar.class.getResource(imgLocation);

        TrackButton button = new TrackButton();
        button.setToolTipText(toolTipText);
        button.addActionListener(this);

        button.setIcon(new ImageIcon(imageURL));

        return button;
    }

    private void addNote() {
        noteLine.add(new JPanel());
        noteField.add(new JTextField(30));
        removeButton.add(makeButton("General","Remove24","Remove Note"));
        upButton.add(makeButton("Navigation","Up24","Move Note Up"));
        downButton.add(makeButton("Navigation","Down24","Move Note Down"));
        int newNoteNum = noteLine.size()-1;
        noteLine.get(newNoteNum).setLayout(
                new BoxLayout(noteLine.get(newNoteNum),BoxLayout.LINE_AXIS));
        noteLine.get(newNoteNum).add(noteField.get(newNoteNum));
        noteLine.get(newNoteNum).add(Box.createRigidArea(new Dimension(8,0)));
        noteLine.get(newNoteNum).add(upButton.get(newNoteNum));
        noteLine.get(newNoteNum).add(Box.createRigidArea(new Dimension(2,0)));
        noteLine.get(newNoteNum).add(downButton.get(newNoteNum));
        noteLine.get(newNoteNum).add(Box.createRigidArea(new Dimension(15,0)));
        noteLine.get(newNoteNum).add(removeButton.get(newNoteNum));

        noteLine.get(newNoteNum).setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        notesPanel.add(noteLine.get(newNoteNum));

        notesPanel.revalidate();
        noteField.get(newNoteNum).requestFocusInWindow();
        notesPanel.scrollRectToVisible(
                new Rectangle(
                    0,(int)notesPanel.getPreferredSize().getHeight(),10,10));
    }

    private void swapNotes (int i, int j) {
        // swap noteFields
        String temp = noteField.get(i).getText();
        noteField.get(i).setText(noteField.get(j).getText());
        noteField.get(j).setText(temp);
    }

    public void actionPerformed(ActionEvent ae) {
        //
        // use if instead of switch because of loop
        if (ae.getSource() == addNoteButton) {
            addNote();
        }

        for (int i = 0; i < noteLine.size(); i++) {
            if (ae.getSource() == removeButton.get(i)) {
                notesPanel.remove(noteLine.get(i));
                noteField.remove(i);
                removeButton.remove(i);
                upButton.remove(i);
                downButton.remove(i);
                noteLine.remove(i);
                notesPanel.revalidate();
                notesPanel.repaint();
            } else if (ae.getSource() == upButton.get(i)) {
                if (i > 0)
                    swapNotes(i,i-1);
            } else if (ae.getSource() == downButton.get(i)) {
                if (i < noteLine.size()-1)
                    swapNotes(i,i+1);
            }
        }

        if (ae.getSource() == okButton) {
            info.setTitle(titleField.getText());
            info.clearNotes();
            for (int i = 0; i < noteLine.size(); i++)
                info.addNote(noteField.get(i).getText());

            SwingUtilities.windowForComponent(this).setVisible(false);
            SwingUtilities.windowForComponent(this).dispose();
        } else if (ae.getSource() == cancelButton) {
            // exit without changing Info
            SwingUtilities.windowForComponent(this).setVisible(false);
            SwingUtilities.windowForComponent(this).dispose();
        }
    }

    InfoPanel(Info inf) {

        info = inf;

        noteField = new ArrayList<JTextField>(0);
        noteLine = new ArrayList<JPanel>(0);
        removeButton = new ArrayList<TrackButton>(0);
        upButton = new ArrayList<TrackButton>(0);
        downButton = new ArrayList<TrackButton>(0);

        JPanel titlePanel = new JPanel();
        titlePanel.add(new JLabel("Title: "));
        titleField = new JTextField(info.getTitle(),20);
        titlePanel.add(titleField);

        notesPanel = new JPanel();
        notesPanel.setLayout(new BoxLayout(notesPanel,BoxLayout.PAGE_AXIS));

        for (int i = 0; i < info.getNotesSize(); i++) {
            addNote();
            noteField.get(i).setText(info.getNote(i));
        }

        JPanel outerNotesPanel = new JPanel(new FlowLayout());
        outerNotesPanel.add(notesPanel);
        JScrollPane scrollPane = new JScrollPane(outerNotesPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        addNoteButton = new JButton("Add note");
        addNoteButton.addActionListener(this);

        okButton = new JButton("Ok");
        okButton.addActionListener(this);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);

        buttonPanel.add(addNoteButton);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        setLayout(new BorderLayout());
        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        titleField.requestFocusInWindow();
        titleField.selectAll();
    }

}

