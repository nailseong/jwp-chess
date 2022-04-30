package chess.domain.room;

import chess.domain.ChessGame;
import chess.domain.GameStatus;
import chess.domain.chessboard.ChessBoardFactory;
import chess.domain.chesspiece.Color;

public class Room {

    private final RoomName name;
    private final Password password;
    private final ChessGame chessGame;

    public Room(final String name, final String password, final ChessGame chessGame) {
        this.name = new RoomName(name);
        this.password = Password.fromPlain(password);
        this.chessGame = chessGame;
    }

    public Room(final String name, final String password) {
        this.name = new RoomName(name);
        this.password = Password.fromPlain(password);
        this.chessGame = new ChessGame(ChessBoardFactory.createChessBoard());
    }

    public Room(final RoomName name, final Password password, final ChessGame chessGame) {
        this.name = name;
        this.password = password;
        this.chessGame = chessGame;
    }

    public void startGame() {
        chessGame.start();
    }

    public boolean canRemove(final String plainPassword) {
        if (chessGame.isPlaying()) {
            throw new IllegalArgumentException("게임이 진행 중입니다.");
        }
        if (plainPassword.isBlank()) {
            throw new IllegalArgumentException("요청에 비밀번호가 존재하지 않습니다.");
        }
        if (password.isSame(plainPassword)) {
            return true;
        }
        throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

    public String getName() {
        return name.getValue();
    }

    public GameStatus getGameStatus() {
        return chessGame.getGameStatus();
    }

    public Color getCurrentTurn() {
        return chessGame.getCurrentTurn();
    }

    public String getPassword() {
        return password.getHashPassword();
    }

    public ChessGame getChessGame() {
        return chessGame;
    }
}
