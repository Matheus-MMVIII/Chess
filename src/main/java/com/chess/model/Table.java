package com.chess.model;

import com.chess.exception.NotFoundException;

public class Table {
  private static Piece[][] table = new Piece[8][8];

  public Table() {
    generateBoard();
    printBoard();
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

  private void generateBoard() {
    for (int i = 0; i < table.length; i++) {
      for (int j = 0; j < table[i].length; j++) {
        if (i == 0) {
          switch (j) {
            //case 0, 7 -> table[i][j] = 'T';
            //case 1, 6 -> table[i][j] = 'C';
            //case 2, 5 -> table[i][j] = 'B';
            //case 3 -> table[i][j] = 'Q';
            //case 4 -> table[i][j] = 'R';
          }
        }else if (i == 1) {
          table[i][j] = new Pawn('P', i, j, false);

        }else if (i == 6) {
          table[i][j] = new Pawn('p', i, j, true);
        }else if (i == 7) {
          switch (j) {
            //case 0, 7 -> table[i][j] = 't';
            //case 1, 6 -> table[i][j] = 'c';
            //case 2, 5 -> table[i][j] = 'b';
            //case 3 -> table[i][j] = 'q';
            //case 4 -> table[i][j] = 'r';
          }
        }else
          table[i][j] = null;
      }
    }
  }

  public boolean getPosNull(int posLine, int posColumn) {
    return table[posLine][posColumn] == null;
  }

  public void registerPos(int posLine, int posColumn, Piece piece) {
    table[posLine][posColumn] = piece;
  }

  public void removePos(int posLine, int posColumn) {
    table[posLine][posColumn] = null;
  }

  public void move(int startLine, int startColumn, int endLine, int endColumn) {
    if (getPosNull(startLine, startColumn)) throw new NotFoundException("Piece not found. ");
    table[startLine][startColumn].move(endLine, endColumn, this);
    printBoard();
  }
}
