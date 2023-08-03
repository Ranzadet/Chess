package Chess;

public class Rook extends Piece{
    public boolean hasMoved;

    public Rook(Square startPos, boolean color){
        super(startPos, color);
        if(color)
            symbol = "R";
        else{
            symbol = "r";
        }
        value = 500;
    }

    
    public Rook copy(Board b){
        Square s = new Square(position.getFile(), position.getRank());
        s.setBoard(b);
        Rook r = new Rook(s, color);
        return r;
    }

    public void move(Square target){
        super.move(target);
        hasMoved = true;
    }

    
}
