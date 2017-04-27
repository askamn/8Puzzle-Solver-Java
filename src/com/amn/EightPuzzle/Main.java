package com.amn.EightPuzzle;

public class Main {
	public int[][] puzzle = new int[][]{
		{ 8, 2, 3 },
		{ Board.EMPTY_VAL, 6, 5 },
		{ 4, 7, 1 }
	};
	
	public Board board;
	
	public static void main(String[] args)
	{
		new Main().run();
	}
	
	public void run()
	{
		board = new Board(puzzle);
		board.Init();
	}
}
