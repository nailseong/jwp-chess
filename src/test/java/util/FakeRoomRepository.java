package util;

import chess.domain.room.Room;
import chess.dto.response.RoomPageDto;
import chess.dto.response.RoomResponseDto;
import chess.exception.NotFoundException;
import chess.repository.RoomRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FakeRoomRepository implements RoomRepository {

    private final Map<Integer, Room> storage = new HashMap<>();
    private int series = 1;

    @Override
    public Room get(final int roomId) {
        final Room room = storage.get(roomId);
        if (room == null) {
            throw new NotFoundException("존재하지 않는 방 입니다.");
        }
        return room;
    }

    @Override
    public RoomPageDto getAll(final int page, final int size) {
        final int lastPage = (int) Math.ceil((double) storage.size() / size);
        final List<RoomResponseDto> responseDtos = storage.entrySet()
                .stream()
                .map(entry -> RoomResponseDto.of(
                        entry.getKey(),
                        entry.getValue().getName(),
                        entry.getValue().getGameStatus().getValue()))
                .skip((long) (page - 1) * size)
                .limit(10)
                .collect(Collectors.toList());
        return RoomPageDto.of(page, lastPage, responseDtos);
    }

    @Override
    public int add(final Room room) {
        storage.put(series, room);
        return series++;
    }

    @Override
    public void update(final int roomId, final Room room) {
        storage.put(roomId, room);
    }

    @Override
    public void remove(final int roomId) {
        storage.remove(roomId);
    }

    public void deleteAll() {
        storage.clear();
        series = 1;
    }
}
