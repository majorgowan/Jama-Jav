package jamajav;

// Swing packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// For input/output
import java.io.*;

// For resizable arrays
import java.util.ArrayList;

class InfoPanel extends JPanel implements ActionListener {

    final private int DEFAULT_WIDTH = 500;
    final private int DEFAULT_HEIGHT = 200;

    private Info info;

    private JTextField titleField;

    private JPanel notesPanel;
    private JButton addNoteButton;
    private ArrayList<JTextField> noteField;
    private ArrayList<JButton> removeButton;
    private ArrayList<JPanel> noteLine;

    private JButton okButton;
    private JButton cancelButton;

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public void actionPerformed(ActionEvent ae) {
        //
        // use if instead of switch because of loop
        if (ae.getSource() == addNoteButton) {
            noteLine.add(new JPanel());
            noteField.add(new JTextField(30));
            removeButton.add(new JButton("Remove"));
            int newNoteNum = noteLine.size()-1;
            noteLine.get(newNoteNum).add(removeButton.get(newNoteNum));
            noteLine.get(newNoteNum).add(noteField.get(newNoteNum));

            notesPanel.add(noteLine.get(newNoteNum));

            notesPanel.revalidate();
        }

        for (int i = 0; i < noteLine.size(); i++) {
            if (ae.getSource() == removeButton.get(i)) {
                notesPanel.remove(noteLine.get(i));
                noteField.remove(i);
                removeButton.remove(i);
                noteLine.remove(i);
                notesPanel.revalidate();
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
        removeButton = new ArrayList<JButton>(0);
        noteLine = new ArrayList<JPanel>(0);

        JPanel titlePanel = new JPanel();
        titlePanel.add(new JLabel("Title: "));
        titleField = new JTextField(info.getTitle(),20);
        titlePanel.add(titleField);

        notesPanel = new JPanel();
        notesPanel.setLayout(new BoxLayout(notesPanel,BoxLayout.PAGE_AXIS));

        for (int i = 0; i < info.getNotesSize(); i++) {
            noteLine.add(new JPanel());
            removeButton.add(new JButton("Remove"));
            noteField.add(new JTextField(info.getNote(i),30));
            noteLine.get(i).add(removeButton.get(i));
            noteLine.get(i).add(noteField.get(i));

            notesPanel.add(noteLine.get(i));
        }

        JScrollPane scrollPane = new JScrollPane(notesPanel);

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
    }

}

