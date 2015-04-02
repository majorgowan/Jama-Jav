package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;

public class MainScrollPane extends JScrollPane {

    final private int DEFAULT_WIDTH = 600;
    final private int DEFAULT_HEIGHT = 340;

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    MainScrollPane(Component view) {
        super(view);
    }
}
