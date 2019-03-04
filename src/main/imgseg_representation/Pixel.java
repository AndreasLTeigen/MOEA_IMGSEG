package imgseg_representation;

import java.util.List;

public class Pixel {
    public float r, g, b;
    public int y;
    public int x;

    Pixel(float r, float g, float b, int y, int x) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.y = y;
        this.x = x;
    }
    //public List<imgseg_representation.Pixel> neighbours;
}