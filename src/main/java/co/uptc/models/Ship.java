package co.uptc.models;

import java.util.List;
import lombok.Getter;

@Getter
public class Ship {
    protected Long idShip;
    protected int size;
    protected Position position;
    protected boolean isRotated;
    protected boolean isSunk;
    protected List<Position> positions;

    public Ship(Long idShip, int size, Position position, boolean isRotated, boolean isSunk) {
        this.idShip = idShip;
        this.size = size;
        this.position = position;
        this.isRotated = isRotated;
        this.isSunk = isSunk;
    }

    public void generatePositions(){
        int x = position.getX();
        int y = position.getY();
        positions = new java.util.ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (isRotated) {
                positions.add(new Position(x + i, y));
            } else {
                positions.add(new Position(x, y + i));
            }
        }
    }

    public void determinateIsSunk(List<Shot> shots) {
        if (positions == null || positions.isEmpty()) {
            isSunk = false;
            return;
        }
    
        boolean allHit = positions.stream().allMatch(shipPos ->
            shots.stream().anyMatch(shot -> {
                Position shotPos = shot.getPosition();
                return shipPos.getX() == shotPos.getX() && shipPos.getY() == shotPos.getY();
            })
        );
    
        this.isSunk = allHit;
    }
}