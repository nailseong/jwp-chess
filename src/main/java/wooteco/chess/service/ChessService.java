package wooteco.chess.service;

import org.springframework.stereotype.Service;
import wooteco.chess.domain.Chess;
import wooteco.chess.domain.board.BoardGenerator;
import wooteco.chess.domain.coordinate.Coordinate;
import wooteco.chess.domain.piece.Team;
import wooteco.chess.dto.ChessResponseDto;
import wooteco.chess.dto.ResponseDto;
import wooteco.chess.entity.Move;
import wooteco.chess.repository.MoveRepository;
import wooteco.chess.repository.RoomRepository;
import wooteco.chess.support.ChessResponseCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChessService {
    private MoveRepository moveRepository;
    private RoomService roomService;

    private Map<Long, Chess> cachedChess = new HashMap<>();

    public ChessService(MoveRepository moveRepository, RoomService roomService) {
        this.moveRepository = moveRepository;
        this.roomService = roomService;
    }

    public ResponseDto move(Move move, String userPassword){
        if (!cachedChess.containsKey(move.getRoomId())) {
            return ResponseDto.fail(ChessResponseCode.CANNOT_FIND_ROOM_ID);
        }

        Chess chess = cachedChess.get(move.getRoomId());
        if (!chess.isTurnOf(roomService.checkAuthentication(move.getRoomId(), userPassword))) {
            return ResponseDto.fail(ChessResponseCode.NOT_YOUR_TURN);
        }

        chess.move(Coordinate.of(move.getSource()), Coordinate.of(move.getTarget()));
        cachedChess.put(move.getRoomId(), chess);
        moveRepository.save(move);

        if (!chess.isKingAlive()) {
            return ResponseDto.success(ChessResponseCode.WIN);
        }
        return ResponseDto.success();
    }

    public ResponseDto getMovableWay(Long roomId, Coordinate coordinate, String userPassword) {
        if (!cachedChess.containsKey(roomId)) {
            return ResponseDto.fail(ChessResponseCode.CANNOT_FIND_ROOM_ID);
        }

        Chess chess = cachedChess.get(roomId);
        Team team = roomService.checkAuthentication(roomId, userPassword);
        if (!(chess.isTurnOf(team) && chess.isTurnOf(coordinate))) {
            return ResponseDto.fail(ChessResponseCode.NOT_YOUR_TURN);
        }
        return ResponseDto.success(chess.getMovableWay(coordinate));
    }

    public ResponseDto renew(Long roomId){
        if (!cachedChess.containsKey(roomId)) {
            load(roomId);
        }
        Chess chess = cachedChess.get(roomId);
        if (!chess.isKingAlive()) {
            return ResponseDto.success(ChessResponseCode.LOSE);
        }
        return ResponseDto.success(ChessResponseDto.of(chess));
    }

    private void load(Long roomId){
        Chess chess = new Chess(BoardGenerator.create());
        List<Move> moves = moveRepository.findByRoomId(roomId)
                .orElse(null);
        for (Move move : moves) {
            chess.move(Coordinate.of(move.getSource()), Coordinate.of(move.getTarget()));
        }
        cachedChess.put(roomId, chess);
    }
}