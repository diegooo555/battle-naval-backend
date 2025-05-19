package co.uptc.models;

import co.uptc.constants.GameStatus;
import co.uptc.constants.PlayerStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
public class GameRoom {

    protected String gameId;
    protected List<Player> listPlayers;
    protected String currentTurn;
    protected Player winner;
    private GameStatus gameStatus;

    public GameRoom(String idGame) {
        this.gameId = idGame;
        this.gameStatus = GameStatus.WAITING_PLAYERS;
        listPlayers = new ArrayList<>();
    }

    public void setShipsPlayer(String playerId, List<Ship> ships) {
        listPlayers.stream().filter(player -> player.getPlayerId().equals(playerId)).findFirst().ifPresent(player -> {
            player.getBoard().setShips(ships);
            player.getBoard().generatePositionsShips();
        });
    }

    public void revalidateStatetGame(){
        for(Player player: listPlayers){
            int shipsSunk = 0;
            for(Ship ship: player.getBoard().getShips()){
                if(ship.isSunk){
                    shipsSunk++;
                }
            }
            if(shipsSunk == 5){
                player.setPlayerStatus(PlayerStatus.LOSE);
                winner = listPlayers.stream().filter(p ->!p.getPlayerId().equals(player.getPlayerId())).findFirst().orElse(null);
                winner.setPlayerStatus(PlayerStatus.WIN);
                gameStatus = GameStatus.FINISHED;
            }
        }
    }

    public void switchTurn() {
        Player player1 = listPlayers.get(0);
        Player player2 = listPlayers.get(1);
    
        currentTurn = currentTurn.equals(player1.getPlayerId()) ? player2.getPlayerId() : player1.getPlayerId();
    }    

    public String addPlayer(String username){
        String playerId = UUID.randomUUID().toString();
        Player newPlayer = new Player(playerId, username, gameId);
        listPlayers.add(newPlayer);
        return playerId;
    }

    public String getInfoPlayers(){
        String infoPlayers = "";
        for (Player player : listPlayers) {
            infoPlayers +=  player.getPlayerId() + "-" + player.getName() + "/";
        }
        return infoPlayers;
    }
}
