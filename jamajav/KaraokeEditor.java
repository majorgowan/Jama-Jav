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

class KaraokeEditor extends JPanel implements ActionListener {

    final private int DEFAULT_WIDTH = 500;
    final private int DEFAULT_HEIGHT = 250;

    private TrackPanel trackPanel;
    private Karaoke karaoke;

    private JPanel linesPanel;
    private JButton addLineButton;
    private ArrayList<JTextField> timeField;
    private ArrayList<JTextField> lineField;
    private ArrayList<JPanel> lineLine;
    private ArrayList<TrackButton> removeButton;

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

    private void addLine() {
        lineLine.add(new JPanel());
        timeField.add(new JTextField(5));
        lineField.add(new JTextField(30));
        removeButton.add(makeButton("General","Remove24","Remove Note"));
        
        int newLineNum = lineLine.size()-1;
        lineLine.get(newLineNum).setLayout(
                new BoxLayout(lineLine.get(newLineNum),BoxLayout.LINE_AXIS));
        lineLine.get(newLineNum).add(timeField.get(newLineNum));
        lineLine.get(newLineNum).add(Box.createRigidArea(new Dimension(5,0)));
        lineLine.get(newLineNum).add(lineField.get(newLineNum));
        lineLine.get(newLineNum).add(Box.createRigidArea(new Dimension(15,0)));
        lineLine.get(newLineNum).add(removeButton.get(newLineNum));

        lineLine.get(newLineNum).setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        linesPanel.add(lineLine.get(newLineNum));

        linesPanel.revalidate();
        timeField.get(newLineNum).requestFocusInWindow();
    }

    private void swapLines (int i, int j) {
        // swap lineFields
        String temp = lineField.get(i).getText();
        lineField.get(i).setText(lineField.get(j).getText());
        lineField.get(j).setText(temp);
    }

    public void actionPerformed(ActionEvent ae) {
        //
        // use if instead of switch because of loop
        if (ae.getSource() == addLineButton) {
            addLine();
        }

        for (int i = 0; i < lineLine.size(); i++) {
            if (ae.getSource() == removeButton.get(i)) {
                linesPanel.remove(lineLine.get(i));
                timeField.remove(i);
                lineField.remove(i);
                removeButton.remove(i);
                lineLine.remove(i);
                linesPanel.revalidate();
                linesPanel.repaint();
            }
        }

        if (ae.getSource() == okButton) {
            karaoke.clear();
            for (int i = 0; i < lineLine.size(); i++) {
                double theTime = Double.parseDouble(timeField.get(i).getText());
                String theLine = lineField.get(i).getText();
                karaoke.addLine(theTime, theLine);
            }
            SwingUtilities.windowForComponent(this).setVisible(false);
            SwingUtilities.windowForComponent(this).dispose();
        } else if (ae.getSource() == cancelButton) {
            // exit without changing Info
            SwingUtilities.windowForComponent(this).setVisible(false);
            SwingUtilities.windowForComponent(this).dispose();
        }
    }

    KaraokeEditor(Karaoke ko, TrackPanel tpl) {

        karaoke = ko;
        trackPanel = tpl;

        timeField = new ArrayList<JTextField>(0);
        lineField = new ArrayList<JTextField>(0);
        lineLine = new ArrayList<JPanel>(0);
        removeButton = new ArrayList<TrackButton>(0);

        linesPanel = new JPanel();
        linesPanel.setLayout(new BoxLayout(linesPanel,BoxLayout.PAGE_AXIS));

        for (int i = 0; i < karaoke.getSize(); i++) {
            addLine();
            KaraokeLine kol = karaoke.getLine(i);
            timeField.get(i).setText("" + kol.getTime());
            lineField.get(i).setText(kol.getText());
        }

        JPanel outerNotesPanel = new JPanel(new FlowLayout());
        outerNotesPanel.add(linesPanel);
        JScrollPane scrollPane = new JScrollPane(outerNotesPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        addLineButton = new JButton("Add line");
        addLineButton.addActionListener(this);

        okButton = new JButton("Ok");
        okButton.addActionListener(this);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);

        buttonPanel.add(addLineButton);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        JPanel playTrackPanel = new JPanel();

        setLayout(new BorderLayout());
        add(playTrackPanel, BorderLayout.PAGE_START);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.PAGE_END);
    }

}

