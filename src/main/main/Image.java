package main;

import java.util.List;

public class Image {

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

    /**
     * get a list of all neighbours in the order [right, left, top, bot, upright, botright, topleft, botleft]
     */
    public List<Pixel> getNeighbours(int row, int col) {
        return null;
    }
    public List<Pixel> getNeighbours(Pixel p) {
        return getNeighbours(p.row, p.col);
    }

    public List<List<Pixel>> getPixels() {
        return pixels;
    }

    public Pixel getPixel(SegLabel label) {
        return getPixel(label.x, label.y);
    }
}
