package Chess;

public class Queen extends Piece{
    

    public Queen(Square startPos, boolean color){
        super(startPos, color);
        if(color)
            symbol = "Q";
        else{
            symbol = "q";
        }
        value = 900;
    }

    
    public Queen copy(Board b){
        Square s = new Square(position.getFile(), position.getRank());
        s.setBoard(b);
        Queen q = new Queen(s, color);
        return q;
    }


}
