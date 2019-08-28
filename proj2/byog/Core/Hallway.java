package byog.Core;

import byog.TileEngine.TETile;

import java.io.Serializable;
import java.util.Random;

/**
 * A hallway is a very specific structure.
 * The reason this structure has its own class is because the width or height
 * of this structure must be set to some specific value (2).
 *
 * @author Kevin Nguyen and Borhan Rafiq
 */
public class Hallway extends Structure implements Serializable {

    private String orientation;
    private Structure connectedRoom;
    private static final long serialVersionUID = 4549823474L;

    public Hallway(TETile[][] world, Position startPosition, String orientation, Random r) {
        if (orientation.equals("vertical")) {
            this.height = RandomUtils.uniform(r, 6, WorldGenerator.WORLD_HEIGHT - 5);
            this.width = 2;
        } else {
            this.height = 2;
            this.width = RandomUtils.uniform(r, 6, WorldGenerator.WORLD_WIDTH / 3);
        }
        this.orientation = orientation;
        this.world = world;

        establishCorners(startPosition);
        correctOutOfBounds();
        establishCoordinates();
        updateStartPlaceToFill();
        establishFloorCoordinates();
    }

    @Override
    /**
     * Constructs the hallway.
     * Reason of overriding: Decided to have a separate deque for existing hallways.
     */
    void constructStructure() {
        if (structureOutOfBounds()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        //Fill in four edges of the Structure and store the corner points
        this.fillEdge(0, false, 1);
        this.fillEdge(1, true, 1);
        this.fillEdge(2, false, -1);
        this.fillEdge(3, true, -1);
        this.fillStructure();
    }

    /**
     * Establishes the room that the hallway is connected to.
     * However, if the room has been detached from the hallway for some
     * reason, this method reattaches the two.
     */
    void connectToRoom(Structure s, Random r) {
        this.connectedRoom = s;
        int adjustment;
        if (this.detached(s)) {
            Position targetPos = this.getFirstOrLastFloorPosition(r);
            Position adjustTargetPos;
            //Handle all right edge horizontal hallway cases
            //Possibly creating an L-shaped hallway
            if (s.isPseudoHallway()) {
                adjustment = 1;
            } else {
                if (this.orientation.equals("vertical")) {
                    adjustment = RandomUtils.uniform(r, 1, (s.getWidth() / 2) + 1);
                } else {
                    adjustment = RandomUtils.uniform(r, 1, (s.getHeight() / 2) + 1);
                }
            }
            if (this.orientation.equals("vertical")) {
                adjustTargetPos = new Position(targetPos.x - adjustment, targetPos.y);
            } else {
                adjustTargetPos = new Position(targetPos.x, targetPos.y - adjustment);
            }
            //Reestablish the room to attach its hallway
            s.establishCorners(adjustTargetPos);
            s.correctOutOfBounds();
            s.updateStartPlaceToFill();
            s.floorCoordinates.clear();
            s.establishFloorCoordinates();
            s.coordinates.clear();
            s.establishCoordinates();
        }
    }

    /**
     * Helper method for connectToRoom.
     * Returns true if hallway's connected room has been detached.
     */
    private boolean detached(Structure s) {
        for (Position floorCoord : this.floorCoordinates) {
            if (s.floorCoordinates.contains(floorCoord)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Helper method for connectToRoom method.
     * Randomly returns first or last floor position in the hallway's floor
     * coordinate deque.
     */
    private Position getFirstOrLastFloorPosition(Random r) {
        boolean decision = RandomUtils.bernoulli(r, 0.50);
        if (decision) {
            return this.floorCoordinates.getFirst();
        }
        return this.floorCoordinates.getLast();
    }

    /**
     * Adjusts a hallway to its associated room.
     *
     * @param rp - Associated room's given position.
     * @param c  - Specified corner of the room (possible arguments: 0, 1, 3)
     */
    void adjustToRoom(Position rp, int c) {
        if (c == 0) {
            structureCorners[0].y = rp.y - this.height + 1;

        } else if (c == 1) {
            structureCorners[0].y = rp.y - 2;
        } else {
            structureCorners[0].x = rp.x - this.width + 1;
        }
        establishCorners(structureCorners[0]);
        correctOutOfBounds();
        updateStartPlaceToFill();
        coordinates.clear();
        establishCoordinates();
        floorCoordinates.clear();
        establishFloorCoordinates();
    }

}
