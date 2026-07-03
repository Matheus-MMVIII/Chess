package com.chess.model;

import com.chess.exception.BadRequestException;
import com.chess.exception.NotFoundException;

import java.util.HashMap;
import java.util.Map;

public class Table {
  private Piece[][] table;
  private boolean whiteTime = true;
  private GameStatus gameStatus = GameStatus.ACTIVE;
  private Boolean winnerWhite;
  private int halfmoveClock = 0;
  private boolean promotionPending = false;
  private final Map<String, Integer> positionOccurrences = new HashMap<>();

  //k = King
  //q = queen
  //b = bishop
  //k = knight/horse
  //r = rook
  //p = pawn

  public Table() {
    table = new Piece[8][8];
    generateBoard();
    recordCurrentPosition();
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

  public GameStatus getGameStatus() {
    return gameStatus;
  }

  public boolean isGameOver() {
    return gameStatus.isGameOver();
  }

  public boolean isDraw() {
    return gameStatus.isDraw();
  }

  public String getWinner() {
    if (winnerWhite == null) {
      return "";
    }

    return winnerWhite ? "white" : "black";
  }

  public String getTurn() {
    return whiteTime ? "white" : "black";
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
    if (gameStatus.isGameOver()) throw new BadRequestException("Game is over. ");
    if (promotionPending) throw new BadRequestException("Promote the pawn before moving another piece. ");
    if (startLine < 0 || startLine > 7 || startColumn < 0 || startColumn > 7) throw new BadRequestException("Invalid start position. ");
    if (endLine < 0 || endLine > 7 || endColumn < 0 || endColumn > 7) throw new BadRequestException("Invalid end position. ");
    if (getPosIsNull(startLine, startColumn)) throw new NotFoundException("Piece not found. ");
    Piece movingPiece = table[startLine][startColumn];
    if (movingPiece.getIsWhite() != whiteTime) throw new BadRequestException("Is not your turn. ");
    if (table[endLine][endColumn] instanceof King) throw new BadRequestException("Kings cannot be captured. ");

    Piece capturedPiece = table[endLine][endColumn];
    MoveSnapshot snapshot = createMoveSnapshot(movingPiece, startLine, startColumn, endLine, endColumn);

    if (snapshot.castlingMove) {
      validateCastlingSafety(startLine, startColumn, endColumn, movingPiece.getIsWhite());
    }
    movingPiece.move(endLine, endColumn);

    if (isKingInCheck(movingPiece.getIsWhite())) {
      restoreMove(snapshot);
      throw new BadRequestException("Move leaves the king in check. ");
    }

    updateHalfmoveClock(movingPiece, capturedPiece);
    whiteTime = !movingPiece.getIsWhite();
    promotionPending = isPawnOnPromotionRank(movingPiece);
    if (promotionPending) {
      gameStatus = GameStatus.PROMOTION_REQUIRED;
      winnerWhite = null;
    } else {
      updateGameStatusAfterTurn();
    }
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

  private MoveSnapshot createMoveSnapshot(Piece movingPiece, int startLine, int startColumn, int endLine, int endColumn) {
    boolean castlingMove = isCastlingMove(movingPiece, startLine, startColumn, endLine, endColumn);
    boolean kingSide = endColumn > startColumn;
    int castlingRookStartColumn = castlingMove ? (kingSide ? 7 : 0) : -1;
    int castlingRookEndColumn = castlingMove ? (kingSide ? 5 : 3) : -1;
    Piece castlingRook = castlingMove ? table[startLine][castlingRookStartColumn] : null;

    return new MoveSnapshot(
            movingPiece,
            startLine,
            startColumn,
            movingPiece.isFirstMove(),
            table[endLine][endColumn],
            endLine,
            endColumn,
            table[endLine][endColumn] != null && table[endLine][endColumn].isFirstMove(),
            castlingMove,
            castlingRook,
            castlingRookStartColumn,
            castlingRookEndColumn,
            castlingRook != null && castlingRook.isFirstMove()
    );
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

  private void restoreMove(MoveSnapshot snapshot) {
    table[snapshot.startLine][snapshot.startColumn] = snapshot.movingPiece;
    table[snapshot.endLine][snapshot.endColumn] = snapshot.capturedPiece;
    snapshot.movingPiece.setPosition(snapshot.startLine, snapshot.startColumn);
    snapshot.movingPiece.setFirstMove(snapshot.movingPieceFirstMove);

    if (snapshot.capturedPiece != null) {
      snapshot.capturedPiece.setPosition(snapshot.endLine, snapshot.endColumn);
      snapshot.capturedPiece.setFirstMove(snapshot.capturedPieceFirstMove);
    }

    if (snapshot.castlingRook != null) {
      table[snapshot.startLine][snapshot.castlingRookEndColumn] = null;
      table[snapshot.startLine][snapshot.castlingRookStartColumn] = snapshot.castlingRook;
      snapshot.castlingRook.setPosition(snapshot.startLine, snapshot.castlingRookStartColumn);
      snapshot.castlingRook.setFirstMove(snapshot.castlingRookFirstMove);
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

  private void updateHalfmoveClock(Piece movingPiece, Piece capturedPiece) {
    if (movingPiece instanceof Pawn || capturedPiece != null) {
      halfmoveClock = 0;
    } else {
      halfmoveClock++;
    }
  }

  private boolean isPawnOnPromotionRank(Piece piece) {
    return piece instanceof Pawn && (piece.line == 0 || piece.line == 7);
  }

  private void updateGameStatusAfterTurn() {
    winnerWhite = null;
    if (!bothKingsPresent()) {
      gameStatus = GameStatus.ACTIVE;
      return;
    }

    String positionKey = currentPositionKey();
    recordPosition(positionKey);

    boolean currentKingInCheck = isKingInCheck(whiteTime);
    if (!hasAnyLegalMove(whiteTime)) {
      if (currentKingInCheck) {
        gameStatus = GameStatus.CHECKMATE;
        winnerWhite = !whiteTime;
      } else {
        gameStatus = GameStatus.STALEMATE;
      }
      return;
    }

    if (hasInsufficientMaterial()) {
      gameStatus = GameStatus.INSUFFICIENT_MATERIAL;
      return;
    }

    if (halfmoveClock >= 100) {
      gameStatus = GameStatus.FIFTY_MOVE_RULE;
      return;
    }

    if (positionOccurrences.getOrDefault(positionKey, 0) >= 3) {
      gameStatus = GameStatus.THREEFOLD_REPETITION;
      return;
    }

    gameStatus = currentKingInCheck ? GameStatus.CHECK : GameStatus.ACTIVE;
  }

  private boolean hasAnyLegalMove(boolean white) {
    for (int startLine = 0; startLine < table.length; startLine++) {
      for (int startColumn = 0; startColumn < table[startLine].length; startColumn++) {
        Piece piece = table[startLine][startColumn];
        if (piece == null || piece.getIsWhite() != white) {
          continue;
        }

        for (int endLine = 0; endLine < table.length; endLine++) {
          for (int endColumn = 0; endColumn < table[endLine].length; endColumn++) {
            if (canMoveLegally(startLine, startColumn, endLine, endColumn, white)) {
              return true;
            }
          }
        }
      }
    }

    return false;
  }

  private boolean canMoveLegally(int startLine, int startColumn, int endLine, int endColumn, boolean white) {
    Piece movingPiece = table[startLine][startColumn];
    if (movingPiece == null || movingPiece.getIsWhite() != white || table[endLine][endColumn] instanceof King) {
      return false;
    }

    MoveSnapshot snapshot = createMoveSnapshot(movingPiece, startLine, startColumn, endLine, endColumn);
    try {
      if (snapshot.castlingMove) {
        validateCastlingSafety(startLine, startColumn, endColumn, white);
      }
      movingPiece.move(endLine, endColumn);
      boolean legal = !isKingInCheck(white);
      restoreMove(snapshot);
      return legal;
    } catch (BadRequestException ex) {
      restoreMove(snapshot);
      return false;
    }
  }

  private boolean hasInsufficientMaterial() {
    int minorPieces = 0;
    int bishops = 0;
    int knights = 0;
    int bishopSquareColor = -1;

    for (int line = 0; line < table.length; line++) {
      for (int column = 0; column < table[line].length; column++) {
        Piece piece = table[line][column];
        if (piece == null || piece instanceof King) {
          continue;
        }

        char type = Character.toLowerCase(piece.getType());
        if (type == 'q' || type == 'r' || type == 'p') {
          return false;
        }

        if (type == 'h') {
          knights++;
          minorPieces++;
        } else if (type == 'b') {
          bishops++;
          minorPieces++;
          int currentSquareColor = (line + column) % 2;
          if (bishopSquareColor == -1) {
            bishopSquareColor = currentSquareColor;
          } else if (bishopSquareColor != currentSquareColor) {
            return false;
          }
        }
      }
    }

    if (minorPieces == 0) {
      return true;
    }

    if (minorPieces == 1 && (bishops == 1 || knights == 1)) {
      return true;
    }

    return knights == 0 && bishops > 0;
  }

  private boolean bothKingsPresent() {
    return findKing(true) != null && findKing(false) != null;
  }

  private void recordCurrentPosition() {
    recordPosition(currentPositionKey());
  }

  private void recordPosition(String positionKey) {
    positionOccurrences.put(positionKey, positionOccurrences.getOrDefault(positionKey, 0) + 1);
  }

  private String currentPositionKey() {
    StringBuilder key = new StringBuilder(80);
    for (int line = 0; line < table.length; line++) {
      for (int column = 0; column < table[line].length; column++) {
        Piece piece = table[line][column];
        key.append(piece == null ? '.' : piece.getType());
      }
    }

    key.append(whiteTime ? 'w' : 'b');
    key.append(castlingRightsKey());
    return key.toString();
  }

  private String castlingRightsKey() {
    StringBuilder rights = new StringBuilder(4);
    appendCastlingRight(rights, true, 7, 7, 'K');
    appendCastlingRight(rights, true, 7, 0, 'Q');
    appendCastlingRight(rights, false, 0, 7, 'k');
    appendCastlingRight(rights, false, 0, 0, 'q');
    return rights.toString();
  }

  private void appendCastlingRight(StringBuilder rights, boolean white, int line, int rookColumn, char marker) {
    Piece king = table[line][4];
    Piece rook = table[line][rookColumn];
    if (king instanceof King
            && rook instanceof Rook
            && king.getIsWhite() == white
            && rook.getIsWhite() == white
            && king.isFirstMove()
            && rook.isFirstMove()) {
      rights.append(marker);
    }
  }

  public void promotePawn(int posLine, int posColumn, char promotionType) {
    if (gameStatus.isGameOver()) throw new BadRequestException("Game is over. ");
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
    promotionPending = false;
    updateGameStatusAfterTurn();
    //printBoard();
  }

  private static class MoveSnapshot {
    private final Piece movingPiece;
    private final int startLine;
    private final int startColumn;
    private final boolean movingPieceFirstMove;
    private final Piece capturedPiece;
    private final int endLine;
    private final int endColumn;
    private final boolean capturedPieceFirstMove;
    private final boolean castlingMove;
    private final Piece castlingRook;
    private final int castlingRookStartColumn;
    private final int castlingRookEndColumn;
    private final boolean castlingRookFirstMove;

    private MoveSnapshot(Piece movingPiece, int startLine, int startColumn, boolean movingPieceFirstMove,
                         Piece capturedPiece, int endLine, int endColumn, boolean capturedPieceFirstMove,
                         boolean castlingMove, Piece castlingRook, int castlingRookStartColumn,
                         int castlingRookEndColumn, boolean castlingRookFirstMove) {
      this.movingPiece = movingPiece;
      this.startLine = startLine;
      this.startColumn = startColumn;
      this.movingPieceFirstMove = movingPieceFirstMove;
      this.capturedPiece = capturedPiece;
      this.endLine = endLine;
      this.endColumn = endColumn;
      this.capturedPieceFirstMove = capturedPieceFirstMove;
      this.castlingMove = castlingMove;
      this.castlingRook = castlingRook;
      this.castlingRookStartColumn = castlingRookStartColumn;
      this.castlingRookEndColumn = castlingRookEndColumn;
      this.castlingRookFirstMove = castlingRookFirstMove;
    }
  }
}
