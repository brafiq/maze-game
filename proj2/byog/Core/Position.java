package byog.Core;

import java.io.Serializable;

/**
 * Establishes the position (corner) of a structure in the world.
 *
 * @author Kevin Nguyen and Borhan Rafiq
 */
public class Position implements Serializable {
    int x;
    int y;
    private static final long serialVersionUID = 798734234L;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    /**
     * Checks whether two positions on the map are equal.
     * This will mainly be used for handling overlaps.
     */
    public boolean equals(Object obj) {
        return this.x == ((Position) obj).x && this.y == ((Position) obj).y;
    }

    @Override
    public int hashCode() {
        return 0;
    }

}
