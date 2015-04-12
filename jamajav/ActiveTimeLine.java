package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// for formatting numbers:
import java.text.DecimalFormat;

class ActiveTimeLine extends PlainTimeLine {

    private int DEFAULT_WIDTH = 300;
    private int DEFAULT_HEIGHT = 65;

    protected double rad;
    protected int lineHeight; 
    protected int barHeight; 
    protected int barThickness; 
    protected int tickLength;

    protected Color darkGoldColour = JamaJav.darkGoldColour;
    protected Color startColour = new Color(0.0f,0.8f,0.1f);
    protected Color stopColour = new Color(0.8f,0.0f,0.1f);

    protected Font tickFont = new Font("SansSerif",Font.PLAIN,10);
    protected Font endsFont = new Font("SansSerif",Font.BOLD,11);

    protected DecimalFormat df = new DecimalFormat("##0.0");

    protected double minTime, maxTime;
    protected double factor = 0.0;

    protected boolean leftOvalGrabbed, rightOvalGrabbed;

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    protected void setParams() {
        rad = 3.5;
        lineHeight = 20; 
        barHeight = 29; 
        barThickness = 10; 
        tickLength = 10;

        tickFont = new Font("SansSerif",Font.PLAIN,10);
        endsFont = new Font("SansSerif",Font.BOLD,11);
    }

    public double getMinTime() {
        return minTime;
    }

    public double getMaxTime() {
        return maxTime;
    }

    public void rehash() {
        maxTime = Math.min(maxTime, runningTime);
        if (maxTime == 0)
            maxTime = runningTime;
    }

    public void setFull() {
        minTime = 0.0;
        maxTime = runningTime;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (runningTime > 0) {
            // normalize:
            factor = (double)getWidth()/(double)runningTime;

            // draw a horizontal line at height lineHeight
            g.setColor(Color.BLUE);
            g.drawLine(0, lineHeight,
                    getWidth(), lineHeight);

            // draw a vertical line and a label at tickInterval intervals
            g.setFont(tickFont);
            for (int i = 0; i <= runningTime/tickInterval; i++) {
                g.drawLine((int)(factor*i*tickInterval), lineHeight-tickLength/2,
                        (int)(factor*i*tickInterval), lineHeight+tickLength/2);
                if (i%2 == 0) {
                    if (tickInterval < 1.0)
                        g.drawString("" + i*tickInterval,(int)(factor*i*tickInterval)+1, lineHeight-6);
                    else
                        g.drawString("" + (int)(i*tickInterval),(int)(factor*i*tickInterval)+1, lineHeight-6);
                }
            }
            // draw runner
            g.fillOval((int)(factor*runnerPosition-rad), 
                    (int)(lineHeight-rad), 
                    (int)(2*rad), (int)(2*rad));

            FontMetrics fontMetrics = g.getFontMetrics(g.getFont());

            // draw active region:
            g.setColor(darkGoldColour);
            g.fillRect((int)(factor*minTime)+1, barHeight, (int)(factor*(maxTime-minTime)-2), barThickness);
            // draw handles:
            g.setColor(startColour);
            g.fillOval((int)(factor*minTime), 
                    (int)(barHeight+0.5*barThickness-2*rad),
                    (int)(3*rad), (int)(4*rad));
            g.setColor(darkGoldColour);
            g.drawOval((int)(factor*minTime), 
                    (int)(barHeight+0.5*barThickness-2*rad),
                    (int)(3*rad), (int)(4*rad));
            g.setColor(stopColour);
            g.fillOval((int)(factor*maxTime-3*rad), 
                    (int)(barHeight+0.5*barThickness-2*rad),
                    (int)(3*rad), (int)(4*rad));
            g.setColor(darkGoldColour);
            g.drawOval((int)(factor*maxTime-3*rad), 
                    (int)(barHeight+0.5*barThickness-2*rad),
                    (int)(3*rad), (int)(4*rad));

            g.setColor(Color.BLACK);

            g.setFont(endsFont);
            String s = "" + df.format(minTime);
            g.drawString(s, 
                    (int)(factor*minTime+1), 
                    (int)(barHeight+barThickness + fontMetrics.getHeight()));
            s = "" + df.format(maxTime);
            g.drawString(s, 
                    (int)(factor*maxTime) - fontMetrics.stringWidth(s)-1, 
                    (int)(barHeight+barThickness + fontMetrics.getHeight()));
        }
    }

    public void setRunningTime(double t) {
        super.setRunningTime(t);
        if (runningTime < 10)
            tickInterval = 1;
        else
            tickInterval = (double)((int)(5*((int)(runningTime/100)+1)));
    }

    ActiveTimeLine() {
        super();

        minTime = 0.0;
        maxTime = runningTime;

        // set geometric parameters
        setParams();

        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                // System.out.println("SOMEBODY CLICKED SOMETHING!!!!");

                int x = me.getX();
                int y = me.getY();

                int leftOvalLeft = (int)(factor*minTime);
                int leftOvalRight = (int)(factor*minTime+3*rad);
                int rightOvalLeft = (int)(factor*maxTime-3*rad);
                int rightOvalRight = (int)(factor*maxTime);

                int ovalTop = (int)(barHeight+0.5*barThickness - 2*rad);
                int ovalBottom = (int)(barHeight+0.5*barThickness + 2*rad);

                // if grabbed the left oval:
                if ((x > leftOvalLeft) && (x < leftOvalRight) 
                    && (y > ovalTop) && (y < ovalBottom))
                {
                    leftOvalGrabbed = true;
                    // System.out.println("GRABBED LEFT!!!");
                }
                // else if grabbed the right oval:
                else if ((x > rightOvalLeft) && (x < rightOvalRight) 
                        && (y > ovalTop) && (y < ovalBottom)) 
                {
                    rightOvalGrabbed = true;
                    // System.out.println("GRABBED RIGHT!!!");
                }
            }

            public void mouseReleased(MouseEvent me) {
                // System.out.println("SOMEBODY RELEASED SOMETHING!!!");
                leftOvalGrabbed = false;
                rightOvalGrabbed = false;
            }
        });

        this.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent me) {
                // System.out.println("SOMEBODY DRAGGED SOMETHING!!!");
                int x = me.getX();
                if ((x >= (int)(3*rad)) && (x <= (int)(factor*runningTime-3*rad))) { 
                    if (leftOvalGrabbed) {
                        minTime = Math.max(0.0,
                            Math.min(((double)(x-3*rad)/factor), maxTime));
                    } else if (rightOvalGrabbed) {
                        maxTime = Math.min(runningTime, 
                            Math.max(minTime, ((double)(x+3*rad)/factor)));
                    }
                    repaint();
                }
            }
        });
    }
}


