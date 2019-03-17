package imgseg_representation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Image implements Cloneable{

    private List<List<Pixel>> pixels;

    /**
     * create a black image to the size of the givven image
     */
    public Image(Image img) {
        pixels = img.pixels.stream()
                .map(prow -> prow.stream()
                        .map(p -> new Pixel(0, 0, 0, p.x, p.y))
                        .collect(Collectors.toList())
                )
                .collect(Collectors.toList());
    }

    public Image(Segmentation seg) {
        this(seg.getWidth(), seg.getHeight());
    }
    public Image(GraphSeg gseg) {
        this(gseg.getWidth(), gseg.getHeight());
    }
    /**
     * Create a black image to the size given
     */
    public Image(int width, int height) {
        this(
                IntStream.range(0, height)
                        .mapToObj(y -> IntStream.range(0, width)
                                .mapToObj(x -> new Pixel(0, 0, 0, x, y))
                                .collect(Collectors.toList())
                        )
                        .collect(Collectors.toList())
        );
    }
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
    public Pixel getPixel(GraphSegNode node) {
        return getPixel(node.x, node.y);
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

    public Stream<Pixel> streamAll() {
        return pixels.stream().flatMap(List::stream);
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
