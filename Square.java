package Chess;

public class Square {
    public boolean isOccupied;
    private Piece occupant;
    private int file;
    private int rank;
    private Board board;


    public Square(int f, int r){
        file = f;
        rank = r;
    }

    public void setBoard(Board b){
        board = b;
    }

    public Board getBoard(){
        return board;
    }

    public Piece getOccupant(){
        return occupant;
    }

    public void setOccupant(Piece p){
        occupant = p;
    }

    public String toString(){
        if (isOccupied)
            return occupant.toString();
        return " ";
    }

    public String testString(){
        if (isOccupied)
            return occupant.toString()+" at " + chessPosition();
        return "("+file+", "+rank+")" + "  -  " + chessPosition();
    }

    public String coordinates(){
        return "(" + file + "," + rank + ")";
    }

    public String chessPosition(){
        char[] files = {'a','b','c','d','e','f','g','h'};
        return "" + files[file] + (8-rank);
    }

    public int getFile(){
        return file;
    }

    public int getRank(){
        return rank;
    }

}
