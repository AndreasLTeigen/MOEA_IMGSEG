package main;

public class Main{
    public static void main(String[] args){

        Image img = ImageUtils.loadImage("images/86016/Test image.jpg");
        ImageUtils.drawImage(img);
    }
}