package Chess;

public class King extends Piece{
    public boolean inCheck;
    
    public King(Square startPos, boolean color){
        super(startPos, color);
        if(color)
            symbol = "K";
        else{
            symbol = "k";
        }
        inCheck = false;
        value = 99999;
    }

    public King copy(Board b){
        Square s = new Square(position.getFile(), position.getRank());
        s.setBoard(b);
        King k = new King(s, color);
        return k;
    }

    public void move(Square target){
        Square s = position;
        super.move(target);
        if(!target.getBoard().isAdjacent(s, target)){
            if(color && !target.getBoard().boardArray[4][7].isOccupied){
                if(target.getFile() < s.getFile()){
                    castle((Rook)position.getBoard().boardArray[0][7].getOccupant());
                }
                else{
                    castle((Rook)position.getBoard().boardArray[7][7].getOccupant());
                }
            }
            else if(!color && !target.getBoard().boardArray[4][0].isOccupied){
                if(target.getFile() < s.getFile()){
                    castle((Rook)position.getBoard().boardArray[0][0].getOccupant());
                }
                else{
                    castle((Rook)position.getBoard().boardArray[7][0].getOccupant());
                }
            }
        }
    }

    public void castle(Rook r){
        if(r.position.getFile() < position.getFile()){
            r.move(position.getBoard().boardArray[position.getFile()+1][position.getRank()]);
        }
        else{
            r.move(position.getBoard().boardArray[position.getFile()-1][position.getRank()]);
        }
    }

}
