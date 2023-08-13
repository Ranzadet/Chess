package Chess;

public abstract class Piece {
    public Square position;
    public int moves;
    public String symbol;
    public boolean color; //true for white, false for black
    public int value;
    public boolean hasMoved;
    public boolean wasPawn;
    public int activePos;

    public Piece(Square startPos, boolean color){
        position = startPos;
        startPos.isOccupied = true;
        startPos.setOccupant(this);
        this.color = color;
        // Board.activePieces.add(this);
    }

    public void move(Square target){
        //System.out.println(this+" to "+target.coordinates());
        position.setOccupant(null);
        hasMoved = true;
        moves += 1;
        position.isOccupied = false;
        position = target;
        if(target.isOccupied)
            target.getOccupant().destroy();
        target.setOccupant(this);
        target.isOccupied = true;
    }

    public void destroy(){
        position.getBoard().activePositions.push(activePos);
        // position.getBoard().activePieces.remove(this);
        position.getBoard().activePieces[activePos] = null;
    }

    public void setActivePos(int pos){
        activePos = pos;
    }

    public String toString(){
        return symbol;
    }

    public abstract Piece copy(Board b);

}
