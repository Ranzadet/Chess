package Chess;
import java.util.Scanner;

public class Pawn extends Piece{
    
    public Pawn(Square startPos, boolean color){
        super(startPos, color);
        if(color)
            symbol = "P";
        else{
            symbol = "p";
        }
        value = 1;
    }

    public Pawn copy(Board b){
        Square s = new Square(position.getFile(), position.getRank());
        s.setBoard(b);
        Pawn p = new Pawn(s, color);
        p.moves = moves;
        p.hasMoved = hasMoved;
        return p;
    }

    
    public void move(Square target){
        if(target.getFile() != position.getFile() && target.isOccupied == false){
            position.getBoard().boardArray[target.getFile()][position.getRank()].getOccupant().destroy();
            position.getBoard().boardArray[target.getFile()][position.getRank()].setOccupant(null);
            position.getBoard().boardArray[target.getFile()][position.getRank()].isOccupied = false;
        }
        super.move(target);
        if(target.getRank() == 7 || target.getRank() == 0){
            Scanner scanner = new Scanner(System.in);
            System.out.println("Pawn promoting! What piece would you like to promote to?");
            String type = scanner.nextLine();
            promote(type);
        }
    }

    public void promoteMove(Square target, String type){
        super.move(target);
        promote(type);
    }

    private void promote(String type){
        type = type.toLowerCase();
        //System.out.println("Promoting to " + type);
        if(type.equals("knight") || type.equals("n") || type.equals("kn")){
            Knight kn = new Knight(position, color);
        }
        else if(type.equals("bishop") || type.equals("b")){
            Bishop b = new Bishop(position, color);
        }
        else if(type.equals("rook") || type.equals("r")){
            Rook r = new Rook(position, color);
        }
        else{
            Queen q = new Queen(position, color);
        }
        destroy();
    }

}
