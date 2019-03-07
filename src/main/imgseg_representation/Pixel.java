package imgseg_representation;

import java.util.List;

public class Pixel implements Cloneable{
    public float r, g, b;
    public int y;
    public int x;

    Pixel(float r, float g, float b, int x, int y) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.y = y;
        this.x = x;
    }
    //public List<imgseg_representation.Pixel> neighbours;

    @Override
    public Pixel clone() {
        return new Pixel(r, g, b, x, y);
    }
}