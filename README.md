# Tabula
A command line based implecounterstation of the board game Tabula, with slightly modified rules to the original Roman game. The game is very similar in play to backgammon, but simpler. 

## Prerequisites 
To run this game, the latest version of **Java** needs to be installed. 

## Rules 
### The Board
* A real-life tabula board consisted of twenty four rectangles aligned in two opposing rows of twelve. In fact, the same form as a modern Backgammon board - but instead of points, lines divide up the board into twelve sections on either side. 
* In this version of the game, the board is split into 24 rectangular locations, arranged in a rectangle. There are three other locations - the start location where all 15 of both players' counters are held initially, the end location where counters enter after moving successfully around the whole board, and the 'knocked off' location where counters are moved to if they are 'knocked off' the board.
* The initial board is as follows: ![Initial Board](https://github.com/mattingram0/Tabula/blob/master/images/initial.png)As is shown, each location is represented by a pair of numbers, enclosed within square brackets and seperated by a vertical line. The left hand number represents the number of the GREEN player's (player 1's) counters on that location, and the right hand number represents the number of the BLUE player's (player 2's) counters on that location.
### The Play
* Beneath the board the two dice are shown. If a double is scored, the player receives four dice of that same number to play with. The numbers on the dice represent the number of spaces the player can move. The player can choose to move one counter twice, or two counters once each. In the case of a double, the moves can be allocated between one, two, three, or four counters. The player must use as many of the dice moves as possible. 
* The player may not move part of a dice's value - for example, if a 3 and a 5 is rolled, the player may not move two counters by 6 and 2, they must move one counter by 3 then 5 (or vice versa) or two counters by 3 and 5. Any part of the dice that cannot be used is abondoned. Players cannot move counters backwards.
* If a player has two counters on the same point, it is safe from attack and an opposing piece cannot move onto that point.  If a single piece occupies a point and a piece from the opposing player lands upon the same point, the single piece is knocked off the board and placed in the 'knocked off' location.
* If a player has counters in the 'knocked off' location, he must move these back on to the board before he can move any other piece.
* To move a piece into the end location, the player must role a number greater than or equal to the piece's distance to the end location (location 25). Once a piece has reached the end location it is safe. The winner is the player who manages to get all of his counters into the end location first. 
### The Interface
The user interface of the game is hopefully intuitive and self-explanatory. Before beginning a game, please select the Help option from the main menu for a quick overview of some of the features of the program - including trialling turns, and saving games.

## Installation 
Click 'Clone or download' above and then 'Download ZIP', or alternatively run the following command from the command line:

``` 
git clone https://github.com/mattingram0/Tabula.git 
``` 

Then to compile then game, please change directory to the extracted folder holding the source code and run the following command:

```
javac *.java
```

## Running 
Finally, to run and begin playing the game, enter the following command at the command line:

```
java Game
```

Once the program has begun, there are several options. You can play player vs player, player vs computer, or even computer vs computer if that sort of thing takes your fancy. Once the game has begun, there is the option to pause the game, and then save the game state to an external file so that it can be loaded and resumed at a later date.

Enjoy!

## Screenshot
The splash screen: ![Splash Screen](https://github.com/mattingram0/Tabula/blob/master/images/splash.png).
