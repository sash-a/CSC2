import java.awt.*;

public class Score
{
    private int missedWords;
    private int caughtWords;
    private int gameScore;

    Score()
    {
        missedWords = 0;
        caughtWords = 0;
        gameScore = 0;
    }

    // all getters and setters must be synchronized

    public int getMissed()
    {
        return missedWords;
    }

    public int getCaught()
    {
        return caughtWords;
    }

    public int getTotal()
    {
        return (missedWords + caughtWords);
    }

    public int getScore()
    {
        return gameScore;
    }

    public synchronized void missedWord(Color colour)
    {
        if (colour.equals(Color.red))
        {
            caughtWords++;
            gameScore++; // give one point for missing red word
        }
        else
        {
            missedWords++;
        }

        GameApp.gameManager.updateLabels();
    }

    public synchronized void caughtWord(int length, Color colour)
    {
        int scoreMod = 1;
        if (colour.equals(Color.green)) scoreMod = 2;

        if (colour.equals(Color.red))
            gameScore = Math.max(0, gameScore - length); // Set score to zero rather than negative

        caughtWords++;
        gameScore += length * scoreMod;
        GameApp.gameManager.updateLabels();
    }

    public void resetScore()
    {
        caughtWords = 0;
        missedWords = 0;
        gameScore = 0;
        GameApp.gameManager.updateLabels();
    }
}
