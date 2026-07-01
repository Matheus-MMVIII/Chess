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

  public Piece getPiece(int posLine, int posColumn) {
    return table[posLine][posColumn];
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
    Piece movingPiece = table[startLine][startColumn];
    if (movingPiece.getIsWhite() != whiteTime) throw new BadRequestException("Is not your turn. ");
    if (table[endLine][endColumn] instanceof King) throw new BadRequestException("Kings cannot be captured. ");

    boolean movingPieceFirstMove = movingPiece.isFirstMove();
    Piece capturedPiece = table[endLine][endColumn];
    boolean capturedPieceFirstMove = capturedPiece != null && capturedPiece.isFirstMove();
    boolean castlingMove = isCastlingMove(movingPiece, startLine, startColumn, endLine, endColumn);
    Piece castlingRook = null;
    int castlingRookStartColumn = -1;
    int castlingRookEndColumn = -1;
    boolean castlingRookFirstMove = false;

    if (castlingMove) {
      validateCastlingSafety(startLine, startColumn, endColumn, movingPiece.getIsWhite());
      boolean kingSide = endColumn > startColumn;
      castlingRookStartColumn = kingSide ? 7 : 0;
      castlingRookEndColumn = kingSide ? 5 : 3;
      castlingRook = table[startLine][castlingRookStartColumn];
      castlingRookFirstMove = castlingRook != null && castlingRook.isFirstMove();
    }

    movingPiece.move(endLine, endColumn);

    if (isKingInCheck(movingPiece.getIsWhite())) {
      restoreMove(movingPiece, startLine, startColumn, movingPieceFirstMove,
              capturedPiece, endLine, endColumn, capturedPieceFirstMove,
              castlingRook, castlingRookStartColumn, castlingRookEndColumn, castlingRookFirstMove);
      throw new BadRequestException("Move leaves the king in check. ");
    }

    whiteTime = !movingPiece.getIsWhite();
    //System.out.println("TurnWhite: "+whiteTime);
    //printBoard();
  }

  public boolean isKingInCheck(boolean white) {
    int[] kingPosition = findKing(white);
    if (kingPosition == null) {
      return false;
    }

    return isSquareUnderAttack(kingPosition[0], kingPosition[1], !white);
  }

  private boolean isCastlingMove(Piece piece, int startLine, int startColumn, int endLine, int endColumn) {
    return piece instanceof King
            && startLine == endLine
            && Math.abs(endColumn - startColumn) == 2;
  }

  private void validateCastlingSafety(int line, int startColumn, int endColumn, boolean white) {
    if (isKingInCheck(white)) {
      throw new BadRequestException("Cannot castle while in check. ");
    }

    int direction = Integer.compare(endColumn, startColumn);
    for (int column = startColumn + direction; column != endColumn + direction; column += direction) {
      if (isSquareUnderAttack(line, column, !white)) {
        throw new BadRequestException("Cannot castle through check. ");
      }
    }
  }

  private void restoreMove(Piece movingPiece, int startLine, int startColumn, boolean movingPieceFirstMove,
                           Piece capturedPiece, int endLine, int endColumn, boolean capturedPieceFirstMove,
                           Piece castlingRook, int castlingRookStartColumn, int castlingRookEndColumn,
                           boolean castlingRookFirstMove) {
    table[startLine][startColumn] = movingPiece;
    table[endLine][endColumn] = capturedPiece;
    movingPiece.setPosition(startLine, startColumn);
    movingPiece.setFirstMove(movingPieceFirstMove);

    if (capturedPiece != null) {
      capturedPiece.setPosition(endLine, endColumn);
      capturedPiece.setFirstMove(capturedPieceFirstMove);
    }

    if (castlingRook != null) {
      table[startLine][castlingRookEndColumn] = null;
      table[startLine][castlingRookStartColumn] = castlingRook;
      castlingRook.setPosition(startLine, castlingRookStartColumn);
      castlingRook.setFirstMove(castlingRookFirstMove);
    }
  }

  private int[] findKing(boolean white) {
    for (int line = 0; line < table.length; line++) {
      for (int column = 0; column < table[line].length; column++) {
        Piece piece = table[line][column];
        if (piece instanceof King && piece.getIsWhite() == white) {
          return new int[]{line, column};
        }
      }
    }

    return null;
  }

  private boolean isSquareUnderAttack(int line, int column, boolean byWhite) {
    for (int attackerLine = 0; attackerLine < table.length; attackerLine++) {
      for (int attackerColumn = 0; attackerColumn < table[attackerLine].length; attackerColumn++) {
        Piece piece = table[attackerLine][attackerColumn];
        if (piece != null
                && piece.getIsWhite() == byWhite
                && pieceCanAttackSquare(piece, attackerLine, attackerColumn, line, column)) {
          return true;
        }
      }
    }

    return false;
  }

  private boolean pieceCanAttackSquare(Piece piece, int startLine, int startColumn, int targetLine, int targetColumn) {
    int lineDiff = Math.abs(targetLine - startLine);
    int columnDiff = Math.abs(targetColumn - startColumn);

    if (lineDiff == 0 && columnDiff == 0) {
      return false;
    }

    return switch (Character.toLowerCase(piece.getType())) {
      case 'p' -> {
        int direction = piece.getIsWhite() ? -1 : 1;
        yield targetLine == startLine + direction && columnDiff == 1;
      }
      case 'h' -> (lineDiff == 2 && columnDiff == 1) || (lineDiff == 1 && columnDiff == 2);
      case 'b' -> lineDiff == columnDiff && pathIsClear(startLine, startColumn, targetLine, targetColumn);
      case 'r' -> (startLine == targetLine || startColumn == targetColumn)
              && pathIsClear(startLine, startColumn, targetLine, targetColumn);
      case 'q' -> ((startLine == targetLine || startColumn == targetColumn) || lineDiff == columnDiff)
              && pathIsClear(startLine, startColumn, targetLine, targetColumn);
      case 'k' -> Math.max(lineDiff, columnDiff) == 1;
      default -> false;
    };
  }

  private boolean pathIsClear(int startLine, int startColumn, int targetLine, int targetColumn) {
    int lineStep = Integer.compare(targetLine, startLine);
    int columnStep = Integer.compare(targetColumn, startColumn);

    for (int line = startLine + lineStep, column = startColumn + columnStep;
         line != targetLine || column != targetColumn;
         line += lineStep, column += columnStep) {
      if (!getPosIsNull(line, column)) {
        return false;
      }
    }

    return true;
  }

  public void promotePawn(int posLine, int posColumn, char promotionType) {
    if (posLine < 0 || posLine > 7 || posColumn < 0 ||posColumn > 7) throw new BadRequestException("Invalid position. ");
    if (table[posLine][posColumn] == null) throw new BadRequestException("Invalid position. ");
    if (posLine != 0 && posLine != 7) throw new BadRequestException("Invalid position to promote. ");
    char type = table[posLine][posColumn].getType();
    if (type != 'p' && type != 'P') throw new BadRequestException("Invalid piece to promotion. ");
    boolean white = table[posLine][posColumn].getIsWhite();
    promotionType = white ? Character.toLowerCase(promotionType) : Character.toUpperCase(promotionType);
    switch (promotionType) {
      case 'Q', 'q' -> table[posLine][posColumn] = new Queen(promotionType, posLine, posColumn, white, this);
      case 'R', 'r' -> table[posLine][posColumn] = new Rook(promotionType, posLine, posColumn, white, this);
      case 'B', 'b' -> table[posLine][posColumn] = new Bishop(promotionType, posLine, posColumn, white, this);
      case 'H', 'h' -> table[posLine][posColumn] = new Horse(promotionType, posLine, posColumn, white, this);
      default -> throw new BadRequestException("Invalid promotion piece. ");
    }
    //printBoard();
  }
}
