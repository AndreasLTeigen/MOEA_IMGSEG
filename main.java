public class Solver{
    public static void main(String[] args){
        Chromosone test = new Chromosone();

    }
}

public class Chromosone {
    public List<List<int>> segmentation;
    public int row_size;
    public int collumn_size;

    public Chromosone(){
        for (int y = 0; y < y_size; y++) {
            ArrayList row = new ArrayList();
            for (int x = 0; x < x_size; x++) {
                row.add(0);
            }
            segmentation.add(row);
        }
    }

    public print(){
        for (int y = 0; y < y_size; y++){
            for (int x = 0; x < x_size; x++){
                System.out.print(segmentation[y][x] + " ");
            }
        }
    }
}

public class Pixel {
    public float color;
    public int x;
    public int y;
    public List<Pixel> neighbours;
}