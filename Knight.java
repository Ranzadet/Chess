package Chess;

public class Knight extends Piece{
    

    public Knight(Square startPos, boolean color){
        super(startPos, color);
        if(color)
            symbol = "N";
        else{
            symbol = "n";
        }
        value = 3;
    }

    public Knight copy(Board b){
        Square s = new Square(position.getFile(), position.getRank());
        s.setBoard(b);
        Knight n = new Knight(s, color);
        return n;
    }


}
