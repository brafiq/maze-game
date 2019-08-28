package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Random;

/**
 * Structure class that represents rooms on the map.
 *
 * @author Kevin Nguyen and Borhan Rafiq
 */
public class Structure implements Serializable {

    int width;
    int height;
    private boolean pseudoHallway = false;
    Position startSpaceToFill;
    TETile[][] world;
    Position[] structureCorners = new Position[4];
    ArrayDeque<Position> coordinates = new ArrayDeque<>();
    //static ArrayDeque<Structure> existingRooms = new ArrayDeque<>();
    ArrayDeque<Position> floorCoordinates = new ArrayDeque<>();
    private static final long serialVersionUID = 45498234798734234L;
    static int numOfPseudoHallways = 0;

    public Structure() {

    }

    /**
     * Constructs a structure onto the given world. However, the structure
     * is not drawn at this point.
     */
    public Structure(TETile[][] world, Position startPosition, Random r) {
        this.width = RandomUtils.uniform(r, 4, 10);
        this.height = RandomUtils.uniform(r, 4, 10);
        convertToHallwayIfLarge(r);
        this.world = world;
        establishCorners(startPosition);
        correctOutOfBounds();
        establishCoordinates();
        updateStartPlaceToFill();
        establishFloorCoordinates();
    }

    /**
     * Converts the room into a hallway if either of its
     * width or height hits maximum length of 8.
     */
    private void convertToHallwayIfLarge(Random r) {
        if (numOfPseudoHallways <= 7) {
            if (this.height == 9) {
                this.height = RandomUtils.uniform(r, 4, 6);
                this.width = 2;
            } else if (this.width == 9) {
                this.width = RandomUtils.uniform(r, 4, 6);
                this.height = 2;
            }
            this.pseudoHallway = true;
            numOfPseudoHallways += 1;
        }
    }

    /**
     * Returns true if room has been converted to hallway.
     */
    public boolean isPseudoHallway() {
        return this.pseudoHallway;
    }

    /**
     * Returns width of structure.
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Returns height of structure.
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Corrects structure during initialization. Makes structure less prone to
     * being out of bounds.
     */
    void correctOutOfBounds() {
        Position startingPoint = this.structureCorners[0];
        Position beforePossibleChange = new Position(startingPoint.x, startingPoint.y);
        int xCoordSpanRight = startingPoint.x + this.width;
        int yCoordSpanUp = startingPoint.y + this.height;

        if (xCoordSpanRight > WorldGenerator.WORLD_WIDTH - 1) {
            startingPoint.x -= xCoordSpanRight - (WorldGenerator.WORLD_WIDTH - 1);
        }
        if (yCoordSpanUp > WorldGenerator.WORLD_HEIGHT - 1) {
            startingPoint.y -= yCoordSpanUp - (WorldGenerator.WORLD_HEIGHT - 1);
        }
        if (startingPoint.y < 0) {
            startingPoint.y = 0;
        }
        if (startingPoint.x < 0) {
            startingPoint.x = 0;
        }
        if (!(beforePossibleChange.equals(startingPoint))) {
            establishCorners(startingPoint);
        }
    }

    /**
     * Updates startSpaceToFill field in case a structure is ever coordinately
     * adjusted.
     */
    void updateStartPlaceToFill() {
        this.startSpaceToFill = new Position(structureCorners[0].x + 1, structureCorners[0].y + 1);
    }

    /**
     * Draws the structure onto the world.
     */
    void constructStructure() {
        if (structureOutOfBounds()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        this.fillEdge(0, false, 1);
        this.fillEdge(1, true, 1);
        this.fillEdge(2, false, -1);
        this.fillEdge(3, true, -1);
        this.fillStructure();
    }

    /**
     * Checks if a structure does not overlap one existing structure.
     */
    boolean doesNotOverlapOther(Structure s) {
        for (Position coordinate : this.coordinates) {
            if (s.floorCoordinates.contains(coordinate)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Stores all of the coordinates of a structure in a deque.
     */
    void establishCoordinates() {
        coordinates.clear();
        for (int i = 0; i < width; i += 1) {
            for (int j = 0; j < height; j += 1) {
                int x = structureCorners[0].x + i;
                int y = this.structureCorners[0].y + j;
                Position coordinate = new Position(x, y);
                coordinates.add(coordinate);
            }
        }
    }

    /**
     * Stores the corners of a structure in an array.
     *
     * @param startPosition - The corner in which the structure will begin its construction.
     */
    void establishCorners(Position startPosition) {
        int startX = startPosition.x;
        int startY = startPosition.y;
        this.structureCorners[0] = startPosition;
        this.structureCorners[1] = new Position(startX, startY + this.height);
        this.structureCorners[2] = new Position(startX + this.width, startY + this.height);
        this.structureCorners[3] = new Position(startX + this.width, startY);
    }

    /**
     * Stores the floor coordinates of a structure in a deque.
     */
    void establishFloorCoordinates() {
        for (int i = 0; i < this.width - 1; i += 1) {
            for (int j = 0; j < this.height - 1; j += 1) {
                int x = this.startSpaceToFill.x + i;
                int y = this.startSpaceToFill.y + j;
                floorCoordinates.add(new Position(x, y));
            }
        }
    }

    /**
     * Fills the edges and stores the corners of the structure.
     *
     * @param i    - the index at which the last tile position will be stored in StructureCorner
     * @param x    - If true, increment the x var. Else, increment y var.
     * @param step - incrementing x or y var according to programmer's specification
     */
    void fillEdge(int i, boolean x, int step) {
        Position reachCorner = new Position(structureCorners[i].x, structureCorners[i].y);
        if (x) {
            for (int c = 0; c < this.width; c += 1) {
                TETile tileToFill = this.world[reachCorner.x][reachCorner.y];
                if (!(tileToFill.equals(Player.PLAYERIMG) || tileToFill.equals(Tileset.FLOOR))) {
                    this.world[reachCorner.x][reachCorner.y] = Tileset.WALL;
                }
                reachCorner.x += step;
            }
        } else {
            for (int c = 0; c < this.height; c += 1) {
                TETile tileToFill = this.world[reachCorner.x][reachCorner.y];
                if (!(tileToFill.equals(Player.PLAYERIMG) || tileToFill.equals(Tileset.FLOOR))) {
                    this.world[reachCorner.x][reachCorner.y] = Tileset.WALL;
                }
                reachCorner.y += step;
            }
        }
    }

    /**
     * Checks whether the structure will be out of bounds; this method does not correct
     * for out of bounds.
     */
    boolean structureOutOfBounds() {
        Position startingPoint = this.structureCorners[0];
        int xCoordSpanRight = startingPoint.x + this.width;
        int yCoordSpanUp = startingPoint.y + this.height;
        return xCoordSpanRight > Game.WIDTH - 1 || yCoordSpanUp > Game.HEIGHT - 1;
    }

    /**
     * Fills the space in the Structure with floor tiles.
     */
    void fillStructure() {
        for (int i = 0; i < this.width - 1; i += 1) {
            for (int j = 0; j < this.height - 1; j += 1) {
                TETile tileToFill = world[startSpaceToFill.x + i][startSpaceToFill.y + j];
                if (!(tileToFill.equals(Player.PLAYERIMG) || tileToFill.equals(Tileset.FLOOR))) {
                    world[startSpaceToFill.x + i][startSpaceToFill.y + j] = Tileset.FLOOR;
                }
            }
        }
    }

    /**
     * Retrieves a structure's first or last floor coordinate.
     * Will mainly used for placing the character in the starter room.
     */
    static Position fstLstFloorCoordinate(Structure s, Random r) {
        int decision = RandomUtils.uniform(r, 0, 2);
        switch (decision) {
            case 0 :
                return s.floorCoordinates.getFirst();
            case 1 :
                return s.floorCoordinates.getLast();
            default :
                return s.floorCoordinates.getLast();
        }
    }

}
