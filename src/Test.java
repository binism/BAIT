import java.lang.annotation.Repeatable;
import java.util.Random;

import core.ArcadeMachine;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 04/10/13
 * Time: 16:29
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Test
{

    public static void main(String[] args)
    {
        //----binism
        //boolean visuals = true; // set to false if you don't want to see the game
        int seed = new Random().nextInt(); // seed for random
        String sampleRandomController = "controllers.Astar.Agent";
        ArcadeMachine.runOneGame("examples/gridphysics/bait.txt", "examples/gridphysics/bait_lvl0.txt", true, sampleRandomController, null, seed, false);
        //----binism----
        //ArcadeMachine.playOneGame( "examples/gridphysics/bait.txt", "examples/gridphysics/bait_lvl0.txt", null, new Random().nextInt());
    }
}
