package byog.Core;

import byog.TileEngine.TETile;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Random;

/**
 * Random world generator
 *
 * @author Kevin Nguyen and Borhan Rafiq
 */
public class WorldGenerator implements Serializable {
    int numFlowers = 0;
    static final int WORLD_WIDTH = 80;
    static final int WORLD_HEIGHT = 30;
    private static final long serialVersionUID = 123123123123123L;
    private boolean firstDone = false;
    private int makeHorizontal = 1;
    Structure starterRoom;
    boolean twoPlayers;
    Player player;
    Player player2;
    TETile[][] world;
    ArrayDeque<Structure> existingRooms = new ArrayDeque<>();
    ArrayDeque<Hallway> existingHallways = new ArrayDeque<>();


    public WorldGenerator() {

    }

    public WorldGenerator(TETile[][] w, boolean twoPlayers) {
        this.world = w;
        this.twoPlayers = twoPlayers;
    }

    /**
     * Creates the state of the this.world, populating it with rooms and hallways.
     */
    void generateRandomStructures(Random r) {
        int randomNumRooms = RandomUtils.uniform(r, 10, 16);
        do {
            if (!firstDone) {
                try {
                    //Starter room begins somewhere in the middle
                    int ranX = WORLD_WIDTH / 2;
                    int ranY = WORLD_HEIGHT / 2;
                    Position randomPos = new Position(ranX, ranY);
                    Structure randomStructure = new Structure(this.world, randomPos, r);
                    Hallway firstHallway = connectWithRandomHallway(randomStructure, r);
                    firstHallway.connectToRoom(randomStructure, r);
                    firstHallway.constructStructure();
                    randomStructure.constructStructure();
                    existingHallways.add(firstHallway);
                    existingRooms.add(randomStructure);
                    firstDone = true;
                    starterRoom = randomStructure;
                    if (!this.twoPlayers) {
                        Position playerPos = Structure.fstLstFloorCoordinate(randomStructure, r);
                        player = new Player(this, playerPos, 1);
                    } else {
                        Position playerPos = randomStructure.floorCoordinates.getFirst();
                        Position player2Pos = randomStructure.floorCoordinates.getLast();
                        player = new Player(this, playerPos, 1);
                        player2 = new Player(this, player2Pos, 2);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    generateRandomStructures(r);
                }
            } else {
                try {
                    int ranX = RandomUtils.uniform(r, 0, WORLD_WIDTH - 1);
                    int ranY = RandomUtils.uniform(r, 0, WORLD_HEIGHT - 1);
                    Position randomPos = new Position(ranX, ranY);
                    Structure randomStructure = new Structure(this.world, randomPos, r);
                    Hallway newHallToConnect = connectWithRandomHallway(randomStructure, r);
                    newHallToConnect.connectToRoom(randomStructure, r);
                    if (doesNotOverlap(newHallToConnect)) {
                        if (isConnectedToAHallway(newHallToConnect)) {
                            if (doesNotOverlap(randomStructure)) {
                                newHallToConnect.constructStructure();
                                randomStructure.constructStructure();
                                existingHallways.add(newHallToConnect);
                                existingRooms.add(randomStructure);
                            }
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    generateRandomStructures(r);
                }
            }
        } while (existingRooms.size() <= randomNumRooms);
        //reset all static variables once world is built
        Structure.numOfPseudoHallways = 0;
    }

    /**
     * Returns true if a structure does not overlap with other structures currently on the map.
     * Should be primarily used for rooms - not hallways. Hallways can and should
     * probably overlap.
     */
    boolean doesNotOverlap(Structure s) {
        for (Structure s2 : existingRooms) {
            for (Position coordinate : s.coordinates) {
                if (s2.floorCoordinates.contains(coordinate)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns true if a hallway is connected to another hall way.
     */
    boolean isConnectedToAHallway(Hallway h) {
        for (Hallway h2 : existingHallways) {
            if (sharesFloorTile(h, h2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper method for isConnectedToAHallway.
     * Checks whether one hallway is connected to another hallway by seeing if
     * they will share a floor tile.
     */
    private boolean sharesFloorTile(Hallway h, Hallway h2) {
        for (Position floorCoord : h.floorCoordinates) {
            if (h2.floorCoordinates.contains(floorCoord)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Connects a given structure with a randomly sized hallway.
     */
    private Hallway connectWithRandomHallway(Structure s, Random r) {
        int randomCornerIndex = RandomUtils.uniform(r, 4);
        if (makeHorizontal % 2 == 0) {
            randomCornerIndex = 2;
        }
        makeHorizontal += 1;
        int x, y;
        Hallway connectingHallway;
        Position connectingPoint;
        switch (randomCornerIndex) {
            case 0:
                x = s.structureCorners[0].x + (s.getWidth() / 2);
                y = s.structureCorners[0].y;
                connectingPoint = new Position(x, y);
                connectingHallway = new Hallway(this.world, connectingPoint, "vertical", r);
                connectingHallway.adjustToRoom(connectingPoint, 0);
                return connectingHallway;
            case 1:
                x = s.structureCorners[1].x + (s.getWidth() / 2);
                y = s.structureCorners[1].y;
                connectingPoint = new Position(x, y);
                connectingHallway = new Hallway(this.world, connectingPoint, "vertical", r);
                connectingHallway.adjustToRoom(connectingPoint, 1);
                return connectingHallway;
            case 2:
                x = s.structureCorners[2].x - 1;
                y = s.structureCorners[2].y - (s.getHeight() / 2);
                connectingPoint = new Position(x, y);
                return new Hallway(this.world, connectingPoint, "horizontal", r);
            case 3:
                x = s.structureCorners[1].x;
                y = s.structureCorners[1].y - (s.getHeight() / 2);
                connectingPoint = new Position(x, y);
                connectingHallway = new Hallway(this.world, connectingPoint, "horizontal", r);
                connectingHallway.adjustToRoom(connectingPoint, 3);
                return connectingHallway;
            default:
                return null;
        }
    }

    /**
     * Extracts number from String input and returns the number.
     * The number will be the seed for the random generator.
     */
    static long extractNumber(String input) {
        String seed = "";
        char[] inputCharArray = input.toCharArray();
        for (char c : inputCharArray) {
            if (Character.isDigit(c)) {
                seed = seed + c;
            }
        }
        if (seed.equals("")) {
            return 24234234234L;
        }
        return Long.parseLong(seed);
    }

}
