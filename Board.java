package Chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class Board {
    public Square[][] boardArray; // (0,0) is a1. (7,7) is h8
    public ArrayList<Piece> activePieces = new ArrayList<>();
    private boolean playerTurn = true;
    public ArrayList<Move> gameLog;
    private final boolean DEBUG = false;

    public Board(){
        boardArray = new Square[8][8];
        for(int i = 0;i<8;i++){
            for(int j = 0;j<8;j++){
                Square s = new Square(i, j);
                s.setBoard(this);
                boardArray[i][j] = s;
            }
        }
        gameLog = new ArrayList<>();
    }

    public void printBoardState(){
        System.out.print("  ");
        for (int i = 0;i<8;i++){
            System.out.print(" -");
        }
        System.out.println();
        for (int rank = 0;rank<8;rank++){
            System.out.print(8-(rank) + " ");
            for (int file = 0;file<8;file++){
                System.out.print("|" + boardArray[file][rank]);
            }
            System.out.println("|");
            System.out.print("  ");
            for (int i = 0;i<8;i++){
                System.out.print(" -");
            }
            System.out.println();
        }
        System.out.print("   ");
        for(int i = 0;i<8;i++){
            System.out.print(""+ (char)(97+i) + " ");
        }
        System.out.println();
    }

    public void printGameLog(){
        for(Move m : gameLog){
            System.out.println(m);
        }
    }


    public boolean isAdjacent(Square s1, Square s2){
        int xdist = Math.abs(s1.getFile() - s2.getFile());
        int ydist = Math.abs(s1.getRank() - s2.getRank());

        if(xdist == 0 && ydist == 1)
            return true;
        if(ydist == 0 && xdist == 1)
            return true;
        if (xdist == 1 && ydist == 1)
            return true;
        
        return false;
    }

    private boolean containsCollision(Square s1, Square s2){

        int xdist = Math.abs(s1.getFile() - s2.getFile());
        int ydist = Math.abs(s1.getRank() - s2.getRank());

        if(xdist == 0 && ydist == 0){
            return true;
        }

        if(isAdjacent(s1, s2))
            return false;

        // int xpos1 = Math.min(s1.getFile(), s2.getFile());
        // int xpos2 = Math.max(s1.getFile(), s2.getFile());

        // int ypos1 = Math.min(s1.getRank(), s2.getRank());
        // int ypos2 = Math.max(s1.getRank(), s2.getRank());

        int xpos1 = s1.getFile();
        int xpos2 = s2.getFile();
        int ypos1 = s1.getRank();
        int ypos2 = s2.getRank();

        int xdirection = 1;
        int ydirection = 1;

        if(xpos2 < xpos1)
            xdirection = -1;
        if(ypos2 < ypos1)
            ydirection = -1;
        
        if (xdist == 0){
              for(int rank = ypos1+ydirection; rank!=ypos2;rank += ydirection){
                if (boardArray[xpos1][rank].isOccupied)
                    return true;
              }
              return false;
        }

        if (ydist == 0){
            for(int file = xpos1+xdirection; file!=xpos2;file += xdirection){
                if (boardArray[file][ypos1].isOccupied)
                    return true;
            }
            return false;
        }

        //square is not diagonal or orthagonal ==> don't bother checking colllisions
        if(xdist != ydist){
            return true;
        }
        
        int rank = ypos1 + ydirection;
        for (int file = xpos1+xdirection; file!=xpos2; file += xdirection){
                if(boardArray[file][rank].isOccupied)
                    return true;
                rank += ydirection;
        }

        return false;
    }


    //NOT DONE
    public Square[] getVision(Piece p){

        ArrayList<Square> seenSquares = new ArrayList<>();
        int visionCount = 0;
        if(p == null)
            return new Square[0];
        for(int file = 0;file<8;file++){
            for(int rank=0;rank<8;rank++){
                Square target = boardArray[file][rank];
                boolean hasIssue = false;

                if(!(p instanceof Knight) && containsCollision(p.position, target)){
                    hasIssue = true;
                }

                if(p instanceof Pawn){
                    if(Math.abs(positionOf(target)[1] - positionOf(p)[1]) != 1){
                        if(((Pawn)p).hasMoved){
                            if(DEBUG)
                                System.out.println("Space ignored: pawns must advance 1 space per movement");
                            hasIssue = true;
                        }
                        else{
                            if(Math.abs(positionOf(target)[1] - positionOf(p)[1]) != 2){
                                hasIssue = true;
                            }
                            else{
                                if(positionOf(target)[0] - positionOf(p)[0] != 0){
                                    hasIssue = true;
                                }
                            }
                        }
                    }
                    else{
                        if (positionOf(target)[1] - positionOf(p)[1] != -1 && p.color){
                            if(DEBUG)
                                System.out.println("Space ignored: White pawns must advance up the board.");
                            hasIssue = true;
                        }
                        else if(positionOf(target)[1] - positionOf(p)[1] != 1 && !p.color){
                            if(DEBUG)
                                System.out.println("Space ignored: Black pawns must advance down the board.");
                            hasIssue = true;
                        }
                    }

                    if(target.isOccupied){
                        if(Math.abs(positionOf(target)[0] - positionOf(p)[0]) != 1){
                            if(DEBUG)
                                System.out.println("Space ignored: pawns can only attack occupied squares to their immediate diagonal.");
                            hasIssue = true;
                        }
                    }
                    else{
                        //attempting to move to diagonal square not occupied by piece
                        //will have to recheck this for en passant
                        if(positionOf(target)[0] != positionOf(p)[0]){
                            if(DEBUG)
                                System.out.println("Space ignored: pawns can only attack diagonally, not move.");
                            if(p.color){
                                if(p.position.getRank() == 3){
                                    Move mv = gameLog.get(gameLog.size()-1);
                                    if(Math.abs(mv.target.getFile() - p.position.getFile()) == 1 && mv.piece instanceof Pawn && mv.originalPosition.getRank() == 1 && ((Pawn)mv.piece).moves == 1 && mv.target.getFile() == target.getFile()){
                                        // System.out.println("En Passant!");
                                    }
                                    else{
                                        hasIssue = true;
                                    }
                                }
                                else{
                                    hasIssue = true;
                                }
                            }
                            else{
                                if(p.position.getRank() == 4){
                                    Move mv = gameLog.get(gameLog.size()-1);
                                    if(Math.abs(mv.target.getFile() - p.position.getFile()) == 1 && mv.piece instanceof Pawn && mv.originalPosition.getRank() == 6 && ((Pawn)mv.piece).moves == 1 && mv.target.getFile() == target.getFile()){
                                        // System.out.println("En Passant!");
                                    }
                                    else{
                                        if(target.getRank() == 5 && target.getFile() == 1){
                                            // System.out.println(mv);
                                            // System.out.println(mv.originalPosition.coordinates());
                                            // if(mv.piece instanceof Pawn)
                                            //     System.out.println(((Pawn)mv.piece).hasMoved);
                                        }
                                        hasIssue = true;
                                    }
                                }
                                else{
                                    hasIssue = true;
                                }
                            }
                        }
                    }
                }
                else if(p instanceof Rook){
                    if(!(positionOf(target)[0] == positionOf(p)[0] || positionOf(target)[1] == positionOf(p)[1])){
                        hasIssue = true;
                    }
                }
                else if(p instanceof King){
                    if(!isAdjacent(p.position, target)){
                        if(p.color && target.getRank() == p.position.getRank() && !((King)p).hasMoved){
                            if(target.getFile() == 2 && boardArray[0][7].isOccupied && boardArray[0][7].getOccupant() instanceof Rook && !((Rook)boardArray[0][7].getOccupant()).hasMoved && !containsCollision(p.position, boardArray[0][7])){
                                //white king castle queenside
                                //check for move through / out of check
                                if(((King)p).inCheck || wouldCheck(new Move(p, boardArray[3][7]))){
                                    hasIssue = true;
                                }
                                else{
                                    //System.out.println("queenside castle white");
                                }
                                
                            }
                            else if(target.getFile() == 6 && boardArray[7][7].isOccupied && boardArray[7][7].getOccupant() instanceof Rook && !((Rook)boardArray[7][7].getOccupant()).hasMoved && !containsCollision(p.position, boardArray[7][7])){
                                //white king castle kingside
                                if(((King)p).inCheck || wouldCheck(new Move(p, boardArray[5][7]))){
                                    hasIssue = true;
                                }
                                else{
                                    //System.out.println("All checks passed kingside castle white");
                                }
                            }
                            else{
                                hasIssue = true;
                            }
                        }
                        else if(!p.color && target.getRank() == p.position.getRank() && !((King)p).hasMoved){
                            if(target.getFile() == 2 && boardArray[0][0].isOccupied && boardArray[0][0].getOccupant() instanceof Rook && !((Rook)boardArray[0][0].getOccupant()).hasMoved && !containsCollision(p.position, boardArray[0][0])){
                                //white king castle queenside
                                //check for move through / out of check
                                if(((King)p).inCheck || wouldCheck(new Move(p, boardArray[3][0]))){
                                    hasIssue = true;
                                }
                                
                            }
                            else if(target.getFile() == 6 && boardArray[7][0].isOccupied && boardArray[7][0].getOccupant() instanceof Rook && !((Rook)boardArray[7][0].getOccupant()).hasMoved && !containsCollision(p.position, boardArray[7][0])){
                                //white king castle kingside
                                if(((King)p).inCheck || wouldCheck(new Move(p, boardArray[5][0]))){
                                    hasIssue = true;
                                }
                            }
                            else{
                                hasIssue = true;
                            }
                        }
                        else{
                            hasIssue = true;
                        }
                    }
                }
                else if(p instanceof Bishop){
                    int xdist = Math.abs(p.position.getFile() - target.getFile());
                    int ydist = Math.abs(p.position.getRank() - target.getRank());

                    if(xdist < 1 || ydist < 1 || xdist != ydist){
                        hasIssue = true;
                    }
                }
                else if(p instanceof Queen){
                    boolean diagonal = true;
                    boolean perpendicular = true;
                    int xdist = Math.abs(p.position.getFile() - target.getFile());
                    int ydist = Math.abs(p.position.getRank() - target.getRank());

                    if(xdist < 1 || ydist < 1 || xdist != ydist){
                        diagonal = false;
                    }
                    if(!(positionOf(target)[0] == positionOf(p)[0] || positionOf(target)[1] == positionOf(p)[1])){
                        perpendicular = false;
                    }
                    if(!diagonal && !perpendicular){
                        hasIssue = true;
                    }

                }
                else if(p instanceof Knight){
                    int xdist = Math.abs(p.position.getFile() - target.getFile());
                    int ydist = Math.abs(p.position.getRank() - target.getRank());

                    if(xdist + ydist != 3){
                        hasIssue = true;
                    }
                    else{
                        if(xdist == 0 || ydist == 0){
                            hasIssue = true;
                        }
                    }
                }

                if(!hasIssue){
                    seenSquares.add(target);
                    visionCount++;
                }
            }
        }
        
        if(DEBUG)
            System.out.println("Seen squares: "+visionCount);
        Square[] squaresArr = new Square[seenSquares.size()];
        for(int i = 0;i<seenSquares.size();i++){
            squaresArr[i] = seenSquares.get(i);
        }

        return squaresArr;
    }

    public int[] positionOf(Piece p){
        for (int file = 0;file<8;file++){
            for(int rank = 0;rank<8;rank++){
                if(boardArray[file][rank].getOccupant() == p){
                    return new int[] {file,rank};
                }
            }
        }

        return new int[] {-1,-1};
    }

    public int[] positionOf(Square s){
        for (int file = 0;file<8;file++){
            for(int rank = 0;rank<8;rank++){
                if(boardArray[file][rank] == s){
                    return new int[] {file,rank};
                }
            }
        }

        return new int[] {-1,-1};
    }

    public boolean contains(Square[] subset, Piece p){
        for(Square s : subset){
            if(DEBUG)
                System.out.println(s);
            if(s != null && s.isOccupied && s.getOccupant() == p){
                return true;
            }
        }
        return false;
    }

    public boolean contains(Square[] subset, Square s){
        for(Square s2 : subset){
            if(s == s2){
                return true;
            }
        }
        return false;
    }

    public boolean wouldCheck(Move mv){
        Piece p = mv.piece;
        Square target = mv.target;
        if(DEBUG)
            System.out.println("Would check target position: "+target.testString());
        Piece tempPiece = null;
        Square tempSquare = null;
        King k = null;
        Board b = new Board(); 
        for (int file = 0;file<8;file++){
            for(int rank=0;rank<8;rank++){
                if(boardArray[file][rank].isOccupied){
                    Piece p2 = boardArray[file][rank].getOccupant().copy(b);
                    Square s = p2.position;

                    if(boardArray[file][rank] == p.position){
                        tempPiece = p2;
                    }
                    
                    b.boardArray[file][rank] = s;
                    if (boardArray[file][rank] == target){
                        tempSquare = s;
                    }
                    if(p.color){
                        if (boardArray[file][rank].getOccupant() instanceof King){
                            if(boardArray[file][rank].getOccupant().color){
                                k = (King)p2;
                            }
                        }
                    }
                    else{
                        if (boardArray[file][rank].getOccupant() instanceof King){
                            if(!boardArray[file][rank].getOccupant().color){
                                k = (King)p2;
                            }
                        }
                    }
                }
                else{
                    if(boardArray[file][rank] == target){
                        tempSquare = b.boardArray[file][rank];
                    }
                }
            }
        }

        for(Move tempMove : gameLog){
            if(tempMove != null){
                b.gameLog.add(tempMove.copy(b));
            }
        }

        if(tempPiece instanceof Pawn && (tempSquare.getRank() == 7 || tempSquare.getRank() == 0)){
            ((Pawn)tempPiece).promoteMove(tempSquare,"q");
        }
        else{
            tempPiece.move(tempSquare);
        }
        if(DEBUG)
            b.printBoardState();

        boolean checked = false;

        for (int file = 0;file<8;file++){
            for(int rank = 0;rank<8;rank++){
                if (b.boardArray[file][rank].isOccupied && b.boardArray[file][rank].getOccupant().color != p.color && contains(b.getVision(b.boardArray[file][rank].getOccupant()), k)){
                    if(b.boardArray[file][rank].getOccupant() instanceof Pawn){
                        //Check to make sure the king is not directly in front of the pawn, which would not put the king in check.
                        if(k.position.getFile() != file){
                            checked = true;
                        }
                    }
                    else{
                        checked = true;
                    }
                }
            }
        }

        if(DEBUG)
            System.out.println("Would check: "+checked);
        return checked;
        
    }

    //For any move suggested by the player
    public boolean isValidMove(Move m){
        Piece p = m.piece;
        Square target = m.target;
        
        if(p.color != playerTurn){
            String name = p.color ? "White" : "Black";
            name += "'s";
            System.out.println("Invalid move: It is not " + name + " turn!");
            return false;
        }

        if(target.isOccupied && target.getOccupant().color == p.color){
            System.out.println("Invalid move: pieces cannot attack pieces of their own color.");
            return false;
        }

        if(wouldCheck(m)){
            System.out.println("Invalid move: " + p + " to " + target.testString() + " would result in a check.");
            return false;
        }

        if(!contains(getVision(p), target)){
            System.out.println("Invalid move: " + p + " on square " + p.position.coordinates() + " cannot move to "
             + target.coordinates() + ".");
            return false;
        }

        //If none of the above checks have caused an early return, allow the attempted move.
        return true;
    }

    //To avoid continuous calls to getVision() when finding all possible player moves
    public boolean checkValidMove(Move m){
        Piece p = m.piece;
        Square target = m.target;
        
        if(p.color != playerTurn){
            String name = p.color ? "White" : "Black";
            name += "'s";
            if(DEBUG)
                System.out.println("Invalid move: It is not " + name + " turn!");
            return false;
        }

        if(target.isOccupied && target.getOccupant().color == p.color){
            if(DEBUG)
                System.out.println("Invalid move: pieces cannot attack pieces of their own color.");
            return false;
        }

        if(wouldCheck(m)){
            if(DEBUG)
                System.out.println("Invalid move: " + p + " to " + target.testString() + " would result in a check.");
            return false;
        }
        
        return true;
    }


    public void move(Move m){
        if(m.piece == null){
            System.out.println("Invald move: there is no piece located on that square.");
            return;
        }
        if(isValidMove(m)){
            if(m.piece instanceof King && !isAdjacent(m.piece.position, m.target)){
                if(m.piece.position.getFile() > m.target.getFile()){
                    m.castleStr = "O-O-O";
                }else{
                    m.castleStr = "O-O";
                }
            }
            Move snapshotMove = new Move(m.piece.copy(this), m.target);
            snapshotMove.castleStr = m.castleStr;
            String atkStr = "";
            if(m.target.isOccupied){
                atkStr = "x";
            }
            else{
                if(m.piece instanceof Pawn && m.target.getFile() != m.piece.position.getFile()){
                    atkStr = "x";
                }
            }
            
            move(m.piece, m.target);
            if(snapshotMove.piece instanceof Pawn && (snapshotMove.target.getRank() == 0 || snapshotMove.target.getRank() == 7)){
                snapshotMove = new PromoteMove(snapshotMove.piece, m.target, m.piece.toString());
            }
            for(Piece tp : activePieces){
                if(tp instanceof King && ((King)tp).inCheck){
                    snapshotMove.checkStr = "+";
                }
            }
            snapshotMove.attackStr = atkStr;
            snapshotMove.originalPosition = m.originalPosition;
            snapshotMove.piece.hasMoved = true;
            snapshotMove.piece.moves += 1;
            gameLog.add(snapshotMove);
        }
    }

    private void move(Piece p, Square target){
        // int count = 0;
        // for(Square sq : getVision(p)){
        //     count++;
        //     if(sq != null)
        //         System.out.println(sq.testString());
        // }
        // System.out.println(count);
        // System.out.println("occupied; "+boardArray[4][1].isOccupied);

        p.move(target);
        playerTurn = !playerTurn;

        //After move, check if either king is in check. Only the king of the color opposite to the player who just moved can be in check
        //at this point, as wouldCheck() should have caught a case of placing one's own king in check already.
        for(Piece pc : activePieces){
            if(pc instanceof King){
                activePieces.forEach((piece) -> {
                    if(piece.color != pc.color && contains(getVision(piece), pc.position)){
                        //String name = pc.color ? "White" : "Black";
                        //System.out.println("Move has placed " + name + " King into check!");
                        ((King)pc).inCheck = true;
                    }
                });
                if(pc.color == p.color){
                    ((King)pc).inCheck = false;
                }
            }
        }
    }


    public Move[] getMoves(boolean player){

        ArrayList<Move> moves = new ArrayList<>();

        for(Piece p : activePieces){
            if(p.color == player){
                Square[] spaces = getVision(p);
                for (Square s : spaces){
                    if(p instanceof Pawn && (s.getRank() == 7 || s.getRank() == 0)){
                        String[] types = {"q", "r", "n", "b"};
                        for(String t : types){
                            Move m = new PromoteMove(p, s, t);
                            if(checkValidMove(m)){
                                moves.add(m);
                            }
                        }
                    }
                    else{
                        Move m = new Move(p, s);
                        if(checkValidMove(m)){
                            moves.add(m);
                        }
                    }
                }
            }
        }

        Move[] movesArr = new Move[moves.size()];
        for(int i = 0;i<moves.size();i++){
            movesArr[i] = moves.get(i);
        }
        return movesArr;
    }

    
    public String winCondition(){
        //TODO: check for which player is in check after each turn, and what moves can be taken to get them out of check
        boolean whiteCheck = false;
        boolean blackCheck = false;
        for(Piece p : activePieces){
            if(p instanceof King && ((King)p).inCheck){
                if(p.color)
                    whiteCheck = true;
                else{
                    blackCheck = true;
                }
            }
        }

        if(playerTurn && whiteCheck){
            Move[] moves = getMoves(true);
            if(moves.length == 0){
                gameLog.get(gameLog.size()-1).checkStr = "#";
                return "White has been checkmated";
            }
        }
        else if(!playerTurn && blackCheck){
            Move[] moves = getMoves(false);
            if(moves.length == 0){
                gameLog.get(gameLog.size()-1).checkStr = "#";
                return "Black has been checkmated";
            }
        }
        else{
            if(playerTurn){
                Move[] moves = getMoves(true);
                if(moves.length == 0){
                    return "Draw by stalemate";
                }
            }
            else{
                Move[] moves = getMoves(false);
                if(moves.length == 0){
                    return "Draw by stalemate";
                }
            }
        }

        return "Game in Progress";
    }


    //For a standard game of chess. Future variants may include chess960 or other stuff idk
    public void standardSetup(){
        //Instatiate rooks
        Rook r1 = new Rook(boardArray[0][0], false);
        Rook r2 = new Rook(boardArray[7][0], false);
        Rook r3 = new Rook(boardArray[0][7], true);
        Rook r4 = new Rook(boardArray[7][7], true);

        //Instatiate knights
        Knight n1 = new Knight(boardArray[1][0], false);
        Knight n2 = new Knight(boardArray[6][0], false);
        Knight n3 = new Knight(boardArray[1][7], true);
        Knight n4 = new Knight(boardArray[6][7], true);

        //Instatiate bishops
        Bishop b1 = new Bishop(boardArray[2][0], false);
        Bishop b2 = new Bishop(boardArray[5][0], false);
        Bishop b3 = new Bishop(boardArray[2][7], true);
        Bishop b4 = new Bishop(boardArray[5][7], true);

        //Instatiate Queens
        Queen q1 = new Queen(boardArray[3][0], false);
        Queen q2 = new Queen(boardArray[3][7], true);

        //Instantiate Kings
        King k1 = new King(boardArray[4][0], false);
        King k2 = new King(boardArray[4][7], true);

        activePieces.addAll(Arrays.asList(new Piece[] {r1, r2, r3, r4, n1, n2, n3, n4, b1, b2, b3, b4, q1, q2, k1, k2}));

        for (int i = 0;i<8;i++){
            Pawn p = new Pawn(boardArray[i][1], false);
            activePieces.add(p);
        }
        for (int i = 0;i<8;i++){
            Pawn p = new Pawn(boardArray[i][6], true);
            activePieces.add(p);
        }
    }
}
