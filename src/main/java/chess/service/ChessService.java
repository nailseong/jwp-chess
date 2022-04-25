package chess.service;

import chess.dao.ChessPieceDao;
import chess.dao.RoomDao;
import chess.domain.ChessGame;
import chess.domain.GameStatus;
import chess.domain.Score;
import chess.domain.chessboard.ChessBoard;
import chess.domain.chessboard.ChessBoardFactory;
import chess.domain.chesspiece.ChessPiece;
import chess.domain.chesspiece.Color;
import chess.domain.position.Position;
import chess.domain.result.EndResult;
import chess.domain.result.MoveResult;
import chess.domain.result.StartResult;
import chess.dto.ChessPieceDto;
import chess.dto.ChessPieceMapper;
import chess.dto.CurrentTurnDto;
import chess.dto.MoveRequestDto;
import chess.dto.RoomStatusDto;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ChessService {

    private final ChessPieceDao chessPieceDao;
    private final RoomDao roomDao;

    public ChessService(final ChessPieceDao chessPieceDao, final RoomDao roomDao) {
        this.chessPieceDao = chessPieceDao;
        this.roomDao = roomDao;
    }

    public List<ChessPieceDto> findAllPiece(final String roomName) {
        checkRoomExist(roomName);
        return chessPieceDao.findAllByRoomName(roomName);
    }

    public void initPiece(final String roomName) {
        checkRoomExist(roomName);
        final ChessGame chessGame = findGameByRoomName(roomName);

        final StartResult startResult = chessGame.start();
        updateChessPiece(roomName, startResult.getPieceByPosition());
        updateRoomStatusTo(roomName, GameStatus.PLAYING);
    }

    public MoveResult move(final String roomName, MoveRequestDto requestDto) {
        checkRoomExist(roomName);
        final ChessGame chessGame = findGameByRoomName(roomName);
        final Position from = requestDto.getFrom();
        final Position to = requestDto.getTo();

        final MoveResult moveResult = chessGame.move(from, to);
        updatePosition(roomName, from, to);
        updateRoom(roomName, moveResult.getGameStatus(), moveResult.getCurrentTurn());

        return moveResult;
    }

    private void updatePosition(final String roomName, final Position from, final Position to) {
        chessPieceDao.deleteByPosition(roomName, to);
        chessPieceDao.update(roomName, from, to);
    }

    public Score findScore(final String roomName) {
        checkRoomExist(roomName);
        final ChessGame chessGame = findGameByRoomName(roomName);

        return chessGame.calculateScore();
    }

    public EndResult result(final String roomName) {
        checkRoomExist(roomName);
        final ChessGame chessGame = findGameByRoomName(roomName);

        final EndResult result = chessGame.end();
        updateRoomStatusTo(roomName, GameStatus.END);

        return result;
    }

    private void checkRoomExist(final String roomName) {
        if (!roomDao.isExistName(roomName)) {
            throw new IllegalArgumentException("존재하지 않는 방 입니다.");
        }
    }

    private ChessGame findGameByRoomName(final String roomName) {
        Map<Position, ChessPiece> pieceByPosition = initAllPiece(roomName);
        Color currentTurn = initCurrentTurn(roomName);
        GameStatus gameStatus = initGameStatus(roomName);

        return new ChessGame(new ChessBoard(pieceByPosition, currentTurn), gameStatus);
    }

    private Map<Position, ChessPiece> initAllPiece(final String roomName) {
        final List<ChessPieceDto> dtos = chessPieceDao.findAllByRoomName(roomName);
        if (dtos.isEmpty()) {
            return ChessBoardFactory.createInitPieceByPosition();
        }

        return dtos.stream()
                .collect(Collectors.toMap(
                        chessPieceDto -> Position.from(chessPieceDto.getPosition()),
                        chessPieceDto -> ChessPieceMapper.toChessPiece(chessPieceDto.getPieceType(),
                                chessPieceDto.getColor())
                ));
    }

    private Color initCurrentTurn(final String roomName) {
        final CurrentTurnDto dto = roomDao.findCurrentTurnByName(roomName);
        if (Objects.isNull(dto)) {
            return Color.WHITE;
        }
        return dto.getCurrentTurn();
    }

    private GameStatus initGameStatus(final String roomName) {
        final RoomStatusDto dto = roomDao.findStatusByName(roomName);
        if (Objects.isNull(dto)) {
            return GameStatus.READY;
        }
        return dto.getGameStatus();
    }

    private void updateChessPiece(final String roomName, final Map<Position, ChessPiece> pieceByPosition) {
        chessPieceDao.deleteAllByRoomName(roomName);
        chessPieceDao.saveAll(roomName, pieceByPosition);
    }

    private void updateRoom(final String roomName, final GameStatus gameStatus, final Color currentTurn) {
        roomDao.update(roomName, gameStatus, currentTurn);
    }

    private void updateRoomStatusTo(final String roomName, final GameStatus gameStatus) {
        roomDao.updateStatusTo(roomName, gameStatus);
    }
}
