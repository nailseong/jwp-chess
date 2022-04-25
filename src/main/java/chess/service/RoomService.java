package chess.service;

import chess.dao.RoomDao;
import chess.domain.GameStatus;
import chess.domain.chesspiece.Color;
import chess.dto.CurrentTurnDto;
import chess.dto.RoomStatusDto;
import org.springframework.stereotype.Service;

@Service
public class RoomService {

    private final RoomDao roomDao;

    public RoomService(final RoomDao roomDao) {
        this.roomDao = roomDao;
    }

    public boolean isExistRoom(final int roomId) {
        return roomDao.isExistId(roomId);
    }

    public void createRoom(final String roomName) {
        roomDao.save(roomName, GameStatus.READY, Color.WHITE);
    }

    public void deleteRoom(final String roomName) {
        checkRoomExist(roomName);

        final RoomStatusDto dto = roomDao.findStatusByName(roomName);
        if (dto.getGameStatus().isEnd()) {
            roomDao.delete(roomName);
        }
    }

    public CurrentTurnDto findCurrentTurn(final String roomName) {
        checkRoomExist(roomName);
        return roomDao.findCurrentTurnByName(roomName);
    }

    private void checkRoomExist(final String roomName) {
        if (!roomDao.isExistName(roomName)) {
            throw new IllegalArgumentException("존재하지 않는 방 입니다.");
        }
    }
}
