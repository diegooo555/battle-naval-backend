package co.uptc.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerDto {
    private String username;
    private String gameId;
    private String playerId;

    public PlayerDto(String username, String gameId, String playerId) {
        this.playerId = playerId;
        this.username = username;
        this.gameId = gameId;
    }
}
