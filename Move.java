package Chess;

public class Move {
    public Piece piece;
    public Square target;
    public Square originalPosition;
    public String castleStr = "";
    public String checkStr = "";
    public String attackStr = "";

    public Move(String moveStr, Board b){
        char[] files = {'a','b','c','d','e','f','g','h'};
        char[] pieces = {'N','K','Q','R','B'};

        String[] moveArr = moveStr.split(" ");

        //In this case, the player has signified a pawn push by expluding a piece name, i.e. "b4"
        if(moveArr.length == 1){
            int file = -1;

            //Because (0,0) on the board is the top left instead of bottom left, we need to flip the rank
            int rank = 8 - Integer.parseInt(""+moveStr.charAt(1));
            
            for(int i = 0;i<8;i++){
                if(moveStr.charAt(0) == files[i]){
                    file = i;
                }
            }

            //TODO: assign a target square out of file and rank, and extrapolate piece by finding square before target
        }
        else{
            // char pieceType = moveArr[0].charAt(0);
            int file1 = -1;
            int rank1 = 8 - Integer.parseInt(""+moveArr[0].charAt(1));
            
            int file2 = -1;
            int rank2 = 8 - Integer.parseInt(""+moveArr[1].charAt(1));

            for(int i = 0;i<8;i++){
                if(moveArr[0].charAt(0) == files[i]){
                    file1 = i;
                }
                if(moveArr[1].charAt(0) == files[i]){
                    file2 = i;
                }
            }

            //Now, assign piece and target to the piece and square at the coordinates of (file1, rank1) and (file2, rank2)
            if(b.boardArray[file1][rank1].isOccupied){
                piece = b.boardArray[file1][rank1].getOccupant();
                target = b.boardArray[file2][rank2];
                originalPosition = b.boardArray[file1][rank1];
            }
            else{
                piece = null;
                target = null;
            }
            
            // System.out.println("Piece location: "+ piece.position.testString());
            // System.out.println("Target: "+target.testString());
        }
    }

    public Move(Piece p, Square s){
        piece = p;
        target = s;
    }

    public String toString(){
        if(!castleStr.equals("")){
            return castleStr;
        }
        // return piece + " " + piece.position.chessPosition() +" to " + target.chessPosition();
        if(piece instanceof Pawn){
            if(attackStr.equals(""))     
                return target.chessPosition() + checkStr;
            return originalPosition.chessPosition() + attackStr + target.chessPosition() + checkStr;
        }
        if(attackStr.equals(""))
            return piece.toString().toUpperCase() + target.chessPosition() + checkStr;
        return piece.toString().toUpperCase() + attackStr + target.chessPosition() + checkStr;
    }

}
