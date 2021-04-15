package chess.contoller;

import chess.dto.web.BoardDto;
import chess.dto.web.GameStatusDto;
import chess.dto.web.PointDto;
import chess.dto.web.RoomDto;
import chess.dto.web.UsersInRoomDto;
import chess.service.SpringChessService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/room")
public class SpringChessApiController {

    private SpringChessService springChessService;

    public SpringChessApiController(SpringChessService springChessService) {
        this.springChessService = springChessService;
    }

    @PostMapping
    @ResponseBody
    private Map<String, String> createRoom(@RequestBody RoomDto roomDto) {
        Map<String, String> result = new HashMap<>();
        result.put("result", "success");
        result.put("roomId", springChessService.create(roomDto));
        return result;
    }

    @GetMapping("{id}/statistics")
    @ResponseBody
    private UsersInRoomDto usersInRoom(@PathVariable String id) {
        return springChessService.usersInRoom(id);
    }

    @GetMapping("{id}/getGameStatus")
    @ResponseBody
    private GameStatusDto gameStatus(@PathVariable String id){
        return springChessService.gameStatus(id);
    }

    @PutMapping("{id}/start")
    @ResponseBody
    private BoardDto startGame(@PathVariable String id) {
        return springChessService.start(id);
    }

    @PutMapping(value = "{id}/exit", produces = "application/json")
    @ResponseBody
    private String exitGame(@PathVariable String id){
        springChessService.exit(id);
        return "\"success\"";
    }

    @PutMapping
    @ResponseBody
    private String closeRoom(@RequestBody Map<String, String> body) {
        String id = body.get("id");
        springChessService.close(id);
        return "\"success\"";
    }

    @GetMapping("/{id}/movablePoints/{point}")
    @ResponseBody
    private List<PointDto> movablePoints(@PathVariable String id, @PathVariable String point) {
        return springChessService.movablePoints(id, point);
    }
}
