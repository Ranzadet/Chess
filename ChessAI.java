package Chess;

import java.util.ArrayList;

public class ChessAI {
    private boolean turn;
    private ArrayList<Move> movePath;
    private int initialDepth;
    private Board board;

    public ChessAI(Board b, int initialDepth){
        board = b;
        turn = b.getPlayer();
        this.initialDepth = initialDepth;
    }

    public Move minimax(){
        
        return null;
    }

    public Move minimax(int depth){
        if(depth == 0){
            return null;
        }
        return null;
    }

    private int minimax(int depth, double alpha, double beta){
        Move[] moves = b.getMoves(b.getPlayer());
        if(moves.length == 0){
            for(Piece p : b.activePieces){
                if(p instanceof King && p.color == b.getPlayer() && ((King)p).inCheck){
                    return b.getPlayer() ? Integer.MIN_VALUE : Integer.MAX_VALUE;
                }
            }
            return 0; //Draw
        }
        if(depth == 0){
            return evaluate(b);
        }

        int movepos = -1;
        if(board.getPlayer()){
            int maxEval = Integer.MIN_VALUE;
            for(int i = 0; i < moves.length; i++){
                board.move(moves[i]);
                int eval = minimax(depth-1, alpha, beta);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if(beta <= alpha)
                    return maxEval;
            }
            return maxEval;
        }
        else{
            int minEval = Integer.MAX_VALUE;
            for(int i = 0; i < moves.length; i++){
                board.move(moves[i]);
                int eval = minimax(depth-1, alpha, beta);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if(beta <= alpha)
                    return minEval;
            }
            return minEval;
        }
    }

    public int evaluate(Board b){
        int score = 0;
        for (Piece p : b.activePieces){
            score += p.color ? p.value : -p.value;
        }
        return score;
    }

    public int moveGenerationTest(int depth){
        if(depth == 0){
            return 1;
        }

        Move[] moves = board.getMoves(board.getPlayer());
        int positions = 0;

        for(Move m : moves){

        }
    }
}
