package co.uptc.services;

import co.uptc.constants.DefaultShipPositions;
import co.uptc.constants.GameStatus;
import co.uptc.constants.PlayerStatus;
import co.uptc.models.GameRoom;
import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Service
public class GameTimerService {

    private final ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(0);
    private final ConcurrentHashMap<String, ScheduledFuture<?>> timers = new ConcurrentHashMap<>();

    @PostConstruct
    public void configureScheduler() {
        scheduler.setMaximumPoolSize(Integer.MAX_VALUE);
        scheduler.setKeepAliveTime(60, TimeUnit.SECONDS);
        scheduler.allowCoreThreadTimeOut(true);
    }

    public void startPlacementTimer(GameRoom gameRoom, int seconds) {
        cancelTimer(gameRoom.getGameId());

        ScheduledFuture<?> future = scheduler.schedule(() -> {
            gameRoom.getListPlayers().forEach(player -> {
                if (player.getPlayerStatus() != PlayerStatus.PLAYING) {
                    player.getBoard().setShips(DefaultShipPositions.getRandomDefaultPositions());
                    player.setPlayerStatus(PlayerStatus.PLAYING);
                    player.getBoard().generatePositionsShips();
                }
            });
            gameRoom.setGameStatus(GameStatus.IN_PROGRESS);
            int random = ThreadLocalRandom.current().nextInt(2); 
            gameRoom.setCurrentTurn(gameRoom.getListPlayers().get(random).getPlayerId());
            timers.remove(gameRoom.getGameId());
        }, seconds, TimeUnit.SECONDS);

        timers.put(gameRoom.getGameId(), future);
    }

    public void cancelTimer(String gameId) {
        ScheduledFuture<?> future = timers.remove(gameId);
        if (future != null && !future.isDone()) {
            future.cancel(true);
        }
    }

    public void startTurnTimer(GameRoom gameRoom, int intervalSeconds) {
        cancelTimer(gameRoom.getGameId());
    
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            if (gameRoom.getGameStatus() == GameStatus.IN_PROGRESS) {
                gameRoom.switchTurn();
                startTurnTimer(gameRoom, intervalSeconds);
            }
        }, intervalSeconds, TimeUnit.SECONDS);
    
        timers.put(gameRoom.getGameId(), future);
    }
}
