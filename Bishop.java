package Chess;

public class Bishop extends Piece{

    public Bishop(Square startPos, boolean color){
        super(startPos, color);
        if(color)
            symbol = "B";
        else{
            symbol = "b";
        }
        value = 315;
    }

    
    public Bishop copy(Board b){
        Square s = new Square(position.getFile(), position.getRank());
        s.setBoard(b);
        Bishop bs = new Bishop(s, color);
        return bs;
    }

    
}
