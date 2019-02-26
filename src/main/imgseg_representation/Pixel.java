package imgseg_representation;

import java.util.List;

public class Pixel {
    public float r, g, b;
    public int row;
    public int col;

    Pixel(float r, float g, float b, int row, int col) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.row = row;
        this.col = col;
    }
    //public List<imgseg_representation.Pixel> neighbours;
}