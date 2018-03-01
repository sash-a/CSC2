import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WordRecord implements Runnable
{
    private String text;
    private int x;
    private int y;
    private int maxY;
    static volatile int FALLING_SPEED_MODIFIER = 1;
    private int fallingSpeed;
    private static int maxWait = 1500;
    private static int minWait = 100;
    private Color colour;

    public static WordDictionary dict;

    WordRecord()
    {
        text = "";
        x = 0;
        y = 0;
        maxY = 300;
        fallingSpeed = (int) (Math.random() * (maxWait - minWait)) / 150 + 5;
        setRandomColour();
    }

    WordRecord(String text)
    {
        this();
        this.text = text;
    }

    WordRecord(String text, int x, int maxY)
    {
        this(text);
        this.x = x;
        this.maxY = maxY;
    }

    // all getters and setters must be synchronized
    public synchronized void setY(int y)
    {
        if (y > maxY)
        {
            y = maxY;
        }
        this.y = y;
    }

    synchronized void setRandomColour()
    {
        double rand = Math.random();
        if (rand < 0.15)
            colour = Color.red;
        else if (rand > .85)
            colour = Color.green;
        else
            colour = Color.blue;
    }

    public synchronized void setX(int x)
    {
        this.x = x;
    }

    public synchronized void setWord(String text)
    {
        this.text = text;
    }

    public synchronized String getWord()
    {
        return text;
    }

    public synchronized int getX()
    {
        return x;
    }

    public synchronized int getY()
    {
        return y;
    }

    public synchronized Color getColour()
    {
        return colour;
    }

    public synchronized int getSpeed()
    {
        return fallingSpeed;
    }

    public synchronized void setPos(int x, int y)
    {
        setY(y);
        setX(x);
    }

    public synchronized void resetPos()
    {
        setY(0);
    }

    public synchronized void resetWord()
    {
        resetPos();
        setRandomColour();
        text = dict.getNewWord();
        fallingSpeed = (int) (Math.random() * (maxWait - minWait)) / 150 + 5;
        GameApp.gameManager.incrementDroppedWords(); // each time word is reset it should drop a new one
    }

    public synchronized boolean matchWord(String typedText)
    {
        // check not equal to blank as that is what the words are set to when they should not fall down
        if (!typedText.equals("") && typedText.equals(this.text))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Decrease the y value of this word
     */
    public synchronized void drop()
    {
        setY(y + fallingSpeed * FALLING_SPEED_MODIFIER);
    }

    /**
     * Check if a word has hit the ground
     * @return true if a word has hit the ground
     */
    public boolean hasHitGround()
    {
        return y == maxY;
    }

    /**
     * Called only when a word is not supposed to drop as the max number of words has already dropped
     * Resets the position of a word and sets the falling speed to 0
     */
    synchronized void killWord()
        {
        resetPos();
        setRandomColour();
        text = "";
        fallingSpeed = 0;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run()
    {
        ActionListener listener = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (GameApp.gameManager.isGameRunning && !GameApp.gameManager.isGamePaused) drop();

                if (hasHitGround())
                {
                    GameApp.gameManager.score.missedWord(getColour());
                    if (GameApp.gameManager.getDroppedWords() != GameApp.gameManager.totalWords)
                        resetWord();
                    else
                        killWord();
                }

                // end game
                if (GameApp.gameManager.score.getTotal() == GameApp.gameManager.totalWords)
                {
                    int scr = GameApp.gameManager.endGame(true);
                    JOptionPane.showMessageDialog(null,
                            "Game Over!\n" +
                                    "You scored: " + scr);
                }
                GameApp.gameManager.wordPanel.repaint();
            }
        };

        Timer timer = new Timer(100, listener);
        timer.start();
    }
}

