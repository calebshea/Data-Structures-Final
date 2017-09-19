package application;

import java.util.Observable;

public class GameBoard extends Observable {
	/** Instance Variables */
	/** 2D array of enum Player type representing gameboard */
	private Player[][] board;
	/**
	 * 2D boolean array that tracks visited positions during the depth first
	 * search. Unvisited positions will be marked with a false boolean, visited
	 * positions will be marked with a true boolean
	 */
	private boolean[][] visitedPosition;
	/** Identifies winner */
	private Player winner;
	/** Identifies the turn of the current player */
	private Player currentTurn;
	/** Identifies the size of the board */
	private int size;
	/** Counts number of turns */
	private int turnCount;
	/** Update status Label */
	private String statusLabel;

	/**
	 * Default constructor Sets gameboard at size of 8, sets turnCount at 0 and
	 * sets the next turn
	 */
	public GameBoard() {
		setSize(8);
		winner = Player.UNOWNED;
		turnCount = 0;
		setNextTurn();
	}

	/**
	 * Constructor
	 * 
	 * @param n
	 *            int size of the game board Sets gameboard at size of n, sets
	 *            turnCount at 0 and sets the next turn
	 */
	public GameBoard(int n) {
		setSize(n);
		winner = Player.UNOWNED;
		turnCount = 0;
		setNextTurn();
	}

	/**
	 * Gets size of the user gameboard
	 * 
	 * @return int
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Gets the type of token at any given position
	 * 
	 * @param row
	 *            as int
	 * @param col
	 *            as int
	 * @return 0 for unowned token, 1 for north rabbit token, 2 for south rabbit
	 *         token, and 3 for farmer token
	 */
	public int getTokenAt(int row, int col) {
		if (board[row][col].equals(Player.UNOWNED))
			return 0;
		if (board[row][col].equals(Player.NORTHRABBIT))
			return 1;
		if (board[row][col].equals(Player.SOUTHRABBIT))
			return 2;
		else
			return 3;
	}

	/**
	 * setMove method that takes in position and aims to
	 * 
	 * @param row
	 * @param col
	 * @throws Exception
	 */
	public void setMove(int row, int col) throws Exception {
		if (board.equals(null) || !winner.equals(Player.UNOWNED))
			throw new Exception("Game is over!");
		if (!board[row][col].equals(Player.UNOWNED) || (row > size || col > size))
			throw new Exception("You can't move there!");
		if (currentTurn.equals(Player.FARMER)) {
			board[row][col] = currentTurn;
		} else {
			if (isValidRabbitMove(row, col))
				board[row][col] = currentTurn;
			else
				throw new Exception("You can't move there!");
		}
		if (!isGameOver())
			setNextTurn();
		setChanged();
		notifyObservers();
	}

	/**
	 * Sets the next turn based on the current turn and increases the turn count
	 * by 1
	 */
	public void setNextTurn() {
		turnCount++;
		if (turnCount % 3 == 1) {
			currentTurn = Player.NORTHRABBIT;
			statusLabel = "North rabbit's turn";
		} else if (turnCount % 3 == 2) {
			currentTurn = Player.SOUTHRABBIT;
			statusLabel = "South rabbit's turn";
		} else {
			currentTurn = Player.FARMER;
			statusLabel = "Farmer's turn";
		}
	}

	/**
	 * Creates game board of size n + 2 in order to streamline the check win
	 * algorithms
	 * 
	 * @param n
	 *            int is size of board
	 */
	private void setSize(int n) {
		size = n;
		board = new Player[n + 2][n + 2];
		for (int i = 0; i < n + 2; i++) {
			for (int j = 0; j < n + 2; j++) {
				board[i][j] = Player.UNOWNED;
			}
		}
		setChanged();
		notifyObservers();
	}

	/**
	 * Method called to check whether a given position constitutes a valid
	 * rabbit move
	 * 
	 * @param row
	 *            as int
	 * @param col
	 *            as int
	 * @return true if move is valid, false if invalid
	 */
	private boolean isValidRabbitMove(int row, int col) {
		if (currentTurn.equals(Player.NORTHRABBIT) && row == 1)
			return true;
		if (currentTurn.equals(Player.SOUTHRABBIT) && row == size)
			return true;
		if (board[row + 1][col].equals(currentTurn))
			return true;
		if (board[row][col + 1].equals(currentTurn))
			return true;
		if (board[row - 1][col].equals(currentTurn))
			return true;
		if (board[row][col - 1].equals(currentTurn))
			return true;
		return false;
	}

	/**
	 * Method called to check for end of game state
	 * 
	 * @return true for end of game, false otherwise
	 */
	private boolean isGameOver() {
		if (currentTurn.equals(Player.FARMER)) {
			if (isFarmerWin()) {
				winner = Player.FARMER;
				statusLabel = "THE FARMER WON!";
				return true;
			}
		} else if (isRabbitWin()) {
			winner = Player.NORTHRABBIT;
			statusLabel = "THE RABBITS WON!";
			return true;
		}
		return false;
	}

	/**
	 * Java enum type for different players Unowned, North Rabbit, South Rabbit,
	 * and Farmer will fill gameboard array
	 */
	private enum Player {
		UNOWNED, NORTHRABBIT, SOUTHRABBIT, FARMER
	}

	/**
	 * Method called to check for a farmer win
	 * 
	 * @return true for farmer win, false if otherwise
	 */
	private boolean isFarmerWin() {
		for (int k = 1; k <= size; k++)
			if (board[k][1].equals(Player.FARMER)) {
				visitedPosition = new boolean[size + 2][size + 2];

				if (recFarmerWin(k, 1) == size)
					return true;
			}
		return false;
	}

	/**
	 * Recursive method called for a depth first search of a possible farmer
	 * win. A farmer win is constituted by an walled impasse
	 * 
	 * @param row
	 *            as int
	 * @param col
	 *            as int
	 * @return the column of the furthest complete impasse. If this is equal to
	 *         the size of the board, the farmer has won.
	 */
	private int recFarmerWin(int row, int col) {
		visitedPosition[row][col] = true;
		if (col == size)
			return col;
		int bestSol = 0;
		int tempVar = 0;
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				if (!(i == 0 && j == 0) && board[row + i][col + j] == Player.FARMER
						&& !visitedPosition[row + i][col + j]) {
					tempVar = recFarmerWin(row + i, col + j);
					if (tempVar > bestSol)
						bestSol = tempVar;
				}
			}
		}
		return bestSol;
	}

	/**
	 * Method called to check for a rabbit win
	 * 
	 * @return true for rabbit win, false if otherwise
	 */
	private boolean isRabbitWin() {
		for (int k = 1; k <= size; k++)
			if (board[1][k].equals(Player.NORTHRABBIT) || board[1][k].equals(Player.SOUTHRABBIT)) {
				visitedPosition = new boolean[size + 2][size + 2];

				if (recRabbitWin(1, k) == size)
					return true;
			}
		return false;
	}

	/**
	 * Recursive method called for a depth first search of a possible rabbit
	 * win. A farmer win is constituted by a complete tunnel from north to south
	 * 
	 * @param row
	 *            as int
	 * @param col
	 *            as int
	 * @return the row of the furthest complete tunnel. If this is equal to the
	 *         size of the board, the farmer has won.
	 */
	private int recRabbitWin(int row, int col) {
		visitedPosition[row][col] = true;
		if (row == size)
			return row;
		int bestSol = 0;
		int tempVar = 0;

		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				if ((i + j == 1 || i + j == -1)
						&& (board[row + i][col + j] == Player.NORTHRABBIT
								|| board[row + i][col + j] == Player.SOUTHRABBIT)
						&& !visitedPosition[row + i][col + j]) {
					tempVar = recRabbitWin(row + i, col + j);
					if (tempVar > bestSol)
						bestSol = tempVar;
				}
			}
		}
		return bestSol;
	}

	/**
	 * Status label method
	 * 
	 * @return current turn and player name when a player has won
	 */
	public String getStatusLabel() {
		return statusLabel;
	}
}
