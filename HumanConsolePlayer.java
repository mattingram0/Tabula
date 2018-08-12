import java.util.*;

import static java.lang.System.out;

/**
 * Created by matt on 22/03/2017.
 */
public class HumanConsolePlayer implements PlayerInterface {

    public HumanConsolePlayer(){

    }

    /**
     * Get from the player the turn that they wish to take
     *
     * @param colour the Colour they are playing as
     *
     * @param board a clone of the current board state, so that the player can try different moves
     *
     * @param diceValues a list of the dice values the player can use.
     *
     * @return the turn the player wishes to take. It is the Player's responsibility to ensure that the turn is legal, matches the provided diceValues and uses as may of the diceValues as possible.
     *
     * @throws PauseException is only used by human players if they are in the middle of a game and wish to pause the game instead of taking a turn.
     **/
    public TurnInterface getTurn(Colour colour, BoardInterface board, List<Integer> diceValues) throws PauseException{

        TurnInterface turn = new Turn();

        out.println("[*] " + colour.toString() + " Player's Turn");
        out.println();
        out.println(board.toString());

        ((Board) board).displayDice(diceValues);
        out.println();

        out.println("[*] Would you Like To Test a Turn? Y - Yes, N - No, P - Pause Game");

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        boolean trialUsed = false;

        while(!(input.equals("Y") || input.equals("y") || input.equals("N") || input.equals("n") || input.equals("P") || input.equals("p"))) {
            System.out.println("[-] Invalid Choice! Please Choose Again");
            input = scanner.nextLine();
        }

        out.println();

        if(input.equals("P") || input.equals("p")){
            out.println("[+] Pausing Game! Returning to Main Menu");
            throw new PauseException("User Requested To Pause The Game");
        } else if (input.equals("Y") || input.equals("y")){

            trialUsed = true;
            String anotherMove = "";
            int moveCounter = 0;

            boolean firstTurn = true;
            boolean firstMove = true;

            String anotherTurn = "";
            TurnInterface tempTurn = new Turn();
            BoardInterface trialBoard = board.clone();
            List<Integer> diceCopy = new ArrayList<Integer>();
            diceCopy.addAll(diceValues);
            Set<MoveInterface> possibleMoves;

            while(!(anotherTurn.equals("A") || anotherTurn.equals("a"))){

                if(firstTurn){
                    out.println("[*] Please Enter a Move in The Format (Without Brackets): [Starting Location]:[Number of Places to Move]");
                    out.println(" Starting Location - " + Integer.toString(Board.START_LOCATION) + " | Finish Location - " + Integer.toString(Board.END_LOCATION) + " | Knocked-Off Location - " + Integer.toString(Board.KNOCKED_LOCATION));
                    firstTurn = false;
                    firstMove = false;
                } else {
                    if (firstMove) {
                        out.println("[*] Please Enter Your First Move");
                        firstMove = false;
                    } else {
                        out.println("[*] Please Enter Another Move");
                    }
                }

                input = scanner.nextLine();
                Move m = parseMove(input);
                possibleMoves = trialBoard.possibleMoves(colour, diceCopy);

                if (m != null) {
                    try {

                        //MODULARISE/TEST THIS

                        if(trialBoard.getKnockedLocation().canRemovePiece(colour) && m.getSourceLocation() != Board.KNOCKED_LOCATION){ //If there are pieces in the knocked off location of the given colour, they have to be moved first
                            throw new IllegalMoveException("[-] All Knocked Off Pieces Must be Moved onto the Board First!");
                        }

                        if(possibleMoves.contains(m)){
                            trialBoard.makeMove(colour, m);

                            out.println();
                            out.println("[+] Move Executed Successfully. Displaying Board After Move:");
                            out.println();

                            try {
                                tempTurn.addMove(m);
                            } catch (IllegalTurnException e) {
                                out.println(e.getMessage());
                            }

                            diceCopy.remove((Integer) m.getDiceValue()); //Remove the dice value just used in the move from the available dice values

                            out.println(trialBoard.toString());
                            ((Board) trialBoard).displayDice(diceCopy);
                            out.println();

                            moveCounter++;
                        } else {
                            out.println();
                            out.println("[-] Attempted Move Is Not Legal, Move Not Executed");
                        }

                    } catch (IllegalMoveException e) {
                        out.println(e.getMessage());
                    }
                }

                if(moveCounter < diceValues.size()){ //If he's not reached the maximum number of moves in his turn, ask if he wants to end his turn early
                    out.println("[*] Do You Want To Trial Another Move in this Turn? (Y/N)");

                    input = scanner.nextLine();

                    while(!(input.equals("Y") || input.equals("y") || input.equals("N") || input.equals("n"))) {
                        System.out.println("[-] Invalid Choice! Please Choose Again");
                        input = scanner.nextLine();
                    }

                    if(input.equals("N") || input.equals("n")){
                        moveCounter = diceValues.size(); //Ensure the next block will execute
                    }
                    out.println();
                }

                if(moveCounter == diceValues.size()){
                    out.println("[*] End of Moves in Turn. Would You Like To Use This Trial Turn As Your Actual Turn? (Y/N)");
                    input = scanner.nextLine();

                    while(!(input.equals("Y") || input.equals("y") || input.equals("N") || input.equals("n"))) {
                        out.println();
                        System.out.println("[-] Invalid Choice! Please Choose Again");
                        input = scanner.nextLine();
                    }

                    out.println();

                    if (input.equals("Y") || input.equals("y")){
                        return tempTurn;
                    } else {
                        out.println("[*] Do You Want To Trial Another Turn, Or Input Actual Turn? T - Trial, A - Actual");

                        anotherTurn = scanner.nextLine();

                        while(!(anotherTurn.equals("T") || anotherTurn.equals("t") || anotherTurn.equals("A") || anotherTurn.equals("a"))) {
                            out.println();
                            System.out.println("[-] Invalid Choice! Please Choose Again");
                            anotherTurn = scanner.nextLine();
                        }

                        if(anotherTurn.equals("T") || anotherTurn.equals("t")){
                            out.println();
                            out.println("[+] Beginning a New Trial Turn! ");
                            out.println();
                            out.println(board.toString());
                            ((Board) board).displayDice(diceValues);
                            out.println();
                            firstMove = true;
                            moveCounter = 0;
                        }

                        //The while loop will take care of trialling another turn or inputting actual turn decision

                        trialBoard = board.clone();
                        diceCopy.clear();
                        diceCopy.addAll(diceValues); //may need to re-add firstTurn/firstMove = true TEST
                        tempTurn = new Turn();
                    }
                }
            }

            /** while(!(anotherMove.equals("N") || anotherMove.equals("n")) || (moveCounter < diceValues.size())) {

                if(moveCounter == 0){
                    out.println("[*] Please Enter a Move in The Format (Without Brackets): [Starting Location]:[Number of Places to Move]");
                    out.println(" Starting Location - " + Integer.toString(Board.START_LOCATION) + " | Finish Location - " + Integer.toString(Board.END_LOCATION) + " | Knocked-Off Location - " + Integer.toString(Board.KNOCKED_LOCATION));
                    out.println();
                } else {
                    out.println("[*] Please Enter another Move");
                }

                input = scanner.nextLine();
                Move m = parseMove(input);

                if (m != null) {
                    try {
                        board.makeMove(colour, m);

                        out.println("[+] Move Executed Successfully. Displaying Board After Move:");
                        out.println();

                        out.println(board.toString());

                        out.println();
                        out.println("[*] Would You Like to Add this Move to Your Turn? (Y/N)");

                        input = scanner.nextLine();

                        while(!(input.equals("Y") || input.equals("y") || input.equals("N") || input.equals("n"))) {
                            out.println("[-] Invalid Choice! Please Choose Again");
                            input = scanner.nextLine();
                        }

                        if(input.equals("Y") || input.equals("y")){
                            try {
                                turn.addMove(m);
                            } catch (IllegalTurnException e) {
                                out.println(e.getMessage());
                            }
                        }

                        moveCounter++;

                    } catch (IllegalMoveException e) {
                        out.println(e.getMessage());
                    }
                }

                if(moveCounter == (diceValues.size()-1)){ //Buf Fix - Ensures you're not asked for another move if you've already inputted the most
                    continue;
                }

                out.println();
                out.println("[*] Would You Like to Try Another Move? (Y/N)");
                out.println();

                anotherMove = scanner.nextLine();

                while(!(anotherMove.equals("Y") || anotherMove.equals("y") || anotherMove.equals("N") || anotherMove.equals("n"))){
                    System.out.println("[-] Invalid Choice! Please Choose Again");
                    anotherMove = scanner.nextLine();
                }
            }*/
        }

        String anotherMove = "";
        int numberOfMoves = 0;

        while(!(anotherMove.equals("N") || anotherMove.equals("n")) && (numberOfMoves < diceValues.size())){
            if(!trialUsed && numberOfMoves == 0){
                out.println("[*] Please Enter a Move in The Format (Without Brackets): [Starting Location]:[Number of Places to Move]");
                out.println(" Starting Location - " + Integer.toString(Board.START_LOCATION) + " | Finish Location - " + Integer.toString(Board.END_LOCATION) + " | Knocked-Off Location - " + Integer.toString(Board.KNOCKED_LOCATION));
            } else {
                if(numberOfMoves == 0) {
                    out.println();
                    out.println("[*] Please Enter Your First Move");
                } else {
                    out.println("[*] Please Enter Another Move");
                }
            }

            input = scanner.nextLine();
            Move m = parseMove(input);

            while(m == null){
                out.println("[*] Please Try Enter Move Again");
                input = scanner.nextLine();
                m = parseMove(input);
            }

            try{
                turn.addMove(m);
                numberOfMoves++;
            } catch(IllegalTurnException e){
                out.println(e.getMessage());
            }

            if(numberOfMoves < diceValues.size()) {
                out.println();
                out.println("[*] Would You Like to Add Another Move? (Y/N)");
            } else {
                out.println();
                out.println("[+] End of Moves in Turn");
                break;
            }

            anotherMove = scanner.nextLine();
            out.println();

            while(!(anotherMove.equals("Y") || anotherMove.equals("y") || anotherMove.equals("N") || anotherMove.equals("n"))){
                System.out.println("[-] Invalid Choice! Please Choose Again");
                anotherMove = scanner.nextLine();
                out.println();
            }
        }

        return turn;
    }

    public Move parseMove (String input){
        if(input.trim().matches("\\d{1,2}:\\d{1,2}")){
            String[] numbers = input.split(":");
            int source = Integer.parseInt(numbers[0]);
            int diceValue = Integer.parseInt(numbers[1]);

            Move m = new Move();

            try{
                m.setSourceLocation(source);
                m.setDiceValue(diceValue);
            } catch(NoSuchLocationException e){
                out.println();
                out.println("[-] Invalid Starting Location, Move Not Executed");
                return null;
            } catch(IllegalMoveException e){
                out.println();
                out.println("[-] Invalid Number of Places to Move, Move Not Executed");
                return null;
            }

            return m;
        } else {
            out.println();
            out.println("[-] Invalid Input Format");
            return null;
        }
    }
}
