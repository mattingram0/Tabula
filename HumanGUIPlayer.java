import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.System.out;


public class HumanGUIPlayer  implements PlayerInterface {

    GUI g;

    public HumanGUIPlayer(){
        g = new GUI();
    }

    public TurnInterface getTurn(Colour colour, BoardInterface board, List<Integer> diceValues) throws PauseException{

        Board testBoard = (Board)board.clone();
        List<Integer> diceCopy = new ArrayList<>();
        diceCopy.addAll(diceValues);

        setup(colour, board, diceValues);
        boolean turnOver = false;

        JFrame mainFrame = new JFrame("Human GUI Player");

        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                // Do something
            }
        });

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setContentPane(g.getMainPanel());
        mainFrame.setSize(780, 540);
        mainFrame.setVisible(true);


        while(!turnOver){
            if(g.hasTakenTurn()){
                if(g.getTurn() != null){
                    try {
                        testBoard.takeTurn(colour, g.getTurn(), diceCopy);
                    } catch (IllegalTurnException e) {
                        ErrorBox eb = new ErrorBox();
                        eb.setErrorMessage(e.getMessage());

                        JFrame errorFrame = new JFrame("Error Message");

                        errorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        errorFrame.setContentPane(eb.getMainPanel());
                        errorFrame.setSize(500, 500);
                        errorFrame.setVisible(true);
                        boolean errorClosed = false;

                        while(!errorClosed){
                            if(eb.isContinueButtonClicked()){
                                errorFrame.dispose();
                                errorClosed = true;
                            }
                        }
                    }
                }

                turnOver = true;
            }
        }

        mainFrame.dispose();
        return g.getTurn();
    }

    public void setup(Colour colour, BoardInterface board, List<Integer> diceValues){
        //Populate locations
        ArrayList<JPanel> guiLocations = g.getLocations();
        List<Location> boardLocations = ((Board)board).getAllLocations();

        for(int i = 0; i < Board.TOTAL_LOCATIONS; i++){
            Component components[] = guiLocations.get(i).getComponents();

            if(boardLocations.get(i).getPieces().containsKey(Colour.values()[0])){
                int greenPieces = boardLocations.get(i).getPieces().get(Colour.values()[0]);
                String greenPiecesString;
                JLabel label = ((JLabel) components[0]);

                if(greenPieces < 10){
                    greenPiecesString = " 0" + Integer.toString(greenPieces);
                } else {
                    greenPiecesString = Integer.toString(greenPieces);
                }

                label.setText(greenPiecesString);

                if(colour.equals(Colour.GREEN) && Integer.parseInt(label.getText().replaceAll("\\s+", "")) > 0){
                    label.setFont(new Font(label.getFont().getName(), Font.BOLD, 18));
                }
            }

            if(boardLocations.get(i).getPieces().containsKey(Colour.values()[1])){
                int bluePieces = boardLocations.get(i).getPieces().get(Colour.values()[1]);
                String bluePiecesString;
                JLabel label = ((JLabel) components[1]);

                if(bluePieces < 10){
                    bluePiecesString = " 0" + Integer.toString(bluePieces);
                } else {
                    bluePiecesString = Integer.toString(bluePieces);
                }

                label.setText(bluePiecesString);

                if(colour.equals(Colour.BLUE) && Integer.parseInt(label.getText().replaceAll("\\s+", "")) > 0){
                    label.setFont(new Font(label.getFont().getName(), Font.BOLD, 18));
                }
            }
        }

        //Populate dice
        ArrayList<JLabel> dice = g.getDice();

        if(diceValues.size() == 2){
            dice.get(0).setText("");
            dice.get(1).setText(Integer.toString(diceValues.get(0)));
            dice.get(2).setText(Integer.toString(diceValues.get(1)));
            dice.get(3).setText("");
        } else {
            dice.get(0).setText(Integer.toString(diceValues.get(0)));
            dice.get(1).setText(Integer.toString(diceValues.get(1)));
            dice.get(2).setText(Integer.toString(diceValues.get(2)));
            dice.get(3).setText(Integer.toString(diceValues.get(3)));
        }

        //Set player turn label
        g.setPlayerTurnLabel(colour);

    }
}
