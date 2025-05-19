package co.uptc.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlayerDtoGame {
    private String username;
    private String gameId;

    public PlayerDtoGame(String username, String gameId) {
        this.username = username;
        this.gameId = gameId;
    }

}
