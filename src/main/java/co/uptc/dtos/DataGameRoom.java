package co.uptc.dtos;

import co.uptc.models.GameRoom;
import lombok.Getter;

@Getter
public class DataGameRoom {
    private GameRoom content;

    public DataGameRoom(GameRoom content) {
        this.content = content;    
    }
}