package jamajav;

// For image
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.net.URL;

class Avatar {

    private String name;
    private BufferedImage image;

    public BufferedImage getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    Avatar(String nme) {
        name = nme;

        try {
            //ClassLoader cl = this.getClass().getClassLoader();
            URL url = Avatar.class.getResource("/Images/Avatars/" + name + ".png");
            image = ImageIO.read(url);
        } catch (IOException e) {
            System.out.println("Logo image not found!");
        }
    }
}


