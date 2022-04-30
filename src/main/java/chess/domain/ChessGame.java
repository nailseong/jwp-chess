package chess.domain;

import chess.domain.chessboard.ChessBoard;
import chess.domain.chessboard.ChessBoardFactory;
import chess.domain.chesspiece.Color;
import chess.domain.position.Position;
import chess.domain.result.EndResult;

public class ChessGame {

    private ChessBoard chessBoard;
    private GameStatus gameStatus;

    public ChessGame(final ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
        this.gameStatus = GameStatus.READY;
    }

    public ChessGame(final ChessBoard chessBoard, final GameStatus gameStatus) {
        this.chessBoard = chessBoard;
        this.gameStatus = gameStatus;
    }

    public void move(final Position from, final Position to) {
        gameStatus.checkPlaying();

        chessBoard.move(from, to);
        if (chessBoard.isKingDie()) {
            gameStatus = GameStatus.KING_DIE;
        }
    }

    public Score calculateScore() {
        gameStatus.checkPlaying();
        return chessBoard.calculateScore();
    }

    public void start() {
        gameStatus.checkReady();
        if (chessBoard.findAllPiece().isEmpty()) {
            chessBoard = ChessBoardFactory.createChessBoard();
        }
        gameStatus = GameStatus.PLAYING;
    }

    public EndResult end() {
        gameStatus = GameStatus.END;
        final Score score = new Score(chessBoard.findAllPiece());
        return new EndResult(score);
    }

    public boolean isPlaying() {
        return gameStatus.equals(GameStatus.PLAYING);
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public Color getCurrentTurn() {
        return chessBoard.getCurrentTurnColor();
    }

    public ChessBoard getChessBoard() {
        return chessBoard;
    }
}
