package chess.repository;

import chess.dao.ChessPieceDao;
import chess.dao.RoomDao;
import chess.domain.ChessGame;
import chess.domain.chessboard.ChessBoard;
import chess.domain.chesspiece.ChessPiece;
import chess.domain.position.Position;
import chess.domain.room.Room;
import chess.dto.response.RoomPageDto;
import chess.dto.response.RoomResponseDto;
import chess.entity.ChessPieceEntity;
import chess.entity.RoomEntity;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class RoomRepositoryImpl implements RoomRepository {

    private final RoomDao roomDao;
    private final ChessPieceDao chessPieceDao;

    public RoomRepositoryImpl(final RoomDao roomDao, final ChessPieceDao chessPieceDao) {
        this.roomDao = roomDao;
        this.chessPieceDao = chessPieceDao;
    }

    @Override
    public Room get(final int roomId) {
        final RoomEntity roomEntity = roomDao.findById(roomId);
        return toRoom(roomEntity);
    }

    @Override
    public RoomPageDto getAll(final int page, final int size) {
        final int totalSize = roomDao.count();
        final int lastPage = (int) Math.ceil((double) totalSize / size);

        final List<RoomResponseDto> responseDtos = roomDao.findAll(page, size)
                .stream()
                .map(roomEntity -> RoomResponseDto.of(
                        roomEntity.getRoomId(),
                        roomEntity.getName(),
                        roomEntity.getGameStatus()
                ))
                .collect(Collectors.toList());
        return RoomPageDto.of(page, lastPage, responseDtos);
    }

    private Room toRoom(final RoomEntity roomEntity) {
        final List<ChessPieceEntity> chessPieceEntityList = chessPieceDao.findAllEntityByRoomId(roomEntity.getRoomId());

        final Map<Position, ChessPiece> pieceByPosition = chessPieceEntityList.stream()
                .collect(Collectors.toMap(
                        ChessPieceEntity::toPosition,
                        ChessPieceEntity::toChessPiece
                ));
        final ChessBoard chessBoard = new ChessBoard(pieceByPosition, roomEntity.toCurrentTurn());
        final ChessGame chessGame = new ChessGame(chessBoard, roomEntity.toGameStatus());

        return new Room(roomEntity.toRoomName(), roomEntity.toPassword(), chessGame);
    }

    @Override
    public int add(final Room room) {
        final Optional<RoomEntity> existName = roomDao.findByName(room.getName());
        if (existName.isPresent()) {
            throw new IllegalArgumentException("이름이 같은 방이 이미 존재합니다.");
        }

        final RoomEntity roomEntity = new RoomEntity(
                room.getName(),
                room.getGameStatus(),
                room.getCurrentTurn(),
                room.getPassword()
        );
        return roomDao.save(roomEntity);
    }

    @Override
    public void update(final int roomId, final Room room) {
        roomDao.updateById(roomId, room.getGameStatus(), room.getCurrentTurn());
    }

    @Override
    public void remove(final int roomId) {
        roomDao.deleteById(roomId);
    }
}
