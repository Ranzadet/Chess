package Chess;
import java.util.Scanner;

public class Game {
    private static Board board;
    private static boolean turn = true;


    public static void main(String[] args) {
        board = new Board();
        board.standardSetup();
        board.printBoardState();
        Scanner scanner = new Scanner(System.in);

        while (board.winCondition().equals("Game in Progress")){
            String moveStr = scanner.nextLine();
            Move m = new Move(moveStr, board);
            board.move(m);
            board.printBoardState();
        }

        System.out.println(board.winCondition());
        board.printGameLog();
        
    }
}
