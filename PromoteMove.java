package Chess;

public class PromoteMove extends Move{
    public String type;

    public PromoteMove(Piece p, Square target, String pieceType){
        super(p, target);
        type = pieceType;
    }

    public String toString(){
        return super.toString() + " = " + type;
    }
}
