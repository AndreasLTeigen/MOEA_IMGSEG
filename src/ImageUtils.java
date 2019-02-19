import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtils {

    public static Image LoadImage(String filename) {
        BufferedImage buffImg;

        try {
            buffImg = ImageIO.read(new File(filename));
        } catch (IOException e) {
        }

        return null;
    }

    public static void drawImage(Image img) {

    }
}
