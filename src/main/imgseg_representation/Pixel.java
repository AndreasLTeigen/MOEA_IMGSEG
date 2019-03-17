package imgseg_representation;

import java.util.List;

public class Pixel implements Cloneable{
    public float r, g, b;
    public int y;
    public int x;

    public Pixel(float r, float g, float b, int x, int y) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.y = y;
        this.x = x;
    }
    //public List<imgseg_representation.Pixel> neighbours;

    public float getPixelDistance(Pixel p2){
        return (float)Math.sqrt( Math.pow(this.r-p2.r, 2) + Math.pow(this.g-p2.g, 2) + Math.pow(this.b-p2.b, 2) );
    }
    public float getPixelIntensity(){
        return (float)Math.sqrt( Math.pow(this.r, 2) + Math.pow(this.g, 2) + Math.pow(this.b, 2) );
    }

    public void setColor(float r, float g, float b) {
        this.r = r; this.g = g; this.b = b;
    }
    public void setColor(float... color) {
        setColor(color[0], color[1], color[2]);
    }

    @Override
    public Pixel clone() {
        return new Pixel(r, g, b, x, y);
    }
}