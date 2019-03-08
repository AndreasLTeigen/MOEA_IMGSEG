package imgseg_representation;

public class SegLabel {

    public int label;
    public int x, y;

    public SegLabel(int x, int y, int label) {
        this.x = x;
        this.y = y;
        this.label = label;
    }

    public boolean equals(SegLabel o) {
        return label == o.label;
    }

    public String toString() {
        return "Label: " + label + " x:" + x + " y:" + y;
    }
}
