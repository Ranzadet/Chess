package Chess;
import java.util.Scanner;

public class Game {
    private static Board board;
    private static boolean turn = true;


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        board = new Board();
        board.standardSetup();
        board.printBoardState();
        

        boolean interrupted = false;
        Move m = null;
        while (board.winCondition().equals("Game in Progress")){
            String moveStr = scanner.nextLine();
            if(moveStr.equals("draw") || moveStr.equals("ff") || moveStr.equals("resign")){
                interrupted = true;
                break;
            }
            else if(moveStr.toLowerCase().equals("fen") || moveStr.toLowerCase().equals("load") || moveStr.toLowerCase().equals("load fen")){
                String fenStr = "";
                fenStr = scanner.nextLine();
                board = new Board();
                board.loadFEN(fenStr);
                board.printBoardState();
            }
            else if(moveStr.equals("unmove")){
                board.unMove(m);
                board.printBoardState();
            }
            else{
                m = new Move(moveStr, board);
                board.move(m);
                board.printBoardState();
            }
            
        }

        if(!interrupted)
            System.out.println("\n"+board.winCondition());
        System.out.println("\n----------Printing Game Log----------");
        board.printGameLog();
        
    }
}
