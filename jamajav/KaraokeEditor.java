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
    final private int DEFAULT_HEIGHT = 400;

    private TrackData trackData, oldTrackData;
    private TimeKeeper timeKeeper;
    private PlainClock clock;
    private JTextField playTimeField;

    private String lastTime = "0.0";

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
            } else if (ae.getSource() == lineField.get(i)) {
                addLine();
            }
        }

        if (ae.getSource() == okButton) {
            if (!trackData.getStopPlay().getValue()) {
                trackData.stopPlaying();
                waitASecond(500);
            }
            System.out.println("Ok then!  Adding " + lineLine.size() + " lines!");
            karaoke.clear();
            for (int i = 0; i < lineLine.size(); i++) {
                double theTime = Double.parseDouble(timeField.get(i).getText());
                String theLine = lineField.get(i).getText();
                karaoke.addLine(theTime, theLine);
            }
            trackPanel.refreshKaraokePanel();

            SwingUtilities.windowForComponent(this).setVisible(false);
            SwingUtilities.windowForComponent(this).dispose();
        } else if (ae.getSource() == cancelButton) {
            // exit without changing Info
            SwingUtilities.windowForComponent(this).setVisible(false);
            SwingUtilities.windowForComponent(this).dispose();
        }

        String cmdStr = ae.getActionCommand();
        if (cmdStr.equals("playall")) {
            if (!trackData.getStopPlay().getValue()) {
                trackData.stopPlaying();
                waitASecond(1000);
                clock.reset(0.0);
            }
            trackData.playback(
                    Double.parseDouble(playTimeField.getText()), trackData.getRunningTime());
        } else if (cmdStr.equals("pause")) {
            trackData.togglePause();
        }
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
        final int newLineNum = lineLine.size()-1;

        timeField.add(new JTextField(5));
        lineField.add(new JTextField(30));
        lineField.get(newLineNum).addActionListener(this);
        removeButton.add(makeButton("General","Remove24","Remove Note"));


        if (newLineNum == 0)
            lastTime = "0.0";
        else
            lastTime = timeField.get(newLineNum-1).getText();
        final String timeDefault = lastTime;

        timeField.get(newLineNum).addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                try {
                    double s = Double.parseDouble(
                        timeField.get(newLineNum).getText());
                    if (s < 0) {
                        throw new TimeOutOfRangeException("low");
                    } else if (s > trackData.getRunningTime()) {
                        throw new TimeOutOfRangeException("high");
                    }
                } catch (NumberFormatException nfe) {
                    timeField.get(newLineNum).setText("" + timeDefault);
                } catch (TimeOutOfRangeException toore) {
                    if (toore.getHighLow().equals("high")) {
                        timeField.get(newLineNum).setText("" + timeDefault);
                    } else {
                        timeField.get(newLineNum).setText("0.0");
                    }
                }
            }
        });

        lineLine.get(newLineNum).setLayout(
                new BoxLayout(lineLine.get(newLineNum),BoxLayout.LINE_AXIS));
        lineLine.get(newLineNum).add(timeField.get(newLineNum));
        lineLine.get(newLineNum).add(Box.createRigidArea(new Dimension(5,0)));
        lineLine.get(newLineNum).add(lineField.get(newLineNum));
        lineLine.get(newLineNum).add(Box.createRigidArea(new Dimension(15,0)));
        lineLine.get(newLineNum).add(removeButton.get(newLineNum));

        lineLine.get(newLineNum).setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        linesPanel.add(lineLine.get(newLineNum));

        refreshLinesPanel();
        linesPanel.revalidate();

        timeField.get(newLineNum).requestFocusInWindow();
        timeField.get(newLineNum).selectAll();
        linesPanel.scrollRectToVisible(
                new Rectangle(
                    0,(int)linesPanel.getPreferredSize().getHeight(),10,10));
    }

    private void swapLines (int i, int j) {
        // swap lineFields
        String temp = lineField.get(i).getText();
        lineField.get(i).setText(lineField.get(j).getText());
        lineField.get(j).setText(temp);
    }

    private void waitASecond(int milliseconds) {
        try {
            Thread.sleep(1000);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private void refreshLinesPanel() {
        linesPanel.removeAll();
        if (lineLine.size() > 0) {
            JPanel instructionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            instructionPanel.add(new JLabel("Enter times and lyrics for Karaoke!"));
            linesPanel.add(instructionPanel);
        }
        for (int i = 0; i < lineLine.size(); i++) {
            linesPanel.add(lineLine.get(i));
        }
    }

    KaraokeEditor(Karaoke ko, TrackPanel tpl) {

        karaoke = ko;

        trackPanel = tpl;

        trackData = trackPanel.combineAll();

        // time keeping and display
        timeKeeper = new TimeKeeper(0.0);
        clock = new PlainClock();
        timeKeeper.setClock(clock);
        trackData.setTimeKeeper(timeKeeper);

        // karaoke lines panel
        timeField = new ArrayList<JTextField>(0);
        lineField = new ArrayList<JTextField>(0);
        lineLine = new ArrayList<JPanel>(0);
        removeButton = new ArrayList<TrackButton>(0);

        linesPanel = new JPanel();
        linesPanel.setLayout(new BoxLayout(linesPanel,BoxLayout.PAGE_AXIS));

        if (karaoke.getSize() > 0) {
            System.out.println("Non-trivial karaoke found!");

            JPanel instructionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            instructionPanel.add(new JLabel("Enter times and lyrics for Karaoke!"));
            linesPanel.add(instructionPanel);

            for (int i = 0; i < karaoke.getSize(); i++) {
                addLine();
                KaraokeLine kol = karaoke.getLine(i);
                timeField.get(i).setText("" + kol.getTime());
                lineField.get(i).setText(kol.getText());
            }
        } else
            System.out.println("Making new karaoke!");

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

        // playback panel:
        JPanel playTrackPanel = new JPanel();
        //playTrackPanel.setLayout(new BoxLayout(playTrackPanel,BoxLayout.LINE_AXIS));

        JLabel playTimeLabel = new JLabel("Start play from: ");
        playTimeField = new JTextField("0.0", 4);
        playTimeField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                try {
                    double s = Double.parseDouble(playTimeField.getText());
                    if (s < 0) {
                        throw new TimeOutOfRangeException("low");
                    } else if (s > trackData.getRunningTime()) {
                        throw new TimeOutOfRangeException("high");
                    }
                } catch (Exception ex) {
                    playTimeField.setText("0.0");
                }
            }
        });

        JPanel outerPlayTimeFieldPanel = new JPanel();
        outerPlayTimeFieldPanel.add(playTimeLabel);
        outerPlayTimeFieldPanel.add(playTimeField);

        JButton playButton = new JButton();
        URL imageURL = Track.class.getResource(
                "/Icons/Toolbar/Media/PlayFromTop24.gif");
        playButton.setIcon(new ImageIcon(imageURL));
        playButton.setToolTipText("(Re)start Jam");
        playButton.setActionCommand("playall");
        playButton.addActionListener(this);

        JButton pauseButton = new JButton();
        imageURL = Track.class.getResource(
                "/Icons/Toolbar/Media/Pause24.gif");
        pauseButton.setIcon(new ImageIcon(imageURL));
        pauseButton.setToolTipText("Pause/Resume Playback");
        pauseButton.setActionCommand("pause");
        pauseButton.addActionListener(this);

        JPanel outerClockPanel = new JPanel();
        outerClockPanel.add(clock);

        //playTrackPanel.add(Box.createHorizontalGlue());
        playTrackPanel.add(outerPlayTimeFieldPanel);
        //playTrackPanel.add(Box.createRigidArea(new Dimension(20,0)));
        playTrackPanel.add(playButton);
        playTrackPanel.add(pauseButton);
        //playTrackPanel.add(Box.createRigidArea(new Dimension(20,0)));
        playTrackPanel.add(outerClockPanel);

        setLayout(new BorderLayout());
        add(playTrackPanel, BorderLayout.PAGE_START);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.PAGE_END);
    }

}

