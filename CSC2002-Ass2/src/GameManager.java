
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;


import java.util.*;
import java.util.concurrent.ExecutorService;
//model is separate from the view.

public class GameManager
{
    //shared variables
    int noWords = 4;
    int totalWords;
    private int droppedWords = 0;
    private volatile int difficulty = 1;

    int frameX = 1000;
    int frameY = 600;
    int yLimit = 480;

    private String playerName = "Player 1"; // default high score name

    static WordDictionary dict = new WordDictionary(); //use default dictionary, to read from file eventually

    WordRecord[] words;
    volatile boolean isGameRunning; // true if there is a live game running no matter if it is paused
    volatile boolean isGamePaused;
    Score score = new Score();

    WordPanel wordPanel;
    private JLabel caughtLbl;
    private JLabel missedLbl;
    private JLabel scrLbl;
    private JTextField txtEntry;

    ExecutorService executor;

    static final String HIGH_SCORE_FILE = "high scores.txt";

    synchronized int getDroppedWords()
    {
        return droppedWords;
    }

    synchronized void incrementDroppedWords()
    {
        this.droppedWords++;
    }

    void updateLabels()
    {
        scrLbl.setText("Score: " + score.getScore());
        missedLbl.setText("Missed: " + score.getMissed());
        caughtLbl.setText("Caught: " + score.getCaught());
    }

    private JTextField createTextField()
    {
        final JTextField txtField = new JTextField("", 20);

        txtField.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                String text = txtField.getText();
                for (WordRecord word : words)
                {
                    if (word.matchWord(text))
                    {
                        score.caughtWord(text.length(), word.getColour());

                        if (droppedWords != totalWords)
                        {
                            word.resetWord();
                        }
                        else
                            word.killWord();

                        break;
                    }
                }
                txtField.setText("");
                txtField.requestFocus();
            }
        });
        return txtField;
    }

    private JButton createStartButton(JTextField txtField)
    {
        JButton startBtn = new JButton("Start");
        startBtn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (isGameRunning)
                {
                    int scr = endGame(false); // only want to write score to file on full game completion
                    JOptionPane.showMessageDialog(null,
                            "Ending so soon? Oh...you clearly don't like my game!\n" +
                                    "You scored: " + scr);
                    startBtn.setText("Start");
                }
                else
                {
                    startGame();
                    startBtn.setText("Restart");
                }
            }
        });
        return startBtn;
    }

    private JButton createEndButton()
    {
        JButton endBtn = new JButton("Pause");
        // add the listener to the jbutton to handle the "pressed" event
        endBtn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                isGamePaused = !isGamePaused;
                if (isGamePaused)
                    endBtn.setText("Un-pause");
                else
                    endBtn.setText("Pause");
            }
        });
        return endBtn;
    }

    private JButton createQuitButton(JFrame frame)
    {
        JButton quitBtn = new JButton("Quit");
        quitBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                executor.shutdown();
                endGame(false);
                System.out.println("Shutting down");
                while (!executor.isTerminated())
                {
                    // wait
                    System.out.print(".");
                }
                frame.dispose();
                System.exit(0);
            }
        });
        return quitBtn;
    }

    JButton createHighScoreBtn()
    {
        JButton highScoreBtn = new JButton("High scores");
        highScoreBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                createHighScoreScreen();
            }
        });

        return highScoreBtn;
    }

    JButton createPlayerNameBtn()
    {

        JButton highScoreBtn = new JButton("Change player name");
        highScoreBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                playerName = JOptionPane.showInputDialog("Input your player name");
            }
        });

        return highScoreBtn;
    }

    void createHighScoreScreen()
    {
        JFrame frame = new JFrame("High Score");
        frame.setSize(frameX / 6, frameY / 2);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        Map<Integer, String> scores = readScores();
        listModel.addElement("Score    Player name");
        for (Integer score : scores.keySet())
        {
            listModel.addElement(score + "    " + scores.get(score));
        }

        JScrollPane scrollPane = new JScrollPane(new JList<>(listModel));
        Panel mainPnl = new Panel();
        mainPnl.setLayout(new GridLayout(1, 1));
        mainPnl.add(scrollPane, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);  // Center window on screen.
        frame.add(mainPnl); // Add contents to window
        frame.setContentPane(mainPnl);
        frame.setVisible(true);
    }

    void onDifficutlyChanged(JSlider difficultySld, JLabel difficultyLbl)
    {
        difficultySld.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                difficulty = difficultySld.getValue();
                difficultyLbl.setText(difficulty + "");
                modifyByDifficulty();
            }
        });
    }

    void modifyByDifficulty()
    {
        if (difficulty > 2)
            WordRecord.FALLING_SPEED_MODIFIER = difficulty / 2;
        else
            WordRecord.FALLING_SPEED_MODIFIER = 1;
    }

    void setupGUI(int frameX, int frameY, int yLimit)
    {
        // Frame init and dimensions
        JFrame frame = new JFrame("WordGame");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(frameX, frameY);

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setSize(frameX, frameY);

        // Word panel
        wordPanel = new WordPanel(words, yLimit);
        wordPanel.setSize(frameX, yLimit + 100);
        mainPanel.add(wordPanel);

        //Text panel
        JPanel txtPanel = new JPanel();
        txtPanel.setLayout(new BoxLayout(txtPanel, BoxLayout.LINE_AXIS));
        caughtLbl = new JLabel("Caught: " + score.getCaught() + "\t\t");
        missedLbl = new JLabel("Missed: " + score.getMissed() + "\t\t");
        scrLbl = new JLabel("Score: " + score.getScore() + "\t\t");

        txtPanel.add(caughtLbl);
        txtPanel.add(missedLbl);
        txtPanel.add(scrLbl);

        txtEntry = createTextField();
        txtPanel.add(txtEntry);
        txtPanel.setMaximumSize(txtPanel.getPreferredSize());
        mainPanel.add(txtPanel);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));

        JButton startBtn = createStartButton(txtEntry);
        JButton endBtn = createEndButton();
        JButton quitBtn = createQuitButton(frame);
        JButton highScoreBtn = createHighScoreBtn();
        JButton playerNameBtn = createPlayerNameBtn();

        buttonPanel.add(startBtn);
        buttonPanel.add(endBtn);
        buttonPanel.add(quitBtn);
        buttonPanel.add(highScoreBtn);
        buttonPanel.add(playerNameBtn);

        //creating difficulty slider
        JSlider slider = new JSlider();
        JLabel difficultyLbl = new JLabel("Difficulty:");
        JLabel difficultyValLbl = new JLabel("0");
        onDifficutlyChanged(slider, difficultyValLbl);

        slider.setMaximum(10);
        slider.setMinimum(1);
        slider.setValue(1);

        buttonPanel.add(difficultyLbl);
        buttonPanel.add(slider);
        buttonPanel.add(difficultyValLbl);

        mainPanel.add(buttonPanel);

        frame.setLocationRelativeTo(null);  // Center window on screen.
        frame.add(mainPanel); // Add contents to window
        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }


    String[] getDictFromFile(String filename)
    {
        String[] dictStr = null;
        try
        {
            Scanner dictReader = new Scanner(new FileInputStream(filename));
            int dictLength = dictReader.nextInt();

            dictStr = new String[dictLength];
            for (int i = 0; i < dictLength; i++)
            {
                dictStr[i] = new String(dictReader.next());
            }
            dictReader.close();
        }
        catch (IOException e)
        {
            System.err.println("Problem reading file " + filename + " default dictionary will be used");
        }
        return dictStr;
    }

    private void writeScore(int scr)
    {
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(HIGH_SCORE_FILE, true));
            writer.write(scr + "," + playerName);
            writer.newLine();
            writer.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private Map<Integer, String> readScores()
    {
        Map<Integer, String> scores = new TreeMap<>(Collections.reverseOrder());

        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(HIGH_SCORE_FILE));
            String line = reader.readLine();

            while (line != null) // file end should always be a blank string
            {
                scores.put(Integer.parseInt(line.split(",")[0]), line.split(",")[1]);
                line = reader.readLine();
            }

        }
        catch (NumberFormatException nfe)
        {
            System.out.println("The high scores file has been corrupted with words instead of letters, " +
                    "please delete it to fix the problem");
            nfe.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return scores;
    }

    int endGame(boolean writeScore)
    {
        txtEntry.setText("");

        isGameRunning = false;
        for (WordRecord word : words) word.resetWord();

        int scr = score.getScore();
        if (writeScore) writeScore(scr);
        score.resetScore();

        droppedWords = 0;
        return scr;
    }

    void startGame()
    {
        isGameRunning = true;
        isGamePaused = false;
        droppedWords = noWords; // initialize the no of dropped words to the total amount of words initially dropping
        txtEntry.requestFocus();  //return focus to the text entry field
    }

}
