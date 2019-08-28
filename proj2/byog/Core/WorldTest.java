package byog.Core;

import static org.junit.Assert.*;

import byog.TileEngine.TETile;

import org.junit.Test;

import java.util.Arrays;

public class WorldTest {

    @Test
    public void wTest() {
        Game game = new Game();
        TETile[][] world1 = game.playWithInputString("n5777067884965724171swwaw");
        TETile[][] world2 = game.playWithInputString("n5777067884965724171swwaw");
        assertTrue(Arrays.deepEquals(world1, world2));
    }

    /*@Test
    public void extractNumTest() {
        String test = "n5197880843569031643:q";
        long actual = WorldGenerator.extractNumber(test);
        long expected = 5197880843569031643L;
        System.out.println(actual);
        System.out.println(expected);

        assertEquals(expected, actual);
    }*/

}
