package co.uptc.services;

import co.uptc.constants.GameStatus;
import co.uptc.constants.PlayerStatus;
import co.uptc.constants.ShotResult;
import co.uptc.dtos.FinishPosition;
import co.uptc.models.GameRoom;
import co.uptc.models.Player;
import co.uptc.models.Position;
import co.uptc.models.Session;
import co.uptc.models.Ship;
import co.uptc.models.Shot;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class GameManager {
    private final GameTimerService gameTimerService;
    private final ConcurrentLinkedQueue<GameRoom> waitingGames = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<String, GameRoom> activeGames = new ConcurrentHashMap<>();

    public GameManager(GameTimerService gameTimerService) {
        this.gameTimerService = gameTimerService;
    }

    public synchronized Session findOrCreateGame(String username) {
        GameRoom gameRoom = waitingGames.peek();
        String playerId;
        String gameId;

        if (gameRoom == null) {
            gameId = UUID.randomUUID().toString();
            gameRoom = new GameRoom(gameId);
            playerId = gameRoom.addPlayer(username);
            waitingGames.offer(gameRoom);
        } else {
            gameId = gameRoom.getGameId();
            playerId = gameRoom.addPlayer(username);
            gameRoom.setGameStatus(GameStatus.POSIOTIONING);
            activeGames.put(gameRoom.getGameId(), gameRoom);
            waitingGames.poll();
            gameTimerService.startPlacementTimer(gameRoom,90);
        }
        return new Session(gameId, playerId);
    }

    public synchronized GameRoom newShot(String gameId, String playerIdShot, Shot shot) {
        GameRoom gameRoom = activeGames.get(gameId);
        if (gameRoom == null) return null;
        Player player = gameRoom.getListPlayers().stream()
                .filter(p -> !p.getPlayerId().equals(playerIdShot))
                .findFirst()
                .orElse(null);
        if (player == null) return null;
        if(!player.getPlayerId().equals(gameRoom.getCurrentTurn())) return gameRoom;
    
        for (Ship ship : player.getBoard().getShips()) {
            for (Position position : ship.getPositions()) {
                if (shot.getPosition().getX() == position.getX() && shot.getPosition().getY() == position.getY()) {
                    shot.setResult(ShotResult.HIT);
                    player.newShot(shot);
                    gameTimerService.cancelTimer(gameId);

                    boolean wasAlreadySunk = ship.isSunk();
                    ship.determinateIsSunk(player.getBoard().getShots());

                    if (!wasAlreadySunk && ship.isSunk()) {
                        gameRoom.setLastSunkShip(ship.getIdShip());
                    }
                    
                    gameRoom.revalidateStatetGame();
                    if (gameRoom.getGameStatus() == GameStatus.FINISHED) {
                        activeGames.remove(gameId);
                        gameTimerService.cancelTimer(gameId);
                        return gameRoom;
                    }
                    gameTimerService.startTurnTimer(gameRoom, 15);
                    return gameRoom;
                }
            }
        }
        player.newShot(shot);
        gameTimerService.cancelTimer(gameId);
        gameRoom.switchTurn();
        gameTimerService.startTurnTimer(gameRoom, 15);
        return gameRoom;
    }
    
    public synchronized GameRoom findGameRoom(String gameId) {
        return activeGames.get(gameId);
    }

    public synchronized String findNameOponnent(String gameId, String username) {
        GameRoom gameRoom = findGameRoom(gameId);
        String nameOpponent = gameRoom.getListPlayers().stream()
                .map(Player::getName)
                .filter(name -> !name.equals(username))
                .findFirst()
                .orElse(null);
        return nameOpponent;
    }

    public synchronized void cancelTimerGame(String gameId) {
        gameTimerService.cancelTimer(gameId);
    }

    public synchronized boolean validatePlayerReady(GameRoom gameRoom, FinishPosition finishPosition) {
        gameRoom.getListPlayers().forEach(player -> {
        if(player.getPlayerId().equals(finishPosition.getPlayerId()) && gameRoom.getGameStatus() == GameStatus.POSIOTIONING){
            player.setPlayerStatus(PlayerStatus.PLAYING);
            gameRoom.setShipsPlayer(player.getPlayerId(), finishPosition.getShips());
            cancelTimerGame(gameRoom.getGameId());
        }});

        boolean isAllPlayersReady  = gameRoom.getListPlayers().stream().allMatch(player -> player.getPlayerStatus() == PlayerStatus.PLAYING);

        if(isAllPlayersReady){
            gameRoom.setGameStatus(GameStatus.IN_PROGRESS);
            int random = ThreadLocalRandom.current().nextInt(2); 
            gameRoom.setCurrentTurn(gameRoom.getListPlayers().get(random).getPlayerId());
            gameTimerService.startTurnTimer(gameRoom, 15);
        }
        return isAllPlayersReady;
    }

    public synchronized GameRoom disconnectGame(String gameId) {
        GameRoom gameRoom = findGameRoom(gameId);
        gameRoom.setGameStatus(GameStatus.DISCONNECTED);
        gameTimerService.cancelTimer(gameId);
        activeGames.remove(gameId);
        return gameRoom;
    }
}