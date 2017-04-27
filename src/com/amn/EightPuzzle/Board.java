package com.amn.EightPuzzle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Board 
{
	// Handy Constants
	public static final int ROWS = 3;
	public static final int COLS = 3;
	public static final int EMPTY_VAL = -1;
	
	// Stores the position of the Empty Tile
	public int[] emptySquarePos = new int[2];
	
	// DEBUG:
	// The default State, for testing
	public static final int[][] DEFAULT_STATE = new int[][]{ 
		{ Board.EMPTY_VAL, 1, 3 },
		{  4, 2, 5 },
		{  7, 8, 6 }
	};
	
	public final static int[][] DEFAULT_STATE2 = new int[][]{
		{  8, 1, 3 },
		{  4, Board.EMPTY_VAL, 2 },
		{  7, 6, 5 }
	};
	
	// DEBUG:
	// This is an insolvable puzzle
	public static final int[][] IMPOSSIBLE_DEFAULT_STATE = new int[][]{
		{ 1, 2, 3 },
		{ 4, 5, 6 },
		{ 8, 7, Board.EMPTY_VAL }
	};
	
	// The Goal State
	public static final int[][] GOAL = new int[][]{
		{ 1, 2, 3  },
		{ 4, 5, 6  },
		{ 7, 8, EMPTY_VAL }
	};
	
	// This array holds the distances of tiles from their actual position in the Goal State
	public int[][] distances = new int[][]{
		{ EMPTY_VAL, EMPTY_VAL, EMPTY_VAL },
		{ EMPTY_VAL, EMPTY_VAL, EMPTY_VAL },
		{ EMPTY_VAL, EMPTY_VAL, EMPTY_VAL },
	};

	// Stores the current state of the board
	public int[][] currentState = new int[Board.ROWS][Board.COLS];
	
	// Stores the solution/final move
	public AIMove solution = null;
	
	// The size of our space graph
	public int stateSpaceSize = 1;
	
	// The beginning...
	public Board()
	{
		this.setState(Board.DEFAULT_STATE2);
	}
	
	// Okay...
	public Board(int state[][])
	{
		this.setState(state);
	}
	
	// Initialize Everything
	public void Init()
	{
		System.out.println("--------------------------------------------------");
		System.out.println(" Original Puzzle: ");
		this.printPuzzle();
		this.solve();
		
		// TODO: I read that not all 8Puzzles are solvable. Check for them!
		if(this.solution != null)
		{
			System.out.println();
			System.out.println("--------------------------------------------------");
			System.out.println("Status:           Solvable");
			System.out.println("Moves Taken: 	  " + this.solution.numberOfMoves);
			System.out.println("Space Graph Size: " + this.stateSpaceSize);
			System.out.println("Solution: ");
			System.out.println("--------------------------------------------------");
			System.out.println();
			this.printSolution();
		}
	}

	// So?
	public void setState(int state[][])
	{
		for(int i = 0; i < ROWS; ++i)
		{
			for(int j = 0; j < COLS; ++j)
			{				
				this.currentState[i][j] = state[i][j];
			}
		}
		
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
	
	// Solves the puzzle
	public void solve()
	{
		LinkedList<AIMove> moves = new LinkedList<AIMove>();
		moves.add(new AIMove(this.getHeuristicDistance(false), 0, this, null));
		
		// Get all valid move directions
		// and iterate over them
		AIMove move = moves.remove();

		while( this.puzzleSolved(move.boardState.currentState) == false )//i < 10)
		{
			for(List<Integer> moveDirection : move.boardState.getValidMoveDirections())
			{	
				int x = moveDirection.get(0);
				int y = moveDirection.get(1);
				
				// Move the block to x,y
				move.boardState.setSquare(move.boardState.emptySquarePos[0], move.boardState.emptySquarePos[1], move.boardState.currentState[x][y]);
				
				// Mark x,y as Empty
				move.boardState.setSquare(x, y, Board.EMPTY_VAL);
				
				// Store for resetting
				int[] originalEmptySquarePos = { move.boardState.emptySquarePos[0], move.boardState.emptySquarePos[1] };
				
				// Update the Empty Square Value
				move.boardState.emptySquarePos[0] = x;
				move.boardState.emptySquarePos[1] = y;
				
				AIMove newMove = new AIMove(x, y, move.boardState.getHeuristicDistance(true), move.numberOfMoves + 1, new Board(move.boardState.currentState), move);
				
				//move.boardState.printPuzzle();
				// Add it to our moves List
				// We only add it if such a move does not already exist in the Space Graph
				if( moves.contains(newMove) == false )
				{
					this.stateSpaceSize++;
					moves.add(newMove);
				}
	
				// Reset the board
				move.boardState.setSquare(x, y, move.boardState.currentState[originalEmptySquarePos[0]][originalEmptySquarePos[1]]);
				move.boardState.setSquare(originalEmptySquarePos[0], originalEmptySquarePos[1], EMPTY_VAL);
				
				move.boardState.emptySquarePos = originalEmptySquarePos;
				//move.boardState.printPuzzle();
			}
			
			// Select the move with minimum Manhattan distance
			move = null;
			
			for(AIMove minimumMove : moves)
			{
				move = (move == null) ? minimumMove : ( move.getManhattanDistance() > minimumMove.getManhattanDistance() ? minimumMove : move );
			}
			
			// Remove it from the moves list
			moves.remove(move);
		}
		
		this.solution = move;
	}
	
	// Used during Move Caching
	public boolean equals(Object obj)
	{
		for(int x = 0; x < Board.ROWS; x++)
		{
			for(int y = 0; y < Board.COLS; ++y)
			{
				if(this.currentState[x][y] != ((Board)obj).currentState[x][y])
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	public int getHeuristicDistance(boolean forceDistanceUpdate)
	{
		if( true == forceDistanceUpdate )
		{
			this.calculateDistances();
		}
		
		int heuristicDistance = 0;
		for(int x = 0; x < Board.ROWS; ++x)
		{
			for(int y = 0; y < Board.COLS; ++y)
			{
				if(this.distances[x][y] != Board.EMPTY_VAL)
					heuristicDistance += this.distances[x][y];
			}
		}
		
		return heuristicDistance;
	}
	
	// Returns a list of all possible and valid moves, given the Empty Tile
	public ArrayList<List<Integer>> getValidMoveDirections()
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
		
		return validMoves;
	}
	
	// Prints the Puzzle
	public void printPuzzle()
	{
		System.out.println("---------");
		
		for(int i = 0; i < ROWS; ++i)
		{
			for(int j = 0; j < COLS; j++)
			{
				if(this.currentState[i][j] == Board.EMPTY_VAL)
					System.out.print("  ");
				else
					System.out.print(this.currentState[i][j] + " ");
			}
			
			System.out.println();
		}
		
		System.out.println("---------");
	}
	
	// DEBUG:
	// Prints the distances of tiles from their actual position in Goal State
	public void printDifferences()
	{
		System.out.println("---------");
		System.out.println("Differences: ");
		
		for(int i = 0; i < ROWS; ++i)
		{
			for(int j = 0; j < COLS; j++)
			{
				System.out.print(this.distances[i][j] + " ");
			}
			
			System.out.println();
		}
		
		System.out.println("---------");
	}
	
	// Prints the solution
	public void printSolution()
	{
		Stack<AIMove> moves = new Stack<AIMove>();
		
		AIMove state = this.solution;
		while(state != null)
		{
			moves.add(state);
			state = state.previousMove;
		}
		
		AIMove move;
		System.out.println("---------");
		while(moves.empty() == false)
		{
			move = moves.pop();
			System.out.println(" Move " + move.numberOfMoves + ": ");
			move.boardState.printPuzzle();
		}
	}
	
	// Calculates the distances of tiles from their position in the Goal State
	public void calculateDistances()
	{
		for(int i = 0; i < ROWS; ++i)
		{
			for(int j = 0; j < COLS; ++j)
			{
				if(this.currentState[i][j] != Board.EMPTY_VAL)
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
					this.distances[i][j] = Board.EMPTY_VAL;
				}
			}
		}
	}
	
	// Sets the value of a square/tile
	public void setSquare(int i, int j, int value)
	{
		this.currentState[i][j] = value;
	}
	
	// Checks if the puzzle is solved by comparing the supplied state with the Goal state
	public boolean puzzleSolved(int[][] state)
	{
		for(int i = 0; i < ROWS; ++i)
		{
			for(int j = 0; j < COLS; ++j)
			{
				if(state[i][j] != GOAL[i][j])
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	// Checks if the puzzle is solved 
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
