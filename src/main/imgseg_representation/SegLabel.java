package imgseg_representation;

public class SegLabel {

    public int label;
    public int x, y;

    public SegLabel(int x, int y, int label) {
        this.x = x;
        this.y = y;
        this.label = label;
    }
}
