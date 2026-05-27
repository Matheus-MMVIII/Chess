package com.chess.model;

public class Table {
  private static int[][] table = new int[8][8];

  public boolean getPosNull(int posx, int posy) {
    return (table[posx][posy] == null);
  }

  public void registerPos(int posx, int posy) {
    table[posx][posy] = 1;
  }
}
