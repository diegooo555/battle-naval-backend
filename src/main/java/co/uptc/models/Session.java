package co.uptc.models;

import lombok.Getter;

@Getter
public class Session {
    private String gameId;
    private String playerId;

    public Session(String gameId, String playerId) {
        this.gameId = gameId;
        this.playerId = playerId;
    }
}
