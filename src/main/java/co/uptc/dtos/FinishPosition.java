package co.uptc.dtos;

import co.uptc.models.Ship;
import lombok.Getter;

import java.util.List;

@Getter
public class FinishPosition {
    private String gameId;
    private String playerId;
    private List<Ship> ships;

    public void showInfoShips(){
        ships.forEach(ship -> {
            System.out.println("BArco: " + ship.getIdShip()
                    + " Size: " + ship.getSize() +
                    " Estado: " + ship.isSunk() +
                    " Posicion: " + ship.getPosition().getX() + "," + ship.getPosition().getY());
        });
    }
}