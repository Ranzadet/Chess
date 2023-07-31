package Chess;

public class PromoteMove extends Move{
    public String type;

    public PromoteMove(Piece p, Square target, String pieceType){
        super(p, target);
        type = pieceType;
    }

    public String toString(){
        String s = super.toString();
        if(checkStr.equals("")){
            return s + "=" + type;
        }
        return s.substring(0,s.length()) + "=" + type + checkStr;
    }
}
