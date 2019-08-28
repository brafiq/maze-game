package byog.Core;

import java.awt.Color;
import java.io.Serializable;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
//import edu.princeton.cs.introcs.StdAudio;

/**
 * This class represents the player that the user controls on the map.
 *
 * @author Kevin Nguyen and Borhan Rafiq
 */
public class Player implements Serializable {

    Position pos;
    TETile[][] world;
    WorldGenerator generatedWorld;
    private static final String P1DESCR = "player - that's you, fool!";
    private static final String P2DESCR = "player2 - that's you, baby!";
    static final TETile PLAYERIMG = new TETile('@', Color.pink, Color.black, P1DESCR);
    static final TETile PLAYERIMG2 = new TETile('@', Color.white, Color.black, P2DESCR);
    private static final long serialVersionUID = 734234L;
    TETile playerTile;

    /**
     * Constructs a player object.
     *
     * @param one Specifies player img to be used. 1 for pink, else white.
     */
    public Player(WorldGenerator wg, Position p, int one) {
        this.pos = p;
        this.generatedWorld = wg;
        this.world = generatedWorld.world;
        if (one == 1) {
            this.world[p.x][p.y] = PLAYERIMG;
            playerTile = PLAYERIMG;
        } else {
            this.world[p.x][p.y] = PLAYERIMG2;
            playerTile = PLAYERIMG2;
        }
    }

    /**
     * Moves the character in the world up.
     */
    void moveUp() {
        int potentialMoveUp = pos.y + 1;
        TETile tileToMoveTo = world[pos.x][potentialMoveUp];
        if (!(tileToMoveTo.equals(Tileset.FLOOR) || tileToMoveTo.equals(Tileset.FLOWER))) {
            //StdAudio.play("/emerald_bump.wav");
            return;
        } else {
            if (world[pos.x][potentialMoveUp].equals(Tileset.FLOWER)) {
                generatedWorld.numFlowers -= 1;
                //StdAudio.play("/collect.wav");
            }
            world[pos.x][pos.y] = Tileset.FLOOR;
            world[pos.x][potentialMoveUp] = playerTile;
            pos.y = potentialMoveUp;
        }
    }

    /**
     * Moves the character in the world down.
     */
    void moveDown() {
        int potentialMoveDown = pos.y - 1;
        TETile tileToMoveTo = world[pos.x][potentialMoveDown];
        if (!(tileToMoveTo.equals(Tileset.FLOOR) || tileToMoveTo.equals(Tileset.FLOWER))) {
            //StdAudio.play("/emerald_bump.wav");
            return;
        } else {
            if (world[pos.x][potentialMoveDown].equals(Tileset.FLOWER)) {
                generatedWorld.numFlowers -= 1;
                //StdAudio.play("/collect.wav");
            }
            world[pos.x][pos.y] = Tileset.FLOOR;
            world[pos.x][potentialMoveDown] = playerTile;
            pos.y = potentialMoveDown;
        }
    }

    /**
     * Moves the character in the world left.
     */
    void moveLeft() {
        int potentialMoveLeft = pos.x - 1;
        TETile tileToMoveTo = world[potentialMoveLeft][pos.y];
        if (!(tileToMoveTo.equals(Tileset.FLOOR) || tileToMoveTo.equals(Tileset.FLOWER))) {
            //StdAudio.play("/emerald_bump.wav");
            return;
        } else {
            if (world[potentialMoveLeft][pos.y].equals(Tileset.FLOWER)) {
                generatedWorld.numFlowers -= 1;
                //StdAudio.play("/collect.wav");
            }
            world[pos.x][pos.y] = Tileset.FLOOR;
            world[potentialMoveLeft][pos.y] = playerTile;
            pos.x = potentialMoveLeft;
        }
    }

    /**
     * Moves the character in the world right.
     */
    void moveRight() {
        int potentialMoveRight = pos.x + 1;
        TETile tileToMoveTo = world[potentialMoveRight][pos.y];
        if (!(tileToMoveTo.equals(Tileset.FLOOR) || tileToMoveTo.equals(Tileset.FLOWER))) {
            //StdAudio.play("/emerald_bump.wav");
            return;
        } else {
            if (world[potentialMoveRight][pos.y].equals(Tileset.FLOWER)) {
                generatedWorld.numFlowers -= 1;
                //StdAudio.play("/collect.wav");
            }
            world[pos.x][pos.y] = Tileset.FLOOR;
            world[potentialMoveRight][pos.y] = playerTile;
            pos.x = potentialMoveRight;
        }
    }

    /**
     * Changes the position of the player to the tile to which the user clicks,
     * only if the clicked tile is a floor tile.
     */
    void clickChangePos(Position newPos) {
        if (this.world[newPos.x][newPos.y].equals(Tileset.FLOOR)) {
            //StdAudio.play("/teleport.wav");
            this.world[this.pos.x][this.pos.y] = Tileset.FLOOR;
            this.world[newPos.x][newPos.y] = playerTile;
            this.pos = newPos;
        }
    }

}
