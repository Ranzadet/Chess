package Chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Stack;

public class Board {
    public Square[][] boardArray; // (0,0) is a8. (7,7) is h0
    public Piece[] activePieces = new Piece[32];
    // public ArrayList<Piece> activePieces = new ArrayList<>();
    private boolean playerTurn = true;
    public ArrayList<Move> gameLog;
    private final boolean DEBUG = false;
    public int halfClock = 0;
    public int fullClock = 0;
    public Stack<Integer> activePositions = new Stack<>();
    public King[] kings = new King[2];

    public Board(){
        boardArray = new Square[8][8];
        for(int i = 0;i<8;i++){
            for(int j = 0;j<8;j++){
                Square s = new Square(i, j);
                s.setBoard(this);
                boardArray[i][j] = s;
            }
        }
        gameLog = new ArrayList<>(200);
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

    public boolean getPlayer(){
        return playerTurn;
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
                    if(Math.abs(target.getRank() - p.position.getRank()) != 1){
                        if(p.hasMoved || (p.position.getRank() != 1 && p.position.getRank() != 6)){
                            hasIssue = true;
                        }
                        else{
                            if(Math.abs(target.getRank() - p.position.getRank()) != 2){
                                hasIssue = true;
                            }
                            else{
                                if(target.getFile() - p.position.getFile() != 0){
                                    hasIssue = true;
                                }
                            }
                        }
                    }
                    else{
                        if (target.getRank() - p.position.getRank() != -1 && p.color){
                            hasIssue = true;
                        }
                        else if(target.getRank() - p.position.getRank() != 1 && !p.color){
                            hasIssue = true;
                        }
                    }

                    if(target.isOccupied){
                        if(Math.abs(target.getFile() - p.position.getFile()) != 1){
                            hasIssue = true;
                        }
                    }
                    else{
                        //attempting to move to diagonal square not occupied by piece
                        //will have to recheck this for en passant
                        if(target.getRank() == 0 && !p.color || target.getRank() == 7 && p.color){
                            hasIssue = true;
                        }
                        if(target.getFile() != p.position.getFile()){
                            if(p.color){
                                if(p.position.getRank() == 3 && gameLog.size() > 0){
                                    Move mv = gameLog.get(gameLog.size()-1);
                                    if(Math.abs(mv.target.getFile() - p.position.getFile()) == 1 && mv.piece instanceof Pawn && mv.originalPosition.getRank() == 1 && mv.piece.moves == 1 && mv.target.getFile() == target.getFile()){
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
                                if(p.position.getRank() == 4 && gameLog.size() > 0){
                                    Move mv = gameLog.get(gameLog.size()-1);
                                    if(Math.abs(mv.target.getFile() - p.position.getFile()) == 1 && mv.piece instanceof Pawn && mv.originalPosition.getRank() == 6 && mv.piece.moves == 1 && mv.target.getFile() == target.getFile()){
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
                    if(!(target.getFile() == p.position.getFile() || target.getRank() == p.position.getRank())){
                        hasIssue = true;
                    }
                }
                else if(p instanceof King){
                    if(!isAdjacent(p.position, target)){
                        if(p.color && target.getRank() == p.position.getRank() && !((King)p).hasMoved){
                            if(target.getFile() == 2 && boardArray[0][7].isOccupied && boardArray[0][7].getOccupant() instanceof Rook && !boardArray[0][7].getOccupant().hasMoved && !containsCollision(p.position, boardArray[0][7])){
                                //white king castle queenside
                                //check for move through / out of check
                                if(((King)p).inCheck || wouldCheck(new Move(p, boardArray[3][7]))){
                                    hasIssue = true;
                                }
                                else{
                                    //System.out.println("queenside castle white");
                                }
                                
                            }
                            else if(target.getFile() == 6 && boardArray[7][7].isOccupied && boardArray[7][7].getOccupant() instanceof Rook && !boardArray[7][7].getOccupant().hasMoved && !containsCollision(p.position, boardArray[7][7])){
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
                            if(target.getFile() == 2 && boardArray[0][0].isOccupied && boardArray[0][0].getOccupant() instanceof Rook && !boardArray[0][0].getOccupant().hasMoved && !containsCollision(p.position, boardArray[0][0])){
                                //black king castle queenside
                                //check for move through / out of check
                                if(((King)p).inCheck || wouldCheck(new Move(p, boardArray[3][0]))){
                                    hasIssue = true;
                                }
                                
                            }
                            else if(target.getFile() == 6 && boardArray[7][0].isOccupied && boardArray[7][0].getOccupant() instanceof Rook && !boardArray[7][0].getOccupant().hasMoved && !containsCollision(p.position, boardArray[7][0])){
                                //black king castle kingside
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
                    if(!(target.getFile() == p.position.getFile() || target.getRank() == p.position.getRank())){
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
        King k = p.color ? kings[0] : kings[1];
        // Piece tempPiece = null;
        // Square tempSquare = null;
        // Board b = copy(); 
        // for (int file = 0;file<8;file++){
        //     for(int rank=0;rank<8;rank++){
        //         if(boardArray[file][rank].isOccupied){
        //             Piece p2 = b.boardArray[file][rank].getOccupant();
        //             if(boardArray[file][rank] == p.position){
        //                 tempPiece = p2;
        //             }

        //             if (boardArray[file][rank] == target){
        //                 tempSquare = b.boardArray[file][rank];
        //             }

        //             if(p.color){
        //                 if (boardArray[file][rank].getOccupant() instanceof King){
        //                     if(boardArray[file][rank].getOccupant().color){
        //                         k = (King)p2;
        //                     }
        //                 }
        //             }
        //             else{
        //                 if (boardArray[file][rank].getOccupant() instanceof King){
        //                     if(!boardArray[file][rank].getOccupant().color){
        //                         k = (King)p2;
        //                     }
        //                 }
        //             }
        //         }
        //         else{
        //             if(boardArray[file][rank] == target){
        //                 tempSquare = b.boardArray[file][rank];
        //             }
        //         }
        //     }
        // }

        // if(mv instanceof PromoteMove){
        //     ((Pawn)tempPiece).promoteMove(tempSquare,((PromoteMove)mv).type);
        // }
        // else{
        //     tempPiece.move(tempSquare);
        // }

        if(mv.castleStr.equals("") && mv.piece instanceof King && !isAdjacent(mv.piece.position, mv.target)){
            if(mv.piece.position.getFile() > mv.target.getFile()){
                mv.castleStr = "O-O-O";
            }else{
                mv.castleStr = "O-O";
            }
        }
        if(mv.target.isOccupied){
            mv.attackStr = mv.target.getOccupant().toString();
        }
        else{
            if(mv.piece instanceof Pawn && mv.target.getFile() != mv.piece.position.getFile()){
                mv.attackStr = "p";
                mv.isPassant = true;
            }
        }

        if(mv instanceof PromoteMove){
            ((Pawn)p).promoteMove(target, ((PromoteMove)mv).type);
        }
        else{
            p.move(target);
        }

        playerTurn = !playerTurn;
        gameLog.add(mv);

        boolean checked = false;

        // for (int file = 0;file<8;file++){
        //     for(int rank = 0;rank<8;rank++){
        //         if (b.boardArray[file][rank].isOccupied && b.boardArray[file][rank].getOccupant().color != p.color && contains(b.getVision(b.boardArray[file][rank].getOccupant()), k)){
        //             if(b.boardArray[file][rank].getOccupant() instanceof Pawn){
        //                 //Check to make sure the king is not directly in front of the pawn, which would not put the king in check.
        //                 if(k.position.getFile() != file){
        //                     checked = true;
        //                 }
        //             }
        //             else{
        //                 checked = true;
        //             }
        //         }
        //     }
        // }

        for (int file = 0;file<8;file++){
            for(int rank = 0;rank<8;rank++){
                if (boardArray[file][rank].isOccupied && boardArray[file][rank].getOccupant().color != p.color && contains(getVision(boardArray[file][rank].getOccupant()), k)){
                    if(boardArray[file][rank].getOccupant() instanceof Pawn){
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

        unMove(mv);
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

        if(!contains(getVision(p), target)){
            System.out.println("Invalid move: " + p + " on square " + p.position.coordinates() + " cannot move to "
             + target.coordinates() + ".");
            return false;
        }

        if(wouldCheck(m)){
            System.out.println("Invalid move: " + p + " to " + target.testString() + " would result in a check.");
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
            return false;
        }

        if(target.isOccupied && target.getOccupant().color == p.color){
            return false;
        }

        if(wouldCheck(m)){
            return false;
        }
        
        return true;
    }


    public void searchMove(Move m){
        // if(m.castleStr.equals("") && m.piece instanceof King && !isAdjacent(m.piece.position, m.target)){
        //     if(m.piece.position.getFile() > m.target.getFile()){
        //         m.castleStr = "O-O-O";
        //     }else{
        //         m.castleStr = "O-O";
        //     }
        // }
        // if(m.target.isOccupied){
        //     m.attackStr = m.target.getOccupant().toString();
        // }
        // else{
        //     if(m.piece instanceof Pawn && m.target.getFile() != m.piece.position.getFile()){
        //         m.attackStr = "p";
        //         m.isPassant = true;
        //     }
        // }
        
        move(m.piece, m.target);
        gameLog.add(m);
    }

    public void move(Move m){
        if(m.piece == null){
            System.out.println("Invald move: there is no piece located on that square.");
            return;
        }
        if(isValidMove(m)){
            //NOTE: commented code in this method should have already been run inside of wouldCheck()
            
            // if(m.castleStr.equals("") && m.piece instanceof King && !isAdjacent(m.piece.position, m.target)){
            //     if(m.piece.position.getFile() > m.target.getFile()){
            //         m.castleStr = "O-O-O";
            //     }else{
            //         m.castleStr = "O-O";
            //     }
            // }
            // if(m.piece instanceof Pawn && m.target.getFile() != m.piece.position.getFile() && !m.target.isOccupied){
            //     m.isPassant = true;
            // }
            Move snapshotMove = new Move(m.piece.copy(this), m.target);
            snapshotMove.castleStr = m.castleStr;
            snapshotMove.isPassant = m.isPassant;
            String atkStr = m.attackStr;
            // String atkStr = "";
            // if(m.target.isOccupied){
            //     atkStr = m.target.getOccupant().toString();
            // }
            // else{
            //     if(m.piece instanceof Pawn && m.target.getFile() != m.piece.position.getFile()){
            //         atkStr = "p";
            //     }
            // }
            
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

        if(p instanceof Pawn || target.isOccupied){
            halfClock = 0;
        }
        else{
            halfClock++;
        }
        if(!playerTurn)
            fullClock++;
        p.move(target);
        playerTurn = !playerTurn;

        //After move, check if either king is in check. Only the king of the color opposite to the player who just moved can be in check
        //at this point, as wouldCheck() should have caught a case of placing one's own king in check already.
        // for(Piece pc : activePieces){
        //     if(pc instanceof King){
        //         activePieces.forEach((piece) -> {
        //             if(piece.color != pc.color && contains(getVision(piece), pc.position)){
        //                 //String name = pc.color ? "White" : "Black";
        //                 //System.out.println("Move has placed " + name + " King into check!");
        //                 ((King)pc).inCheck = true;
        //             }
        //         });
        //         if(pc.color == p.color){
        //             ((King)pc).inCheck = false;
        //         }
        //     }
        // }

        King k = playerTurn ? kings[0] : kings[1];
        boolean isSpotted = false;
        for(Piece pc : activePieces){
            if(pc == null){
                continue;
            }
            if(pc.color != k.color && contains(getVision(pc), k)){
                isSpotted = true;
            }
            if(pc.color != k.color && pc instanceof King){
                ((King)pc).inCheck = false; //After a move, your king should be out of check
            }
        }
        k.inCheck = isSpotted;
    }

    public void unMove(Move m){
        playerTurn = !playerTurn;
        gameLog.remove(gameLog.size()-1);
        halfClock--;
        if(!m.piece.color){
            fullClock--;
        }

        if(m.castleStr.equals("O-O")){
            if(m.piece.color){
                boardArray[5][7].getOccupant().move(boardArray[7][7]);
                boardArray[6][7].getOccupant().move(boardArray[4][7]);
                boardArray[4][7].getOccupant().hasMoved = false;
                boardArray[7][7].getOccupant().hasMoved = false;
                boardArray[7][7].getOccupant().moves = 0;
            }
            else{
                boardArray[5][0].getOccupant().move(boardArray[7][0]);
                boardArray[6][0].getOccupant().move(boardArray[4][0]);
                boardArray[4][0].getOccupant().hasMoved = false;
                boardArray[7][0].getOccupant().hasMoved = false;
                boardArray[7][0].getOccupant().moves = 0;
            }
            m.piece.moves = 0;
            return;
        }
        if(m.castleStr.equals("O-O-O")){
            if(m.piece.color){
                boardArray[3][7].getOccupant().move(boardArray[0][7]);
                boardArray[2][7].getOccupant().move(boardArray[4][7]);
                boardArray[4][7].getOccupant().hasMoved = false;
                boardArray[0][7].getOccupant().hasMoved = false;
                boardArray[0][7].getOccupant().moves = 0;
            }
            else{
                boardArray[3][0].getOccupant().move(boardArray[0][0]);
                boardArray[2][0].getOccupant().move(boardArray[4][0]);
                boardArray[4][0].getOccupant().hasMoved = false;
                boardArray[0][0].getOccupant().hasMoved = false;
                boardArray[0][0].getOccupant().moves = 0;
            }
            m.piece.moves = 0;
            return;
        }
        
        if(m.isPassant){
            m.piece.move(m.originalPosition);
            m.piece.moves -= 2;
            Pawn p = new Pawn(boardArray[m.target.getFile()][m.piece.position.getRank()], !m.piece.color);
            p.activePos = activePositions.pop();
            activePieces[p.activePos] = p;
            p.moves = 1;
            p.hasMoved = true;
            return;
        }

        if(m.attackStr.equals("")){
            if(m instanceof PromoteMove){
                Pawn p = new Pawn(m.originalPosition, m.piece.color);
                p.hasMoved = true;
                p.moves = m.piece.moves - 1;
                m.target.setOccupant(null);
                m.target.isOccupied = false;
                // activePieces.remove(m.piece);
                p.setActivePos(activePositions.pop());
                activePieces[p.activePos] = p;
                // activePieces.add(p.activePos, p);
                return;
            }

            //System.out.println("Move 2: " + m.originalPosition.chessPosition() + " to "+m.target.chessPosition());
            m.piece.move(m.originalPosition);
            m.piece.moves -= 2;
            if(m.piece.moves == 0){
                m.piece.hasMoved = false;
            }
            return;
        }

        //else, capture move
        if(m instanceof PromoteMove){
            Pawn p = new Pawn(m.originalPosition, m.piece.color);
            p.hasMoved = true;
            p.moves = m.piece.moves - 1;
            m.target.setOccupant(null);
            m.target.isOccupied = false;
            // activePieces.remove(m.piece); //Here I manually remove m.piece because it's position in the activePieces array was alread pushed when
            //^the pawn promoted and called it's own destroy() method
            p.setActivePos(activePositions.pop()); 
            activePieces[p.activePos] = p;
            // activePieces.add(p.activePos, p);
        }
        else{
            //System.out.println("Move 3");
            m.piece.move(m.originalPosition);
            m.piece.moves -= 2;
            if(m.piece.moves == 0){
                m.piece.hasMoved = false;
            }
        }

        Piece replacement = null;
        if(m.attackStr.toLowerCase().equals("q")){
            replacement = new Queen(m.target, !m.piece.color);
        }
        else if(m.attackStr.toLowerCase().equals("r")){
            replacement = new Rook(m.target, !m.piece.color);
        }
        else if(m.attackStr.toLowerCase().equals("n")){
            replacement = new Knight(m.target, !m.piece.color);
        }
        else if(m.attackStr.toLowerCase().equals("b")){
            replacement = new Bishop(m.target, !m.piece.color);
        }
        else if(m.attackStr.toLowerCase().equals("p")){
            replacement = new Pawn(m.target, !m.piece.color);
        }
        else{
            System.out.println(m.attackStr);
        }
        replacement.moves = m.targetMoves;
        if(m.targetMoves == 0){
            replacement.hasMoved = false;
        }
        replacement.setActivePos(activePositions.pop());
        //activePieces.add(replacement.activePos, replacement);
        activePieces[replacement.activePos] = replacement;
    }


    public Move[] getMoves(boolean player){

        ArrayList<Move> moves = new ArrayList<>();

        for(int i = 0; i < activePieces.length; i++){ //TODO: find way to assign places in arraylist to pieces => check activePieces before and after a move;
            Piece p = activePieces[i];
            if(p == null){
                continue;
            }
            if(p.color == player){ //Loop through the two lists until you find a difference, and then use that as the insert slot for the piece
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

    public boolean isCheckmated(boolean player){
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

        if(player && whiteCheck){
            Move[] moves = getMoves(true);
            if(moves.length == 0){
                return true;
            }
        }
        else if(!player && blackCheck){
            Move[] moves = getMoves(false);
            if(moves.length == 0){
                return true;
            }
        }
        return false;
    }

    public boolean isDraw(boolean player){
        if(halfClock >= 50)
            return true;
        Move[] moves = getMoves(player);
        if(moves.length == 0){
            return true;
        }
        return false;
    }

    
    public String winCondition(){
        boolean isMated = isCheckmated(playerTurn);
        if(isMated){
            gameLog.get(gameLog.size()-1).checkStr = "#";
            return playerTurn ? "White has been checkmated" : "Black has been checkmated";
        }

        boolean draw = isDraw(playerTurn);
        if(draw){
            if(halfClock >= 50)
                return "Draw by fifty move rule";
            return "Draw by stalemate";
        }

        return "Game in Progress";
    }
    

    public Board copy(){
        Board b = new Board(); 
        for (int file = 0;file<8;file++){
            for(int rank=0;rank<8;rank++){
                if(boardArray[file][rank].isOccupied){
                    Piece p2 = boardArray[file][rank].getOccupant().copy(b);
                    Square s = p2.position;
                    b.boardArray[file][rank] = s;
                }
            }
        }

        for(Move tempMove : gameLog){
            if(tempMove != null){
                b.gameLog.add(tempMove.copy(b));
            }
        }

        return b;
    }


    //For a standard game of chess. Future variants may include chess960 or other stuff idk
    public void standardSetup(){
        //Instatiate rooks
        Rook r3 = new Rook(boardArray[0][0], false);
        Rook r4 = new Rook(boardArray[7][0], false);
        Rook r1 = new Rook(boardArray[0][7], true);
        Rook r2 = new Rook(boardArray[7][7], true);

        //Instatiate knights
        Knight n4 = new Knight(boardArray[1][0], false);
        Knight n3 = new Knight(boardArray[6][0], false);
        Knight n1 = new Knight(boardArray[1][7], true);
        Knight n2 = new Knight(boardArray[6][7], true);

        //Instatiate bishops
        Bishop b3 = new Bishop(boardArray[2][0], false);
        Bishop b4 = new Bishop(boardArray[5][0], false);
        Bishop b1 = new Bishop(boardArray[2][7], true);
        Bishop b2 = new Bishop(boardArray[5][7], true);

        //Instatiate Queens
        Queen q2 = new Queen(boardArray[3][0], false);
        Queen q1 = new Queen(boardArray[3][7], true);

        //Instantiate Kings
        King k1 = new King(boardArray[4][7], true);
        King k2 = new King(boardArray[4][0], false);
        kings[0] = k1; //white
        kings[1] = k2; //black

        //activePieces.addAll(Arrays.asList(new Piece[] {r1, r2, r3, r4, n1, n2, n3, n4, b1, b2, b3, b4, q1, q2, k1, k2}));
        activePieces[0] = k1;
        activePieces[1] = q1;
        activePieces[2] = r1;
        activePieces[3] = r2;
        activePieces[4] = b1;
        activePieces[5] = b2;
        activePieces[6] = n1;
        activePieces[7] = n2;

        activePieces[16] = k2;
        activePieces[17] = q2;
        activePieces[18] = r3;
        activePieces[19] = r4;
        activePieces[20] = b3;
        activePieces[21] = b4;
        activePieces[22] = n3;
        activePieces[23] = n4;
        

        int pos = 8;
        for (int i = 0;i<8;i++){
            Pawn p = new Pawn(boardArray[i][1], false);
            // activePieces.add(p);
            activePieces[pos] = p;
            pos++;
        }
        pos = 24;
        for (int i = 0;i<8;i++){
            Pawn p = new Pawn(boardArray[i][6], true);
            // activePieces.add(p);
            activePieces[pos] = p;
            pos++;
        }

        for(int i = 0; i < activePieces.length; i++){
            // activePieces.get(i).setActivePos(i);
            activePieces[i].setActivePos(i);
        }
    }

    public void loadFEN(String fen){
        String[] fenlis = fen.split(" ");
        String[] cols = fenlis[0].split("/");

        //fenlis[0] => piece positions
        int pos = 0;
        for(int rank = 0; rank < 8; rank++){
            int file = 0;
            for(int f = 0; f < cols[rank].length(); f++){
                char c = cols[rank].charAt(f);
                if(Character.isDigit(c)){
                    file += Integer.parseInt(""+c);
                }
                else if(Character.isUpperCase(c)){
                    Piece p;
                    if(c == 'R'){
                        p = new Rook(boardArray[file][rank], true);
                    }
                    else if(c == 'B'){
                        p = new Bishop(boardArray[file][rank], true);
                    }
                    else if(c == 'N'){
                        p = new Knight(boardArray[file][rank], true);
                    }
                    else if(c == 'Q'){
                        p = new Queen(boardArray[file][rank], true);
                    }
                    else if(c == 'K'){
                        p = new King(boardArray[file][rank], true);
                        kings[0] = (King)p;
                    }
                    else{
                        p = new Pawn(boardArray[file][rank], true);
                    }
                    // activePieces.add(p);
                    activePieces[pos] = p;
                    pos++;
                    file += 1;
                }
                else{
                    Piece p;
                    if(c == 'r'){
                        p = new Rook(boardArray[file][rank], false);
                    }
                    else if(c == 'b'){
                        p = new Bishop(boardArray[file][rank], false);
                    }
                    else if(c == 'n'){
                        p = new Knight(boardArray[file][rank], false);
                    }
                    else if(c == 'q'){
                        p = new Queen(boardArray[file][rank], false);
                    }
                    else if(c == 'k'){
                        p = new King(boardArray[file][rank], false);
                        kings[1] = (King)p;
                    }
                    else{
                        p = new Pawn(boardArray[file][rank], false);
                    }
                    // activePieces.add(p);
                    activePieces[pos] = p;
                    pos++;
                    file += 1;
                }
            }
        }

        //fenlis[1] => playyer turn
        playerTurn = fenlis[1].equals("w"); //if w, then turn = true (white); else, turn = false (black)

        //fenlis[2] => castling rights
        for(Piece p : activePieces){
            if(p instanceof King || p instanceof Rook){
                p.hasMoved = true;
            }
        }

        for(int i = 0; i < fenlis[2].length(); i++){
            char c = fenlis[2].charAt(i);
            if(c == '-'){
                //do nothing; pieces are already set to not be able to castle
            }
            else if(Character.isUpperCase(c)){
                int rookspace = c == 'K' ? 7 : 0; //if 'K', can kingside castle; else, c == 'Q' means queenside castle
                for(Piece p : activePieces){
                    if(p instanceof King){
                        p.hasMoved = false;
                    }
                    if(p instanceof Rook && p.position.getFile() == rookspace && p.position.getRank() == 7){
                        p.hasMoved = false;
                    }
                }
            }
            else{
                int rookspace = c == 'k' ? 7 : 0;
                for(Piece p : activePieces){
                    if(p instanceof King){
                        p.hasMoved = false;
                    }
                    if(p instanceof Rook && p.position.getFile() == rookspace && p.position.getRank() == 0){
                        p.hasMoved = false;
                    }
                }
            }
        }

        //fenlis[3] => en passant spaces
        if(fenlis[3].charAt(0) != '-'){
            int file = 0;
            for(char c = 'a'; c != fenlis[3].charAt(0); c++){
                file++;
            }
            int rank = 8 - Integer.parseInt(""+fenlis[3].charAt(1));

            int direction = rank == 2 ? 1 : -1;
            Piece p = boardArray[file][rank+direction].getOccupant();
            p.hasMoved = true;
            p.moves = 1;
            Move m = new Move(p, boardArray[file][rank+direction]);
            m.originalPosition = boardArray[file][rank-direction];
            gameLog.add(m);
        }

        //fenlis[4] => halfmove clock
        halfClock = Integer.parseInt(fenlis[4]);

        //fenlis[5] => fullmove clock
        fullClock = Integer.parseInt(fenlis[5]);

        //Set active positions
        for(int i = 0; i < activePieces.length; i++){
            if(activePieces[i] != null)
                activePieces[i].setActivePos(i);
        }
    }
}
