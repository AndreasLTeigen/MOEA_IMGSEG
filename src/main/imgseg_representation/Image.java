package imgseg_representation;

import java.util.List;
import java.util.stream.Collectors;

public class Image implements Cloneable{

    private List<List<Pixel>> pixels;

    public Image(List<List<Pixel>> pixels) {
        this.pixels = pixels;
    }


    public int getWidth() {
        return pixels.size() == 0 ? 0: pixels.get(0).size();
    }
    public int getHeight() {
        return pixels.size();
    }
    public Pixel getPixel(int x, int y) {
        return pixels.get(y).get(x);
    }
    public Pixel getPixel(SegLabel label) {
        return getPixel(label.x, label.y);
    }


    /**
     * get a list of all neighbours in the order [right, left, top, bot, upright, botright, topleft, botleft]
     */
    public List<Pixel> getNeighbours(int x, int y) {
        return null;
    }
    public List<Pixel> getNeighbours(Pixel p) {
        return getNeighbours(p.x, p.y);
    }

    public List<List<Pixel>> getPixels() {
        return pixels;
    }


    public Image clone() {
        List<List<Pixel>> clonedP = pixels.stream().map(
                row -> row.stream()
                        .map(Pixel::clone)
                        .collect(Collectors.toList())
        ).collect(Collectors.toList());
        return new Image(clonedP);
    }
}
