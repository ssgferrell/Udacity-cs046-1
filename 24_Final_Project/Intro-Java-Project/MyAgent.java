import java.util.Random;

public class MyAgent extends Agent
{
    Random r;

    /**
     * Constructs a new agent, giving it the game and telling it whether it is Red or Yellow.
     * 
     * @param game The game the agent will be playing.
     * @param iAmRed True if the agent is Red, False if the agent is Yellow.
     */
    public MyAgent(Connect4Game game, boolean iAmRed)
    {
        super(game, iAmRed);
        r = new Random();
    }

    /**
     * The move method is run every time it is this agent's turn in the game. You may assume that
     * when move() is called, the game has at least one open slot for a token, and the game has not
     * already been won.
     * 
     * By the end of the move method, the agent should have placed one token into the game at some
     * point.
     * 
     * After the move() method is called, the game engine will check to make sure the move was
     * valid. A move might be invalid if:
     * - No token was place into the game.
     * - More than one token was placed into the game.
     * - A previous token was removed from the game.
     * - The color of a previous token was changed.
     * - There are empty spaces below where the token was placed.
     * 
     * If an invalid move is made, the game engine will announce it and the game will be ended.
     * 
     */
    public void move()
    {
        int columnIndex = iCanWin();
        if (columnIndex >= 0) {
            moveOnColumn(columnIndex);
        }
        else {
            columnIndex = theyCanWin();
            if (columnIndex >= 0) {
                moveOnColumn(columnIndex);
            } 
            else {
                columnIndex = iCanConnectTheMost();
                if (columnIndex >= 0) {
                moveOnColumn(columnIndex);                    
                }
                else {
                    moveFromMiddle();
                }
            }
        }
    }

    /**
     * Attempts to drop a token from the middle column of the game panel.
     * If all the columns are full, nothing will change.
     */
    public void moveFromMiddle()
    {
        int nbrColumns = myGame.getColumnCount();
        for (int i = 0; i <= nbrColumns/2; i++) {
            int lowestEmptySlotIndex = getLowestEmptyIndex(myGame.getColumn(nbrColumns/2 + i)); 
            if (lowestEmptySlotIndex != -1) {
                moveOnColumn(nbrColumns/2 + i);
                break;
            }
            if (i > 0) {
                lowestEmptySlotIndex = getLowestEmptyIndex(myGame.getColumn(nbrColumns/2 - i)); 
                if (lowestEmptySlotIndex != -1) {
                    moveOnColumn(nbrColumns/2 - i);
                    break;
                }
            }
        }        
    }
    
    /**
     * Drops a token into a particular column so that it will fall to the bottom of the column.
     * If the column is already full, nothing will change.
     * 
     * @param columnNumber The column into which to drop the token.
     */
    public void moveOnColumn(int columnNumber)
    {
        int lowestEmptySlotIndex = getLowestEmptyIndex(myGame.getColumn(columnNumber));   // Find the top empty slot in the column
                                                                                                  // If the column is full, lowestEmptySlot will be -1
        if (lowestEmptySlotIndex > -1)  // if the column is not full
        {
            Connect4Slot lowestEmptySlot = myGame.getColumn(columnNumber).getSlot(lowestEmptySlotIndex);  // get the slot in this column at this index
            if (iAmRed) // If the current agent is the Red player...
            {
                lowestEmptySlot.addRed(); // Place a red token into the empty slot
            }
            else // If the current agent is the Yellow player (not the Red player)...
            {
                lowestEmptySlot.addYellow(); // Place a yellow token into the empty slot
            }
        }
    }

    /**
     * Returns the index of the top empty slot in a particular column.
     * 
     * @param column The column to check.
     * @return the index of the top empty slot in a particular column; -1 if the column is already full.
     */
    public int getLowestEmptyIndex(Connect4Column column) {
        int lowestEmptySlot = -1;
        for  (int i = 0; i < column.getRowCount(); i++)
        {
            if (!column.getSlot(i).getIsFilled())
            {
                lowestEmptySlot = i;
            }
        }
        return lowestEmptySlot;
    }

    /**
     * Returns a random valid move. If your agent doesn't know what to do, making a random move
     * can allow the game to go on anyway.
     * 
     * @return a random valid move.
     */
    public int randomMove()
    {
        int i = r.nextInt(myGame.getColumnCount());
        while (getLowestEmptyIndex(myGame.getColumn(i)) == -1)
        {
            i = r.nextInt(myGame.getColumnCount());
        }
        return i;
    }

    /**
     * Returns the column that would allow the agent to win.
     * 
     * You might want your agent to check to see if it has a winning move available to it so that
     * it can go ahead and make that move. Implement this method to return what column would
     * allow the agent to win.
     *
     * @return the column that would allow the agent to win.
     */
    public int iCanWin()
    {
         return canWin(iAmRed);
    }
    
    /**
     * Returns the column that would allow the agent to win
     * @param redPlayer true if the agent is Red, false if the agent is Yellow.
     * @return the column that would allow the agent to win, -1 if none.
     */
    public int canWin(boolean redPlayer)
    {
        //Find out myColor; this.iAmREd
        int winningColumn = -1;
        for (int i = 0; i < myGame.getColumnCount(); i++) 
        {
            int slotIndex = getLowestEmptyIndex(myGame.getColumn(i));
            if ( slotIndex != -1 )  // if column is not full
                {
                    if ( canWinSlot(i, slotIndex, redPlayer) )  // if we can connect 4 slots 
                    {
                        winningColumn = i;
                        break;
                    }
                }
        }
        return winningColumn; 
    }

    /** 
     * Returns whether the given slot at the given column would allow to win
     * @param columnIndex index of the column the slot belongs to.
     * @param slotIndex index of the given slot 
     * @return true if they would allow the agent to win, else false.
     */
    public boolean canWinSlot(int columnIndex, int slotIndex, boolean redPlayer) 
    {
        int nbrColumns = myGame.getColumnCount();
        int nbrRows = myGame.getRowCount();
        
        if ( !((0 <= columnIndex && columnIndex < nbrColumns) && (0 <= slotIndex && slotIndex < nbrRows)) )
            return false;
        
        // Check horizontally and left side
        int nbrConnectedSlots = 1;
        for (int x = columnIndex - 1; 0 <= x; x--) {
            Connect4Slot slot = myGame.getColumn(x).getSlot(slotIndex);
            if (slot.getIsFilled() && slot.getIsRed() == redPlayer) {
                nbrConnectedSlots ++;
                if (nbrConnectedSlots == 4) {
                    return true;
                }
            }
            else break;
        }
        // Continue to check horizontally and right side
        for (int x = columnIndex + 1; x < nbrColumns; x++) {
            Connect4Slot slot = myGame.getColumn(x).getSlot(slotIndex);
            if (slot.getIsFilled() && slot.getIsRed() == redPlayer) {
                nbrConnectedSlots ++;
                if (nbrConnectedSlots == 4) {
                    return true;
                }
            }
            else break;
        }           
        
        // Check vertically and down side
        nbrConnectedSlots = 1;
        for (int y = slotIndex + 1; y < nbrRows; y++) {
            Connect4Slot slot = myGame.getColumn(columnIndex).getSlot(y);
            if (slot.getIsFilled() && slot.getIsRed() == redPlayer) {
                nbrConnectedSlots ++;
                if (nbrConnectedSlots == 4) {
                    return true;
                }
            }
            else break;
        } 
        // Continue to check vertically and up side
        for (int y = slotIndex - 1; 0 <= y; y--) {
            Connect4Slot slot = myGame.getColumn(columnIndex).getSlot(y);
            if (slot.getIsFilled() && slot.getIsRed() == redPlayer) {
                nbrConnectedSlots ++;
                if (nbrConnectedSlots == 4) {
                    return true;
                }
            }
            else break;
        }           
        
        // Check diagonally left side and up side
        nbrConnectedSlots = 1;
        int x = columnIndex - 1;
        int y = slotIndex - 1;
        while (0 <= x && 0 <= y) {
            Connect4Slot slot = myGame.getColumn(x).getSlot(y);
            if (slot.getIsFilled() && slot.getIsRed() == redPlayer) {
                nbrConnectedSlots ++;
                if (nbrConnectedSlots == 4) {
                    return true;
                }
            }
            else break;
            x--;
            y--;
        }
        // Continue to check diagonally right side and down side
        x = columnIndex + 1;
        y = slotIndex + 1;
        while (x < nbrColumns && y < nbrRows) {
            Connect4Slot slot = myGame.getColumn(x).getSlot(y);
            if (slot.getIsFilled() && slot.getIsRed() == redPlayer) {
                nbrConnectedSlots ++;
                if (nbrConnectedSlots == 4) {
                    return true;
                }
            }
            else break; 
            x++;
            y++;
        }

        // Check diagonally left side and down side
        nbrConnectedSlots = 1;
        x = columnIndex - 1;
        y = slotIndex + 1;
        while (0 <= x && y < nbrRows) {
            Connect4Slot slot = myGame.getColumn(x).getSlot(y);
            if (slot.getIsFilled() && slot.getIsRed() == redPlayer) {
                nbrConnectedSlots ++;
                if (nbrConnectedSlots == 4) {
                    return true;
                }
            }
            else break; 
            x--;
            y++;
        }
        // Continue to check diagonally right side and up side
        x = columnIndex + 1;
        y = slotIndex - 1;
        while (x < nbrColumns && 0 <= y) {
            Connect4Slot slot = myGame.getColumn(x).getSlot(y);
            if (slot.getIsFilled() && slot.getIsRed() == redPlayer) {
                nbrConnectedSlots ++;
                if (nbrConnectedSlots == 4) {
                    return true;
                }
            }
            else break; 
            x++;
            y--;
        }

        return false;
    }
    
    /**
     * Returns the column that would allow the opponent to win.
     * 
     * You might want your agent to check to see if the opponent would have any winning moves
     * available so your agent can block them. Implement this method to return what column should
     * be blocked to prevent the opponent from winning.
     *
     * @return the column that would allow the opponent to win.
     */
    public int theyCanWin()
    {
        return canWin(!iAmRed);
    }

    /**
     * Returns the column that would allow the Red agent to connect the most number of tokens.
     * @return the column index.
     */
    public int iCanConnectTheMost()
    {
         return canConnectTheMost(iAmRed);
    }

    /**
     * Returns the column that would allow the agent to connect the most of tokens.
     * @param redPlayer true if the agent is Red, false if the agent is Yellow.
     * @return the column index, -1 if none.
     */
    public int canConnectTheMost(boolean redPlayer)
    {
        int winningColumn = -1;
        int maxConnects = 0;
        for (int i = 0; i < myGame.getColumnCount(); i++) 
        {
            int slotIndex = getLowestEmptyIndex(myGame.getColumn(i));
            if (slotIndex != -1)  // if column is not full
            {
                int connects = canConnectUpTo(i, slotIndex, redPlayer);
                if (connects > maxConnects)  
                {
                    maxConnects = connects;
                    winningColumn = i;
                }
            }
        }
        return winningColumn; 
    }

    /** 
     * Returns the maximum out of all the possible connections, given the slot at 
     * the given column.
     * @param columnIndex index of the column the slot belongs to.
     * @param slotIndex index of the given slot 
     * @return the maximum number.
     */
    public int canConnectUpTo(int columnIndex, int slotIndex, boolean redPlayer) 
    {
        int nbrColumns = myGame.getColumnCount();
        int nbrRows = myGame.getRowCount();
        int maxConnects = 0;    
        
        if ( !((0 <= columnIndex && columnIndex < nbrColumns) && (0 <= slotIndex && slotIndex < nbrRows)) )
            return maxConnects;
        
        // Check horizonatally and left side
        int nbrConnectedTokens = 0;
        for (int x = columnIndex - 1; 0 <= x; x--) {
            Connect4Slot slot = myGame.getColumn(x).getSlot(slotIndex);
            if (slot.getIsFilled() && slot.getIsRed() == redPlayer) {
                nbrConnectedTokens ++;
            }
            else break;
        }
        // Continue checking horizonatally but right side
        for (int x = columnIndex + 1; x < nbrColumns; x++) {
            Connect4Slot slot = myGame.getColumn(x).getSlot(slotIndex);
            if (slot.getIsFilled() && slot.getIsRed() == redPlayer) {
                nbrConnectedTokens ++;
                }
            else break;
        }           
        maxConnects = (nbrConnectedTokens > maxConnects) ? nbrConnectedTokens : maxConnects;

        // Check vertically and down side
        nbrConnectedTokens = 0;
        for (int y = slotIndex + 1; y < nbrRows; y++) {
            Connect4Slot slot = myGame.getColumn(columnIndex).getSlot(y);
            if (slot.getIsFilled() && slot.getIsRed() == redPlayer) {
                nbrConnectedTokens ++;
            }
            else break;
        } 
        // Continue checkinh vertically but up side
        for (int y = slotIndex - 1; 0 <= y; y--) {
            Connect4Slot slot = myGame.getColumn(columnIndex).getSlot(y);
            if (slot.getIsFilled() && slot.getIsRed() == redPlayer) {
                nbrConnectedTokens ++;
            }
            else break;
        }           
        maxConnects = (nbrConnectedTokens > maxConnects) ? nbrConnectedTokens : maxConnects;

        // Chek diagonally left side and up side
        nbrConnectedTokens = 0;
        int x = columnIndex - 1;
        int y = slotIndex - 1;
        while (0 <= x && 0 <= y) {
            Connect4Slot slot = myGame.getColumn(x).getSlot(y);
            if (slot.getIsFilled() && slot.getIsRed() == redPlayer) {
                nbrConnectedTokens ++;
            }
            else break;
            x--;
            y--;
        }
        // Continue checking diagonally but right side and down side
        x = columnIndex + 1;
        y = slotIndex + 1;
        while (x < nbrColumns && y < nbrRows) {
            Connect4Slot slot = myGame.getColumn(x).getSlot(y);
            if (slot.getIsFilled() && slot.getIsRed() == redPlayer) {
                nbrConnectedTokens ++;
            }
            else break; 
            x++;
            y++;
        }
        maxConnects = (nbrConnectedTokens > maxConnects) ? nbrConnectedTokens : maxConnects;

        // Check diagonally left side and down side
        nbrConnectedTokens = 0;
        x = columnIndex - 1;
        y = slotIndex + 1;
        while (0 <= x && y < nbrRows) {
            Connect4Slot slot = myGame.getColumn(x).getSlot(y);
            if (slot.getIsFilled() && slot.getIsRed() == redPlayer) {
                nbrConnectedTokens ++;
            }
            else break; 
            x--;
            y++;
        }
        // Continue checking diagonally but right side and up side
        x = columnIndex + 1;
        y = slotIndex - 1;
        while (x < nbrColumns && 0 <= y) {
            Connect4Slot slot = myGame.getColumn(x).getSlot(y);
            if (slot.getIsFilled() && slot.getIsRed() == redPlayer) {
                nbrConnectedTokens ++;
            }
            else break; 
            x++;
            y--;
        }
        maxConnects = (nbrConnectedTokens > maxConnects) ? nbrConnectedTokens : maxConnects;

        return maxConnects;
    }
    
    /**
     * Returns the name of this agent.
     *
     * @return the agent's name
     */
    public String getName()
    {
        return "My Agent";
    }
}
