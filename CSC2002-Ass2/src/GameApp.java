import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CSC2002-Ass2
 * Sasha
 * 2017/09/12.
 */
public class GameApp
{
    static GameManager gameManager = new GameManager();

    public static void main(String[] args)
    {
        //deal with command line arguments
        gameManager.totalWords = Integer.parseInt(args[0]);  //total words to fall
        gameManager.noWords = Integer.parseInt(args[1]); // total words falling at any point
        gameManager.executor = Executors.newCachedThreadPool();

        assert (gameManager.totalWords >= gameManager.noWords); // this could be done more neatly

        String[] tmpDict = gameManager.getDictFromFile(args[2]); //file of words
        if (tmpDict != null)
        {
            GameManager.dict = new WordDictionary(tmpDict);
        }

        WordRecord.dict = GameManager.dict; //set the class dictionary for the words.

        // or somehow change array size to scale with difficulty.
        gameManager.words = new WordRecord[gameManager.noWords];
        gameManager.setupGUI(gameManager.frameX, gameManager.frameY, gameManager.yLimit);
        //Start WordPanel thread - for redrawing animation
        int x_inc = (int) gameManager.frameX / gameManager.noWords;

        //initialize shared array of current words
        for (int i = 0; i < gameManager.noWords; i++)
        {
            gameManager.words[i] = new WordRecord(GameManager.dict.getNewWord(), i * x_inc, gameManager.yLimit);
            gameManager.executor.execute(gameManager.words[i]);
        }
    }
}
