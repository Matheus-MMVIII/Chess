package com.chess.model;

import com.chess.exception.BadRequestException;
import com.chess.exception.NotFoundException;

public class Table {
  private Piece[][] table;
  private boolean whiteTime = true;

  //k = King
  //q = queen
  //b = bishop
  //k = knight/horse
  //r = rook
  //p = pawn

  public Table() {
    table = new Piece[8][8];
    generateBoard();
    //printBoard();
  }

  public String[][] getBoard() {
    String[][] board = new String[8][8];
    for (int i = 0; i < table.length; i++) {
      for (int j = 0; j < table[i].length; j++) {
        board[i][j] = "";
        if (table[i][j] != null) {
          board[i][j] += table[i][j].getType();
        }else {
          board[i][j] += '.';
        }
      }
    }
    return board;
  }

  private void printBoard() {
    System.out.print(" ");
    for (int a = 0; a < table.length; a++) {
      System.out.print(" "+a);
    }
    System.out.println(" ");
    for (int i = 0; i < table.length; i++) {
      System.out.print(i);
      for (int j = 0; j < table[i].length; j++) {
        if (table[i][j] != null)
          System.out.print(" "+table[i][j].getType());
        else
          System.out.print(" .");
      }
      System.out.println("");
    }
    System.out.println(" ");
  }

  public void deleteBoard() {
    table = null;
  }

  private void generateBoard() {
    for (int i = 0; i < table.length; i++) {
      for (int j = 0; j < table[i].length; j++) {
        if (i == 0) {
          switch (j) {
            case 0, 7 -> table[i][j] = new Rook('R', i, j, false, this);
            case 1, 6 -> table[i][j] = new Horse('H', i, j, false, this);
            case 2, 5 -> table[i][j] = new Bishop('B', i, j, false, this);
            case 3 -> table[i][j] = new Queen('Q', i, j, false, this);
            case 4 -> table[i][j] = new King('K', i, j, false, this);
          }
        }else if (i == 1) {
          table[i][j] = new Pawn('P', i, j, false, this);

        }else if (i == 6) {
          table[i][j] = new Pawn('p', i, j, true, this);
        }else if (i == 7) {
          switch (j) {
            case 0, 7 -> table[i][j] = new Rook('r', i, j, true, this);
            case 1, 6 -> table[i][j] = new Horse('h', i, j, true, this);
            case 2, 5 -> table[i][j] = new Bishop('b', i, j, true, this);
            case 3 -> table[i][j] = new Queen('q', i, j, true, this);
            case 4 -> table[i][j] = new King('k', i, j, true, this);
          }
        }else
          table[i][j] = null;
      }
    }
  }

  public boolean getPosIsNull(int posLine, int posColumn) {
    return table[posLine][posColumn] == null;
  }

  public boolean getPieceFirstMove(int posLine, int posColumn) {
    if (table[posLine][posColumn] == null)
      return false;
    return table[posLine][posColumn].isFirstMove();
  }

  public boolean haveFriendPiece(int posLine, int posColumn, boolean white) {
    if (table[posLine][posColumn] == null)
      return false;
    return table[posLine][posColumn].getIsWhite() == white;
  }

  public void registerPos(int posLine, int posColumn, Piece piece) {
    table[posLine][posColumn] = piece;
  }

  public void removePos(int posLine, int posColumn) {
    table[posLine][posColumn] = null;
  }

  public synchronized void move(int startLine, int startColumn, int endLine, int endColumn) {
    if (startLine < 0 || startLine > 7 || startColumn < 0 || startColumn > 7) throw new BadRequestException("Invalid start position. ");
    if (endLine < 0 || endLine > 7 || endColumn < 0 || endColumn > 7) throw new BadRequestException("Invalid end position. ");
    if (getPosIsNull(startLine, startColumn)) throw new NotFoundException("Piece not found. ");
    if (table[startLine][startColumn].getIsWhite() != whiteTime) throw new BadRequestException("Is not your turn. ");
    table[startLine][startColumn].move(endLine, endColumn);
    whiteTime = !table[endLine][endColumn].getIsWhite();
    //System.out.println("TurnWhite: "+whiteTime);
    //printBoard();
  }

  public void promotePawn(int posLine, int posColumn, char type) {
    if (posLine < 0 || posLine > 7 || posColumn < 0 ||posColumn > 7) throw new BadRequestException("Invalid position. ");
    boolean white = table[posLine][posColumn].getIsWhite();
    type = white ? Character.toLowerCase(type) : Character.toUpperCase(type);
    switch (type) {
      case 'Q', 'q' -> table[posLine][posColumn] = new Queen(type, posLine, posColumn, white, this);
      case 'R', 'r' -> table[posLine][posColumn] = new Rook(type, posLine, posColumn, white, this);
      case 'B', 'b' -> table[posLine][posColumn] = new Bishop(type, posLine, posColumn, white, this);
      case 'H', 'h' -> table[posLine][posColumn] = new Horse(type, posLine, posColumn, white, this);
    }
    //printBoard();
  }
}
