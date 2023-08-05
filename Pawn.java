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
        value = 100;
        wasPawn = true;
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
        if(target.getFile() != position.getFile() && target.isOccupied == false && (color && target.getRank() < position.getRank() || !color && target.getRank() > position.getRank())){
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
        Piece p;
        if(type.equals("knight") || type.equals("n") || type.equals("kn")){
            p = new Knight(position, color);
        }
        else if(type.equals("bishop") || type.equals("b")){
            p = new Bishop(position, color);
        }
        else if(type.equals("rook") || type.equals("r")){
            p = new Rook(position, color);
        }
        else{
            p = new Queen(position, color);
        }
        p.wasPawn = true;
        destroy();
    }

}
