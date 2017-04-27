package com.amn.EightPuzzle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OldBoard 
{
	public static final int ROWS = 3;
	public static final int COLS = 3;
	public static final int EMPTY_VAL = -1;
	
	public int[] emptySquarePos = new int[2];
	
	public static final int[][] DEFAULT_STATE = new int[][]{ 
		/*{ -1, 1, 3 },
		{  4, 2, 5 },
		{  7, 8, 6 }*/
		
		{  8, 1, 3 },
		{  4, EMPTY_VAL, 2 },
		{  7, 6, 5 }
	};
	
	public static final int[][] GOAL = new int[][]{
		{ 1, 2, 3  },
		{ 4, 5, 6  },
		{ 7, 8, EMPTY_VAL }
	};
	
	public int[][] distances = new int[][]{
		{ EMPTY_VAL, EMPTY_VAL, EMPTY_VAL },
		{ EMPTY_VAL, EMPTY_VAL, EMPTY_VAL },
		{ EMPTY_VAL, EMPTY_VAL, EMPTY_VAL },
	};

	
	public int[][] currentState;
	
	public OldBoard()
	{
		this.setState(Board.DEFAULT_STATE);
		this.printPuzzle();
		this.makeMove();
		this.printPuzzle();
		this.makeMove();
		this.printPuzzle();
		this.printDifferences();
	}
	
	public OldBoard(int state[][])
	{
		this.setState(state);
	}
	
	public void setState(int state[][])
	{
		this.currentState = state;
		this.calculateDistances();
		
		// Find the empty square position on the board
		boolean found = false;
		
		for(int i = 0; i < ROWS; ++i)
		{
			for(int j = 0; j < COLS; ++j)
			{
				if(this.currentState[i][j] == EMPTY_VAL)
				{
					this.emptySquarePos[0] = i;
					this.emptySquarePos[1] = j;
					found = true;
					break;
				}
			}
			
			if(found)	break;
		}
	}
	
	public void makeMove()
	{	
		ArrayList<List<Integer>> validMoves = new ArrayList<List<Integer>>();
		
		// Valid moves along the same row
		if(this.emptySquarePos[1] + 1 < COLS)
		{
			validMoves.add(Arrays.asList(this.emptySquarePos[0], this.emptySquarePos[1] + 1));
		}
		
		if(this.emptySquarePos[1] - 1 >= 0)
		{
			validMoves.add(Arrays.asList(this.emptySquarePos[0], this.emptySquarePos[1] - 1));
		}
		
		// Valid moves along the same column
		if(this.emptySquarePos[0] - 1 >= 0)
		{
			validMoves.add(Arrays.asList(this.emptySquarePos[0] - 1, this.emptySquarePos[1]));
		}
		
		if(this.emptySquarePos[0] + 1 < ROWS)
		{
			validMoves.add(Arrays.asList(this.emptySquarePos[0] + 1, this.emptySquarePos[1]));
		}
		
		// Select the move with the minimum distance
		int[] minimumDistanceMoveIndex = this.getMoveWithMinimumDistance(validMoves);
		
		this.setSquare(this.emptySquarePos[0], this.emptySquarePos[1], this.currentState[minimumDistanceMoveIndex[0]][minimumDistanceMoveIndex[1]]);
		this.setSquare(minimumDistanceMoveIndex[0], minimumDistanceMoveIndex[1], EMPTY_VAL);
		
		this.emptySquarePos = minimumDistanceMoveIndex;
		
		this.calculateDistances();
	}
	
	public void printPuzzle()
	{
		for(int i = 0; i < ROWS; ++i)
		{
			for(int j = 0; j < COLS; j++)
			{
				System.out.print(this.currentState[i][j] + " ");
			}
			
			System.out.println();
		}
		
		System.out.println("--------");
	}
	
	public int[] getMoveWithMinimumDistance(ArrayList<List<Integer>> moves)
	{
		int[] bestMove = new int[2];
		
		int minimum = this.distances[0][0];
		
		for(List<Integer> l : moves)
		{
			int i = l.get(0);
			int j = l.get(1);
			
			// If distance is 0 then it is already at the required position, no need to update best move
			if(this.distances[i][j] != 0 && ( this.distances[i][j] < minimum || minimum == EMPTY_VAL ))
			{
				minimum = this.distances[i][j];
				bestMove[0] = i;
				bestMove[1] = j;
			}
		}
		
		return bestMove;
	}
	
	public void printDifferences()
	{
		for(int i = 0; i < ROWS; ++i)
		{
			for(int j = 0; j < COLS; j++)
			{
				System.out.print(this.distances[i][j] + " ");
			}
			
			System.out.println();
		}
	}
	
	public void calculateDistances()
	{
		for(int i = 0; i < ROWS; ++i)
		{
			for(int j = 0; j < COLS; ++j)
			{
				if(this.currentState[i][j] != EMPTY_VAL)
				{
					// Already at it's position
					if(this.currentState[i][j] == GOAL[i][j])
					{
						this.distances[i][j] = 0;
						continue;
					}
					
					int actualPositionIndex = this.currentState[i][j] - 1;
					int currentPositionIndex = j + i * COLS;
					
					int distance = 0;
					int difference = (currentPositionIndex - actualPositionIndex);
					
					if( difference > 0 )
					{
						while( difference > 2 )
						{
							currentPositionIndex -= COLS;
							difference = (currentPositionIndex - actualPositionIndex);
							distance++;
						}
						
						while(difference != 0)
						{
							currentPositionIndex--;
							difference = (currentPositionIndex - actualPositionIndex);
							distance++;
						}
					}
					else
					{
						while( difference < -2 )
						{
							currentPositionIndex += COLS;
							difference = (currentPositionIndex - actualPositionIndex);
							distance++;
						}
						
						while(difference != 0)
						{
							currentPositionIndex++;
							difference = (currentPositionIndex - actualPositionIndex);
							distance++;
						}
					}
					
					this.distances[i][j] = distance;
				}
				else
				{
					this.distances[i][j] = EMPTY_VAL;
				}
			}
		}
	}
	
	public int[] selectMinimumDistanceMove()
	{
		int minimum = this.distances[0][0];
		int[] index = { 0, 0 };
		
		for(int i = 0; i < ROWS; ++i)
		{
			for(int j = 0; j < COLS; ++j)
			{
				if(this.distances[i][j] != -1 && ( this.distances[i][j] < minimum || minimum == EMPTY_VAL))
				{
					minimum = this.distances[i][j];
					index[0] = i;
					index[1] = j;
				}
			}
		}
		
		return index;
	}
	
	public void setSquare(int i, int j, int value)
	{
		this.currentState[i][j] = value;
	}
	
	public boolean puzzleSolved()
	{
		for(int i = 0; i < ROWS; ++i)
		{
			for(int j = 0; j < COLS; ++j)
			{
				if(this.currentState[i][j] != GOAL[i][j])
				{
					return false;
				}
			}
		}
		
		return true;
	}
}
