import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Image {

    private List<List<Pixel>> pixels;

    public Image(List<List<Pixel>> pixels) {
        this.pixels = pixels;
    }

    public Pixel getPixel(int row, int col) {
        return null;
    }

    /**
     * get a list of all neighbours in the order [right, left, top, bot, upright, botright, topleft, botleft]
     */
    public List<Pixel> getNeighbours(int row, int col) {
        return null;
    }
    public List<Pixel> getNeighbours(Pixel p) {
        return getNeighbours(p.row, p.col);
    }

    public void show() {

    }
}
