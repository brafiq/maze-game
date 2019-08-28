package byog.lab5;
import org.junit.Test;
import static org.junit.Assert.*;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {

    private static final int WIDTH = 60;
    private static final int HEIGHT = 40;
    private static final int SEED = 100;
    private static final Random RANDOM = new Random(SEED);

    /** Generates the Settlers of Catan board. */
    public static void createCatanBoard(TETile[][] world, Position p, int s) {
        Position left = new Position(p.x - s - (s - 1), p.y + s  );
        Position right = new Position(p.x + s + (s - 1), p.y + s);
        Position outerLeft = new Position(left.x - s -  (s - 1), left.y + s);
        Position outerRight = new Position(right.x + s + (s - 1), right.y + s);
        addHexagons(world, outerLeft, s, 3);
        addHexagons(world, left, s, 4);
        addHexagons(world, p, s, 5);
        addHexagons(world, right, s, 4);
        addHexagons(world, outerRight, s, 3);
    }

    /** Adds N number of stacked hexagons onto the map. */
    private static void addHexagons(TETile[][] world, Position p, int s, int N) {
        boolean run = true;
        try {
            while (run && N > 0) {
                addHexagon(world, p, s);
                updatePosition(p, -1, 0);
                N -= 1;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return;
        }
    }

    /** Adds a hexagon into the world. */
    private static void addHexagon(TETile[][] world, Position p, int s) {
        int height = s * 2;
        int halfWay = height/2 - 1;
        TETile t = randomTileGenerator();
        //Fill in the rows up to the middle
        for (int c = 0; c < halfWay; c += 1) {
            addRow(world, t, s, p);
            updatePosition(p, -1, 1);
            s += 2;
        }
        //Add the two middle rows of equal length
        for (int c = 0; c < 2; c += 1) {
            addRow(world, t, s, p);
            updatePosition(p, 0, 1);
        }
        //Fill in the final half of the hexagon
        updatePosition(p, 1, 0);
        s -= 2;
        for (int c = 0; c < halfWay; c += 1) {
            addRow(world, t, s, p);
            updatePosition(p, 1, 1);
            s -= 2;
        }
    }

    /** Randomly generates a tile. */
    private static TETile randomTileGenerator() {
        int tileNum = RANDOM.nextInt(3);
        switch (tileNum) {
            case 0 : return Tileset.WATER;
            case 1 : return Tileset.FLOWER;
            case 2 : return Tileset.SAND;
            case 3 : return Tileset.TREE;
            default : return Tileset.NOTHING;
        }
    }

    /**
     * Adds a row to the hexagon and updates the position for the next row.
     */
    private static void addRow(TETile[][] world, TETile t, int length, Position p) {
        int startPoint = p.x;
        for (int c = 0; c < length; c += 1) {
            world[startPoint][p.y] = t;
            startPoint += 1;
        }
    }

    /**
     * Updates the position. Used for correct row positioning for
     * hexagon building and correct alignment of adjacent hexagons.
     */
    private static void updatePosition(Position p, int editX, int editY) {
        p.x = p.x + editX;
        p.y = p.y + editY;
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        createCatanBoard(world, new Position(WIDTH/2, (HEIGHT/2) - 15), 2);
        // draws the world to the screen
        ter.renderFrame(world);
    }

}
