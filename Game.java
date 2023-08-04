package Chess;
import java.util.Scanner;

public class Game {
    private static Board board;
    private static boolean turn = true;


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        board = new Board();

        String configuration = scanner.nextLine();
        String fenStr = "";
        if(configuration.toUpperCase().equals("FEN")){
            fenStr = scanner.nextLine();
            board.loadFEN(fenStr);
        }
        else{
            board.standardSetup();
        }
        board.printBoardState();

        boolean interrupted = false;
        while (board.winCondition().equals("Game in Progress")){
            String moveStr = scanner.nextLine();
            if(moveStr.equals("draw") || moveStr.equals("ff") || moveStr.equals("resign")){
                interrupted = true;
                break;
            }
            Move m = new Move(moveStr, board);
            board.move(m);
            board.printBoardState();
            board.unMove(m);
            board.printBoardState();
        }

        if(!interrupted)
            System.out.println("\n"+board.winCondition());
        System.out.println("\n----------Printing Game Log----------");
        board.printGameLog();
        
    }
}
