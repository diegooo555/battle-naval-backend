package co.uptc.constants;


import co.uptc.models.Position;
import co.uptc.models.Ship;

import java.util.List;
import java.util.Arrays;
import java.util.Random;

public class DefaultShipPositions {

    public static final List<Ship> DEFAULT_POSITIONS_1 = Arrays.asList(
            new Ship(0L, 5, new Position(0, 2), false, false),
            new Ship(1L, 4, new Position(3, 3), false, false),
            new Ship(2L, 3, new Position(4, 9), true, false),
            new Ship(3L, 3, new Position(8, 3), false, false),
            new Ship(4L, 2, new Position(6, 3), false, false)
    );

    public static final List<Ship> DEFAULT_POSITIONS_2 = Arrays.asList(
            new Ship(0L, 5, new Position(3, 1), true, false),
            new Ship(1L, 4, new Position(5, 3), false, false),
            new Ship(2L, 3, new Position(2, 3), false, false),
            new Ship(3L, 3, new Position(7, 3), false, false),
            new Ship(4L, 2, new Position(9, 3), false, false)
    );

    public static List<Ship> getRandomDefaultPositions() {
        return new Random().nextBoolean() ? DEFAULT_POSITIONS_1 : DEFAULT_POSITIONS_2;
    }
}