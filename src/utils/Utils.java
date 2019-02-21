package utils;


public class Utils {

    public static int randRange(int low, int high) {
        return (int)Math.floor(low + Math.random()*(high-low));
    }

    public static boolean testProb(float prob) {
        return prob > Math.random();
    }
}
