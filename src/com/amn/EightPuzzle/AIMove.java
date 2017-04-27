package com.amn.EightPuzzle;

public class AIMove {
	// ...
	// We don't really need these any more... but meh...
	public int x;
	public int y;
	
	// The heuristic distance
	public int heuristicDistance;
	
	// Moves made to reach this state
	public int numberOfMoves;
	
	// The previous State from whence we came
	public AIMove previousMove;
	
	// The state of the board
	public Board boardState;
	
	// The constructors
	public AIMove() {}

	public AIMove(int heuristicDistance, int numberOfMoves, Board boardState, AIMove previousMove) 
	{
		this.heuristicDistance = heuristicDistance;
		this.numberOfMoves = numberOfMoves;
		this.boardState = boardState;
		this.previousMove = previousMove;
	}
	
	public AIMove(int x, int y, int heuristicDistance, int numberOfMoves, Board boardState, AIMove previousMove)
	{
		this.x = x;
		this.y = y;
		this.heuristicDistance = heuristicDistance;
		this.numberOfMoves = numberOfMoves;
		this.boardState = boardState;
		this.previousMove = previousMove;
	}
	
	// Returns the Manhattan Distance
	public int getManhattanDistance()
	{
		return this.heuristicDistance + this.numberOfMoves;
	}
	
	// Used during Move Xaching
	public boolean equals(Object obj)
	{
		if(this.boardState.equals(((AIMove)obj).boardState))
		{
			return true;
		}
		
		return false;
	}
}
