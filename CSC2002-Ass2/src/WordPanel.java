import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.*;

public class WordPanel extends JPanel
{
    private WordRecord[] words;
    private int maxY;

    public void paintComponent(Graphics g)
    {
        int width = getWidth();
        int height = getHeight();
        g.clearRect(0, 0, width, height);
        g.setColor(Color.red);
        g.fillRect(0, maxY - 10, width, height);

        g.setFont(new Font("Helvetica", Font.PLAIN, 26));

        for (WordRecord word : words)
        {
            g.setColor(word.getColour());
            g.drawString(word.getWord(), word.getX(), word.getY());
        }
    }

    WordPanel(WordRecord[] words, int maxY)
    {
        this.words = words; // will this work? YES!
        GameApp.gameManager.isGameRunning = false;
        this.maxY = maxY;
    }
}


